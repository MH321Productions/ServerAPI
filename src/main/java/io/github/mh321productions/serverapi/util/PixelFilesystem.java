package io.github.mh321productions.serverapi.util;

import java.io.File;
import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.SubPlugin;
import io.github.mh321productions.serverapi.module.ModuleType;

public class PixelFilesystem {
	
	private Main plugin;
	public File dataFolder, baseFolder;
	private File[] moduleFolder = new File[ModuleType.values.length];
	
	public PixelFilesystem(Main plugin) {
		this.plugin = plugin;
		
		plugin.getLogger().info("Starte Filesystem");
		
		init();
	}
	
	private void init() {
		dataFolder = plugin.getDataFolder();
		baseFolder = plugin.getDataFolder().getParentFile().getParentFile(); //Zwei Ebenen über dem Pluginordner
		
		//Modulordner erstellen
		File temp;
		for (ModuleType<?> module: ModuleType.values) {
			temp = new File(dataFolder, module.folderName);
			moduleFolder[module.folderIndex] = temp;
			if (!temp.exists()) temp.mkdir();
		}
	}
	
	/**
	 * Gibt den Dateiordner eines Moduls zurück
	 * @param module Das Modul, dessen Ordner abgefragt werden soll
	 * @return Der Ordner des Moduls
	 */
	public File getModuleFolder(ModuleType<?> module) {
		return moduleFolder[module.folderIndex];
	}
	
	/**
	 * Gibt die Logdatei eines Sub-Plugins zurück, um über das Logging-Modul reinzuschreiben
	 * @param sub Das im Logging-Modul registrierte Sub-Plugin
	 * @return Die Logdatei
	 */
	public File getLogFile(SubPlugin sub) {
		return new File(getModuleFolder(ModuleType.Logging), sub.getName() + ".log");
	}
}