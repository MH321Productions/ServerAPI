package io.github.mh321productions.serverapi.module.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.SubPlugin;
import io.github.mh321productions.serverapi.util.logging.PixelLogHandler;

/**
 * Eine Wrapper-Klasse für das {@link LogModule Logging-Modul}
 * @author 321Productions
 */
public class LogWrapper {
	
	public SubPlugin sub;
	public Logger log;
	public PixelLogHandler handler;
	public File logFile;
	
	public LogWrapper(Main plugin, SubPlugin sub) {
		this.sub = sub;
		log = sub.getLogger();
		logFile = plugin.filesystem.getLogFile(sub);
	}
	
	/**
	 * Initialisiert das Umleiten des Loggers
	 * @return Ob das Umleiten erfolgreich war
	 */
	public boolean init() {
		log.info("Starte API Logger");
		
		try {
			handler = new PixelLogHandler(logFile);
			log.addHandler(handler);
			log.info("API Logger gestartet");
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			log.log(Level.SEVERE, "Der API Logger konnte nicht gestartet werden:", e);
			return false;
		}
		
		return true;
	}
	
	public void end() {
		log.info("Stoppe API Logger");
		
		handler.close();
		log.removeHandler(handler);
		
		log.info("API Logger gestoppt");
	}
}
