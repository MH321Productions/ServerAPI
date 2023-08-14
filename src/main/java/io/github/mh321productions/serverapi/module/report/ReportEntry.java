package io.github.mh321productions.serverapi.module.report;

import java.time.ZonedDateTime;
import java.util.UUID;

import io.github.mh321productions.serverapi.api.ServerSubPlugin;
import io.github.mh321productions.serverapi.api.SubPlugin;
import io.github.mh321productions.serverapi.util.StringFormatter;
import io.github.mh321productions.serverapi.util.StringReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

/**
 * Diese Klasse bezeichnet einen Report-Eintrag. Dieser wird von den Spielern <br/>
 * automatisch erstellt und kann von den Sups/Mods eingesehen werden. Diese <br/>
 * können den Eintrag entweder als "bestraft" oder "nicht bestraft" markieren. <br/>
 * Das Strafmaß liegt bei den Sups/Mods und ist nicht mein Problem ;D Sobald ein <br/>
 * Eintrag bearbeitet wurde, wird er in die Statistiken des Spielers einfließen.
 * @author 321Productions
 *
 */
public class ReportEntry {
	
	private static final HoverEvent listHover = new HoverEvent(Action.SHOW_TEXT, new Text("Klicken, um mehr anzuzeigen"));
	private static final HoverEvent namemcHover = new HoverEvent(Action.SHOW_TEXT, new Text("Klicken, um das NameMC-Profil anzuzeigen"));
	private static final HoverEvent sanctionHover = new HoverEvent(Action.SHOW_TEXT, new Text("Klicken, um den Report als \"bestraft\" zu markieren"));
	private static final HoverEvent freeHover = new HoverEvent(Action.SHOW_TEXT, new Text("Klicken, um den Report als \"nicht bestraft\" zu markieren"));
	
	private static final String namemcLink = "https://de.namemc.com/search?q=";
	private static final String sanctionCmd = "/reportlist sanction ";
	private static final String freeCmd = "/reportlist free ";
	
	private static final TextComponent taeterComp = new TextComponent("\n§7Reportet wurde§8: ");
	private static final TextComponent opferComp = new TextComponent("\n§7Von§8: ");
	private static final TextComponent headerFooterComp = new TextComponent("\n§8-------------------------------------------");
	
	/**
	 * Muss nicht der Weltname sein (siehe {@link SubPlugin#getWorldName(World)})
	 */
	public final String worldName;
	/**
	 * Die einzigartige ID des Eintrages
	 */
	public final UUID id;
	public final String subName;
	public ReportType type;
	public final ZonedDateTime time;
	public final UUID reportedPlayer, reporter;
	public final Location loc;
	
	private TextComponent title;
	private TextComponent zeitComp;
	private TextComponent reasonComp;
	private TextComponent locComp;
	private TextComponent worldComp;
	private TextComponent subComp;
	private TextComponent sanctionComp = new TextComponent("\n\n§4[Bestrafen] ");
	private TextComponent freeComp = new TextComponent("§2[Löschen]");
	private TextComponent[] showComps;
	private String logString;
	
	/**
	 * Intern: erstellt einen vorhandenen Eintrag aus einem gespeicherten Eintrag (z.B bei Reloads)
	 * @param entryLine Die Zeile aus dem ReportFile
	 * @param module Das Reportmodul
	 * @throws IllegalArgumentException Wenn die Zeile kein Report-Eintrag ist
	 */
	public ReportEntry(String entryLine, ReportModule module) throws IllegalArgumentException {
		String[] comp = entryLine.split(":");
		if (comp.length != 8) throw new IllegalArgumentException("Die Zeile \"" + entryLine + "\" ist kein gültiger Report-Eintrag");
		
		//Konstruieren
		id = UUID.fromString(comp[0]);
		type = module.getInvalidType(comp[1]);
		time = StringReader.readDateTime(comp[2]);
		reportedPlayer = UUID.fromString(comp[3]);
		reporter = UUID.fromString(comp[4]);
		loc = StringReader.readLocation(comp[5]);
		worldName = comp[6];
		subName = comp[7];

		//Testen, ob der Grund Valide ist
		if (!type.isValid());
		
		init();
	}
	
	/**
	 * Erstellt einen neuen Eintrag
	 * @param reported Der Spieler, der reportet wurde
	 * @param reporter Der Spieler, der Reportet hat
	 * @param type Der Reportgrund
	 */
	public ReportEntry(Player reported, Player reporter, ReportType type) {
		time = ZonedDateTime.now();
		reportedPlayer = reported.getUniqueId();
		this.reporter = reporter.getUniqueId();
		loc = reported.getLocation();
		this.type = type;
		id = UUID.randomUUID();
		
		//Sub-Plugin und Weltname anhand des Reporttyps identifizieren
		SubPlugin temp;
		if (type.getPlugin() == null) temp = new ServerSubPlugin(reported);
		else temp = type.getPlugin();
		worldName = temp.getWorldName(loc.getWorld());
		subName = temp.getName();
		
		init();
	}
	
