package io.github.mh321productions.serverapi.module;

import io.github.mh321productions.serverapi.module.chat.ChatModule;
import io.github.mh321productions.serverapi.module.config.ConfigModule;
import io.github.mh321productions.serverapi.module.log.LogModule;
import io.github.mh321productions.serverapi.module.nick.NickModule;
import io.github.mh321productions.serverapi.module.npc.NPCModule;
import io.github.mh321productions.serverapi.module.realtime.RealtimeModule;
import io.github.mh321productions.serverapi.module.report.ReportModule;

/**
 * Alle verfügbaren Modularten werden hier gelistet
 * @author 321Productions
 */
public final class ModuleType<T extends Module> {
	
	/**
	 * Das Logging-Modul erstellt für jedes Sub-Plugin ein eigenes Logfile
	 * @see LogModule
	 */
	public static final ModuleType<LogModule> Logging = new ModuleType<>("Logging", "logs", 0);
	
	/**
	 * Mit dem Report-Modul können Spieler andere Spieler reporten
	 * @see ReportModule
	 */
	public static final ModuleType<ReportModule> Report = new ModuleType<>("Report", "report", 1);
	
	/**
	 * Mit dem Chat-Modul können Sub-Plugins eigene Minichats erstellen
	 * @see ChatModule
	 */
	public static final ModuleType<ChatModule> Chat = new ModuleType<>("Chat", "chat", 2);
	
	/**
	 * Mit dem Config-Modul können Sub-Plugins verschiedene Konfigurationen für jeden Spieler speichern
	 * @see ConfigModule
	 */
	public static final ModuleType<ConfigModule> Config = new ModuleType<>("Config", "config", 3);
	
	/**
	 * Mit dem Realtime-Modul haben registrierte Welten immer die aktuelle Serverzeit
	 * @see RealtimeModule
	 */
	public static final ModuleType<RealtimeModule> Realtime = new ModuleType<>("Realtime", "realtime", 4);

	/**
	 * Mit dem NPC-Modul können Plugins NPCs spawnen und verwalten
	 * @see NPCModule
	 */
	public static final ModuleType<NPCModule> NPC = new ModuleType<>("NPC", "npc", 5);

	public static final ModuleType<NickModule> Nick = new ModuleType<>("Nick", "nick", 6);
	
	/**
	 * Alle Modularten in einem Array
	 */
	public static final ModuleType<?>[] values = new ModuleType<?>[] {Logging, Report, Chat, Config, Realtime, NPC, Nick};
	
	/**
	 * Der Name des Typs (wird beim Logging benutzt)
	 */
	public final String name;
	public final String folderName;
	public final int folderIndex;
	
	private ModuleType(String name, String folderName, int folderIndex) {
		this.name = name;
		this.folderIndex = folderIndex;
		this.folderName = folderName;
	}
}
