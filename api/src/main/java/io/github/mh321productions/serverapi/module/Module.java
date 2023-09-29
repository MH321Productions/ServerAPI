package io.github.mh321productions.serverapi.module;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.api.ServerAPI;
import io.github.mh321productions.serverapi.api.SubPlugin;

/**
 * Die Superklasse aller API-Module. <br/>
 * Der Zugriff erfolgt über {@link ServerAPI#getModule(ModuleType)}
 * @author 321Productions
 */
public abstract class Module {
	
	private static final String startFormat = "Lade %s-Modul";
	private static final String startFormatSuccess = "%s-Modul erfolgreich geladen";
	private static final String startFormatFailure = "%s-Modul konnte nicht geladen werden";
	private static final String stopFormat = "Stoppe %s-Modul";
	private static final String stopFormatSuccess = "%s-Modul erfolgreich gestoppt";
	
	public ModuleType<? extends Module> type;
	public boolean initialized;
	protected Main plugin;
	protected APIImplementation api;
	protected HashMap<SubPlugin, ModuleStopFunction> loadedSubPlugins = new HashMap<>();
	protected Logger log;
	
	protected Module(ModuleType<? extends Module> type, Main plugin, APIImplementation api) {
		this.type = type;
		this.plugin = plugin;
		this.api = api;
		log = plugin.getLogger();
		
		//Initialisieren
		log.info(String.format(startFormat, type.name));
		
		initialized = init();
		if (initialized) log.info(String.format(startFormatSuccess, type.name));
		else log.severe(String.format(startFormatFailure, type.name));
	}
	
	/**
	 * Internes initialisieren des Moduls
	 */
	protected abstract boolean init();
	
	/**
	 * Interne Stoppfunktion des Moduls (siehe {@link Module#stop()})
	 */
	protected abstract void stopIntern();
	
	/**
	 * Registriert ein Sub-Plugin für das Modul.
	 * @param sub Das Sub-Plugin
	 * @param func Die Stoppfunktion (kann null sein)
	 * @return Ob das Registrieren erfolgreich war (wenn nicht, wird das Sub-Plugin wieder entfernt)
	 */
	public abstract boolean registerSubPlugin(@Nonnull SubPlugin sub, @Nullable ModuleStopFunction func);
	
	/**
	 * Entfernt ein geladenes Sub-Plugin (wenn es nicht vorher registriert ist, passiert nichts)
	 * @param sub Das Sub-Plugin
	 */
	public abstract void unregisterSubPlugin(@Nullable SubPlugin sub);
	
	/**
	 * Fügt das Sub-Plugin intern zur Liste der geladenen Sub-Plugins hinzu.
	 * @param sub Das Sub-Plugin
	 * @param func Die Stoppfunktion
	 * @return Ob das Hinzufügen erfolgreich war
	 */
	protected boolean addIntern(@Nonnull SubPlugin sub, @Nullable ModuleStopFunction func) {
        loadedSubPlugins.put(sub, EmptyFunctions.checkNullModule(func));
		api.addSub(sub);
		
		return true;
	}
	
	/**
	 * Entfernt des Sub-Plugin aus der internen Liste.
	 * @param sub Das Sub-Plugin
	 */
	protected void removeIntern(SubPlugin sub) {
		if (loadedSubPlugins.containsKey(sub)) {
			loadedSubPlugins.remove(sub);
			api.removeSub(sub);
		}
	}
	
	/**
	 * Entlädt das Modul in folgender Reihenfolge: <ol>
	 * <li>Alle registrierten Sub-Plugins werden benachrichtigt, <br/>
	 * dass das Modul entladen wird (siehe {@link ModuleStopFunction})</li>
	 * <li>Die {@link Module#stopIntern() interne} Stoppfunktion wird aufgerufen, <br/>
	 * um das Modul artgerecht zu entladen</li>
	 * <li>Alle geladenen Sub-Plugins werden entfernt (erst hier, <br/>
	 * damit das Modul noch mit den Sub-Plugins interagieren kann)</li>
	 * </ol>
	 */
	public void stop() {
		log.info(String.format(stopFormat, type.name));
		
		for (ModuleStopFunction func: loadedSubPlugins.values()) func.onStop();
		stopIntern();
		loadedSubPlugins.clear();
		
		log.info(String.format(stopFormatSuccess, type.name));
	}
	
	/**
	 * Gibt zurück, ob ein Sub-Plugin im Modul registriert ist. Diese Methode kann von Subklassen erweitert werden.
	 * @param sub Das abzufragende Sub-Plugin
	 * @return Ob das Plugin registriert ist
	 */
	public boolean isSubPluginLoaded(SubPlugin sub) {
		return loadedSubPlugins.containsKey(sub);
	}
}
