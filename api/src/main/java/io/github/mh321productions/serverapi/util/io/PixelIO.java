package io.github.mh321productions.serverapi.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Eine Utility-Klasse, die Einige IO-Operationen zur Verfügung stellt
 * @author 321Productions
 *
 */
public final class PixelIO {

	/**
	 * Liest alle Zeilen aus einer Datei im UTF-8-Format aus
	 * @param file Die auszulesende Datei
	 * @return Eine Liste der Zeilen
	 * @throws IOException Wenn es einen Fehler beim Lesen gibt (es wird versucht, noch alles noch zu schließen)
	 */
	public static List<String> readLines(File file) throws IOException {
		BufferedReader reader = null;
		IOException ex = null;
		ArrayList<String> ausgabe = new ArrayList<>();
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			String line;
			
			while ((line = reader.readLine()) != null) {
				ausgabe.add(line);
			}
			
		} catch (IOException e) {
			ex = e;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					if (ex == null) ex = e;
				}
			}
		}
		
		if (ex != null) throw ex;
		
		return ausgabe;
	}
	
	/**
	 * Schreibt Zeilen in eine Datei im UTF-8-Format
	 * @param file Die Datei, in die geschrieben werden soll (wird automatisch erstellt)
	 * @param lines Die Zeilen, die geschrieben werden sollen
	 * @throws IOException Wenn es einen Fehler beim Schreiben gibt (es wird versucht, noch alles noch zu schließen)
	 */
	public static void writeLines(File file, List<String> lines) throws IOException {
		BufferedWriter writer = null;
		IOException ex = null;
		
		try {
			file.createNewFile();
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
			
			for (String l: lines) {
				writer.write(l);
				writer.newLine();
			}
		} catch (IOException e) {
			ex = e;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					if (ex == null) ex = e;
				}
			}
		}
		
		if (ex != null) throw ex;
	}
}