	private void init() {
		title = new TextComponent("\n§cReport §b" + id.toString());
		zeitComp = new TextComponent("\n§7Zeitpunkt§8: §r" + StringFormatter.formatDateTime(time));
		reasonComp = new TextComponent("\n§7Grund§8: §4" + type.getName());
		locComp = new TextComponent("\n§7Position§8: §d" + StringFormatter.formatChatLocation(loc, false));
		worldComp = new TextComponent("\n§7Weltname§8: §3" + worldName);
		subComp = new TextComponent("\n§7Plugin§8: §3" + subName);
		sanctionComp = new TextComponent("\n\n§8[§c§lBestrafen§8] ");
		freeComp = new TextComponent("§8[§a§lLöschen§8]");
		sanctionComp.setHoverEvent(sanctionHover);
		freeComp.setHoverEvent(freeHover);
		sanctionComp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, sanctionCmd + id.toString()));
		freeComp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, freeCmd + id.toString()));
		
		showComps = new TextComponent[] {
				headerFooterComp, title,
				taeterComp, toNamemcString(reportedPlayer),
				opferComp, toNamemcString(reporter),
				zeitComp, reasonComp, locComp, worldComp,
				subComp, sanctionComp, freeComp, headerFooterComp
		};
		
		//Log String
		StringBuilder builder = new StringBuilder();
		OfflinePlayer taeter = Bukkit.getOfflinePlayer(reportedPlayer);
		OfflinePlayer opfer = Bukkit.getOfflinePlayer(reporter);
		
		builder.append(opfer.getName());
		builder.append(" -> ");
		builder.append(taeter.getName());
		builder.append(": ");
		builder.append(type.getInternalName());
		builder.append(", ");
		builder.append(StringFormatter.formatDateTime(time));
		
		logString = builder.toString();
	}
	
	public void setType(ReportType type) {
		this.type = type;
		reasonComp = new TextComponent("\nGrund: " + type.getName());
		showComps[7] = reasonComp;
	}
	
	public String toSaveString() {
		StringBuilder builder = new StringBuilder();
		
		//ID
		builder.append(id.toString());
		builder.append(':');
		
		//Reporttype
		builder.append(type.getInternalName());
		builder.append(':');
		
		//Zeitpunkt
		builder.append(StringFormatter.formatDateTimeSave(time));
		builder.append(':');
		
		//Spieler, der reportet wurde
		builder.append(reportedPlayer.toString());
		builder.append(':');
		
		//Spieler, der Reportet hat
		builder.append(reporter.toString());
		builder.append(':');
		
		//Location des reporteten Spielers
		builder.append(StringFormatter.formatLocation(loc));
		builder.append(':');
		
		//Weltname
		builder.append(worldName);
		builder.append(':');
		
		//Pluginname
		builder.append(subName);
		//builder.append(':');
		
		return builder.toString();
	}
	
	/**
	 * Formatiert den Eintrag in ein {@link TextComponent}, um diesen beim Aufrufen des Befehls /reportlist list [Seite] anzuzeigen.
	 * @return Ein Spigot Textkomponent
	 */
	public TextComponent toListString() {
		StringBuilder builder = new StringBuilder("§8[§7");
		
		//Zeit
		builder.append(StringFormatter.formatDateTime(time));
		builder.append("§8] [");
		
		//Spieler
		OfflinePlayer player = Bukkit.getOfflinePlayer(reportedPlayer);
		builder.append("§e");
		if (player == null) builder.append(reportedPlayer.toString());
		else builder.append(player.getName());
		builder.append("§8] [");
		
		//Grund
		builder.append("§4");
		builder.append(type.getName());
		builder.append("§8]\n\n");
		
		//Component
		TextComponent text = new TextComponent(builder.toString());
		ClickEvent click = new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/reportlist show " + id.toString());
		text.setHoverEvent(listHover);
		text.setClickEvent(click);
		
		return text;
	}
	
	/**
	 * Formatiert den Eintrag in {@link TextComponent}s, um diese beim Aufrufen des Befehls /reportlist show &ltid&gt anzuzeigen.
	 * @return Ein Array aus Textkomponenten
	 */
	public TextComponent[] toShowString() {
		return showComps;
	}
	
	/**
	 * Formatiert den Eintrag in einen String, der in den Log/die Konsole geschrieben werden kann. <br>
	 * Alternativ kann auch {@link #toString()} verwendet werden.
	 * @return Der formatierte String
	 */
	public String toLogString() {
		return logString;
	}
	
	@Override
	public String toString() {
		return logString;
	}
	
	/**
	 * Formatiert einen Spielernamen in ein {@link TextComponent}, das per Klick auf das NameMC-Profil des Spielers <br/>
	 * weiterleitet.
	 * @param id Die UUID des Spielers, dessen Profil abgefragt werden soll
	 * @return Das generierte {@link TextComponent}
	 */
	public static TextComponent toNamemcString(UUID id) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(id);
		TextComponent text = new TextComponent("§e" + player.getName());
		ClickEvent click = new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, namemcLink + id.toString());
		text.setClickEvent(click);
		text.setHoverEvent(namemcHover);
		
		return text;
	}
}
