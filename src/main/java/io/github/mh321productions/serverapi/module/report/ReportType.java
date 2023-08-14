package io.github.mh321productions.serverapi.module.report;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import io.github.mh321productions.serverapi.api.SubPlugin;
import org.bukkit.entity.Player;

/**
 * Diese Klasse bezeichnet den Reportgrund. Jedes Plugin kann eigene Gründe definieren, <br/>
 * die aber nur ausgewählt werden können, wenn man sich zur Zeit im Hohheitsbereich <br/>
 * dieses Plugins befindet (siehe {@link SubPlugin#isPlayerInGame(Player)}). Die API <br/>
 * definiert bereits allgemeine Gründe wie z.B Hacking. Diese müssen (und dürfen) nicht <br/>
 * neu definiert werden.
 * @author 321Productions
 *
 */
public final class ReportType {
	
	private SubPlugin plugin;
	private String name, internalName;
	
	/**
	 * Erstellt einen Reportgrund
	 * @param plugin Das Plugin, das die Kategorie erstellt
	 * @param name Der Anzeigename des Grundes, den Sups/Mods beim abfragen sehen
	 * @param internalName Der interne Name, der abgespeichert wird. Dieser muss einzigartig und folgendermaßen aufgebaut sein: &ltplugin&gt.&ltreason&gt (ohne Leerzeichen, alles klein)
	 */
	public ReportType(SubPlugin plugin, String name, String internalName) {
		this.plugin = plugin;
		this.name = name;
		this.internalName = internalName;
	}
	
	/**
	 * Intern: Erstellt einen unvollständigen Reportgrund. Wird benutzt, wenn ein Grund geladen wird, <br/>
	 * das zugehörige Sub-Plugin diesen aber nicht registriert hat.
	 * @param internalName Der interne Name
	 */
	public ReportType(String internalName) {
		plugin = null;
		name = "";
		this.internalName = internalName; 
	}
	
	public SubPlugin getPlugin() {
		return plugin;
	}
	
	public String getName() {
		if (name == null || name.isEmpty()) return internalName;
		return name;
	}
	
	public String getInternalName() {
		return internalName;
	}
	
	public String getPluginName() {
		if (plugin == null) return "?";
		else return plugin.getName();
	}
	
	/**
	 * Gibt zurück, ob der Grund vollständig/gültig ist
	 * @return Ob der Grund gültig ist
	 */
	public boolean isValid() {
		return plugin != null || !name.isEmpty();
	}
	
	public static final ReportType Hacking = new ReportType(null, "Hacking", "server.hacking");
	public static final ReportType Chat = new ReportType(null, "Chat", "server.chat");
	public static final ReportType Bugusing = new ReportType(null, "Bugusing", "server.bugusing");
	public static final ReportType Skin = new ReportType(null, "Skin", "server.skin");
	public static final ReportType Username = new ReportType(null, "Username", "server.name");
	
	public static final ArrayList<ReportType> standardReasons = Lists.newArrayList(
			ReportType.Bugusing, ReportType.Chat, ReportType.Hacking,
			ReportType.Skin, ReportType.Username
	);
}
