package io.github.mh321productions.serverapi.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.module.nick.NickModule;
import io.github.mh321productions.serverapi.module.npc.NPCModule;
import io.github.mh321productions.serverapi.util.CounterSet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import io.github.mh321productions.serverapi.module.Module;
import io.github.mh321productions.serverapi.module.ModuleType;
import io.github.mh321productions.serverapi.module.chat.ChatModule;
import io.github.mh321productions.serverapi.module.log.LogModule;
import io.github.mh321productions.serverapi.module.realtime.RealtimeModule;
import io.github.mh321productions.serverapi.module.report.ReportModule;
import io.github.mh321productions.serverapi.util.permission.PermissionHandler;

/**
 * Intern: Die Implementierung der {@link ServerAPI}
 * @author 321Productions
 *
 */
public class APIImplementation implements ServerAPI {
	
	private Main plugin;
	private ArrayList<Module> loadedModules = new ArrayList<>(ModuleType.values.length); //Wird automatisch mit der Anzahl der Module initialisiert
	private CounterSet<SubPlugin> loadedSubs = new CounterSet<>();
	private NickModule nickModule = null;
	
	public APIImplementation(Main plugin) {
		this.plugin = plugin;
		
		init();
	}
	
	private void init() {
		//API als RegisteredServiceProvider registrieren
		plugin.getServer().getServicesManager().register(ServerAPI.class, this, plugin, ServicePriority.Normal);
		plugin.getLogger().info("API gestartet und betriebsbereit");
		
		//TODO: Module starten
		loadedModules.add(new LogModule(plugin, this));
		loadedModules.add(new ReportModule(plugin, this));
		loadedModules.add(new ChatModule(plugin, this));
		loadedModules.add(new RealtimeModule(plugin, this));
		loadedModules.add(new NPCModule(plugin, this));
		nickModule = new NickModule(plugin, this);
		loadedModules.add(nickModule);
	}
	
	public void stop() {
		plugin.getLogger().info("Stoppe API");
		
		//Die Module werden in umgekehrter Reihenfolge entladen (das Logging-Modul zuletzt)
		Collections.reverse(loadedModules);
		for (Module mod: loadedModules) mod.stop();
		
		//API als RegisteredServiceProvider stoppen
		plugin.getServer().getServicesManager().unregister(ServerAPI.class, this);
		plugin.getLogger().info("API gestoppt");
	}

	@Override
	public List<Module> getLoadedModules() {
		return loadedModules;
	}

	@Override
	public boolean isModuleLoaded(ModuleType<?> type) {
		for (Module m: loadedModules) {
			if (m.type == type && m.initialized) return true;
		}
		
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Module> T getModule(ModuleType<T> type) {
		for (Module m: loadedModules) {
			if (m.type == type && m.initialized) return (T) m;
		}
		
		return null;
	}
	
	public void addSub(SubPlugin s) {
		loadedSubs.add(s);
	}
	
	public void removeSub(SubPlugin s) {
		loadedSubs.remove(s);
	}
	
	public CounterSet<SubPlugin> getLoadedSubs() {
		return loadedSubs;
	}
	
	public SubPlugin getControllingPlugin(Player player) {
		for (SubPlugin sub: loadedSubs) if (sub.isPlayerInGame(player)) {
			//plugin.getLogger().info(player.getName() + " wird vom Sub-Plugin \"" + sub.getName() + "\" kontrolliert");
			return sub;
		}
		
		//plugin.getLogger().info(player.getName() + " wird von keinem Sub-Plugin kontrolliert");
		return null;
	}

	@Override
	public PermissionHandler getPermissionHandler() {
		return plugin.perms;
	}

	@Override
	public boolean isPlayerNicked(Player player) {
		if (nickModule != null) return nickModule.isPlayerNicked(player);
		else return false;
	}

	@Override
	public String getNickname(Player player) {
		if (nickModule != null) return nickModule.getNickname(player);
		else return player.getName();
	}
}
