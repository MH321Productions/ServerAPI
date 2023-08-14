package io.github.mh321productions.serverapi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.module.ModuleType;

public class PixelLogManager {
	
	private Main plugin;
	private PixelLogHandler logHandler = null;
	private Logger log;
	
	public PixelLogManager(Main plugin) {
		this.plugin = plugin;
		log = plugin.getLogger();
		init();
	}
	
	private void init() {
		log.info("Starte internen Logger");
		try {
			logHandler = new PixelLogHandler(new File(plugin.filesystem.getModuleFolder(ModuleType.Logging), "API.log"));
			log.addHandler(logHandler);
			
			log.info("Interner Logger gestartet");
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "Konnte den internen Logger nicht starten", e);
		}
	}
	
	public void stop() {
		if (logHandler != null) {
			log.info("Stoppe internen Logger");
			
			log.removeHandler(logHandler);
			logHandler.close();
			
			log.info("Interner Logger gestoppt");	
		}
	}
}