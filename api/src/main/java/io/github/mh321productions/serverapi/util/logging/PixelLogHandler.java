package io.github.mh321productions.serverapi.util.logging;

import io.github.mh321productions.serverapi.util.formatting.StringFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class PixelLogHandler extends Handler {
	
	private static final String format = "%s %s %s %s"; //Datum/Zeit Level Message Exception
	
	private BufferedWriter writer = null;
	
	public PixelLogHandler(File logFile) throws FileNotFoundException {
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8));
	}

	@Override
	public void publish(LogRecord record) {
		if (writer != null) {
			String datum = StringFormatter.formatDateTime(record.getInstant());
			String level = record.getLevel().getName();
			String message = record.getMessage();
			String throwable = "";
			
			if (record.getThrown() != null) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				pw.println();
				record.getThrown().printStackTrace(pw);
				pw.close();
				throwable = sw.toString();
			}
			
			try {
				writer.write(String.format(format, datum, level, message, throwable));
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			flush();
		}
	}

	@Override
	public void flush() {
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws SecurityException {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
