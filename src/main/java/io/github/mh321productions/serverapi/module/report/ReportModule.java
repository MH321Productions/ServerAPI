package io.github.mh321productions.serverapi.module.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nullable;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.api.ServerSubPlugin;
import io.github.mh321productions.serverapi.api.SubPlugin;
import io.github.mh321productions.serverapi.module.Module;
import io.github.mh321productions.serverapi.module.ModuleStopFunction;
import io.github.mh321productions.serverapi.module.ModuleType;
import io.github.mh321productions.serverapi.module.report.command.ReportCommand;
import io.github.mh321productions.serverapi.module.report.command.ReportsCommand;
import io.github.mh321productions.serverapi.util.PixelIO;
import io.github.mh321productions.serverapi.util.message.MessagePrefix;
import io.github.mh321productions.serverapi.util.message.MessagePrefix.PrefixFormat;

/**
 * Mit dem Report-Modul können Spieler andere Spieler mithilfe des <br/>
 * Befehls "/report &ltSpieler&gt &ltGrund&gt" reporten. Jedes Sub-Plugin kann eigene <br/>
 * Gründe definieren (siehe {@link ReportType})
 * @author 321Productions
 *
 */
public class ReportModule extends Module {
	
	public static final MessagePrefix prefix = new MessagePrefix("§cReport", PrefixFormat.Main);
	
	private ArrayList<ReportType> reasons;
	
	private ArrayList<ReportEntry> entries;
	private ArrayList<ReportEntry> invalidEntries;
	private File reportFile;
	private ReportCommand cmd;
	private ReportsCommand manageCmd;

	public ReportModule(Main plugin, APIImplementation api) {
		super(ModuleType.Report, plugin, api);
	}

	@Override
	protected boolean init() {
		reasons = new ArrayList<>(ReportType.standardReasons);
		entries = new ArrayList<>();
		invalidEntries = new ArrayList<>();
		
		log.info("Lade Reportdatei");
		reportFile = new File(plugin.filesystem.getModuleFolder(ModuleType.Report), "reports.dat");
		
		cmd = new ReportCommand(plugin, api, this);
		manageCmd = new ReportsCommand(plugin, api, this);
		plugin.cmd.registerCommand("report", cmd);
		plugin.cmd.registerCommand("reportlist", manageCmd);
		
		if (reportFile.exists()) {
			try {
				List<String> lines = PixelIO.readLines(reportFile);
				for (String l: lines) {
					if (l.isEmpty()) continue;
					try {
						newEntry(new ReportEntry(l, this));
					} catch (IllegalArgumentException e) {
						log.severe("Konnte Report-Eintrag nicht laden: \"" + l + "\"");
					}
				}
			} catch (IOException e) {
				log.log(Level.SEVERE, "Konnte Report-Datei nicht laden: ", e);
				return false;
			}
		}
		
		log.info(entries.size() + " Einträge geladen, " + invalidEntries.size() + " davon bisher unvollständig");
		
		return true;
	}

	@Override
	protected void stopIntern() {
		log.info("Speichere Reportdatei");
		ArrayList<String> lines = new ArrayList<>(entries.size());
		for (ReportEntry en: entries) lines.add(en.toSaveString());
		
		try {
			PixelIO.writeLines(reportFile, lines);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Konnte Reportdatei nicht abspeichern: ", e);
		}
		log.info("Reportdatei erfolgreich gespeichert");
	}

	@Override
	public boolean registerSubPlugin(SubPlugin sub, ModuleStopFunction func) {
		return addIntern(sub, func);
	}

	@Override
	public void unregisterSubPlugin(@Nullable SubPlugin sub) {
		removeIntern(sub);
	}
	
	/**
	 * Fügt eigene Reportgründe hinzu, die vom eigenen Sub-Plugin definiert wurden
	 * @param sub Das Sub-Plugin, das die Gründe hinzufügt
	 * @param reasons Die eigenen Gründe
	 */
	public void addReasons(SubPlugin sub, List<ReportType> reasons) {
		if (sub == null || sub instanceof ServerSubPlugin) return;
		
		int counter = 0;
		log.info("Das Sub-Plugin " + sub.getName() + " fügt folgende Reportgründe hinzu:");
		for (ReportType type: reasons) {
			if (type.getPlugin() == sub) {
				this.reasons.add(type);
				log.info(type.getName() + " (" + type.getInternalName() + ")");
				counter++;
			}
		}
		log.info(counter + " Reportgründe hinzugefügt");
		
		checkNewReasons();
	}
	
	/**
	 * Entfernt die eigenen Reportgründe, die vorher hinzugefügt wurden. Dies entfernt aber nicht die vorhandenen <br/>
	 * Reports
	 * @param sub Das Sub-Plugin
	 * @param reasons Die zu entfernenden Gründe
	 */
	public void removeReasons(SubPlugin sub, List<ReportType> reasons) {
		if (sub == null || sub instanceof ServerSubPlugin) return;
		
		int counter = 0;
		log.info("Das Sub-Plugin " + sub.getName() + " entfernt folgende Reportgründe:");
		for (ReportType type: reasons) {
			if (type.getPlugin() == sub) {
				this.reasons.remove(type);
				log.info(type.getName() + " (" + type.getInternalName() + ")");
				counter++;
			}
		}
		log.info(counter + " Reportgründe entfernt");
	}
	
	/**
	 * Gibt alle Reportgründe, die man über das angegebene Sub-Plugin nutzen kann, zurück.
	 * @param sub Das Sub-Plugin, dessen Gründe genutzt werden sollen (plus die allgemeinen)
	 * @return Die Gründe
	 */
	public List<ReportType> getReasons(SubPlugin sub) {
		if (sub instanceof ServerSubPlugin || sub == null) return ReportType.standardReasons;
		
		ArrayList<ReportType> ausgabe = new ArrayList<>();
		for (ReportType type: reasons) if (type.getPlugin() == sub) ausgabe.add(type);
		ausgabe.addAll(ReportType.standardReasons);
		
		return ausgabe;
	}
	
	/**
	 * Sucht einen Reportgrund aus den geladenen. Falls der Grund noch nicht geladen sein sollte, wird ein <br/>
	 * unvollständiger/ungültiger Typ zurückgegeben.
	 * @param internalName Der interne (einzigartige) Name
	 * @return Der geladene Typ, oder ein unvollständiger (zu prüfen mit {@link ReportType#isValid()})
	 */
	public ReportType getInvalidType(String internalName) {
		for (ReportType type: reasons) {
			if (type.getInternalName().equalsIgnoreCase(internalName)) return type;
		}
		return new ReportType(internalName);
	}
	
	/**
	 * Sucht einen Reportgrund aus den geladenen. Falls der Grund noch nicht geladen sein sollte, wird null <br/>
	 * zurückgegeben.
	 * @return Der geladene Typ, oder null
	 * @param displayName Der Anzeigename
	 */
	public ReportType getType(String displayName) {
		for (ReportType type: reasons) {
			if (type.getName().equals(displayName)) {
				//log.info("Typ \"" + type.getInternalName() + "\" gefunden");
				return type;
			}
		}
		//log.info("Kein Typ gefunden");
		return null;
	}
	
	/**
	 * Registriert einen neuen Report-Eintrag. Dieser muss vorher erstellt werden.
	 * @param entry Der erstellte Eintrag
	 */
	public void newEntry(ReportEntry entry) {
		entries.add(entry);
		if (!entry.type.isValid()) invalidEntries.add(entry);
		
		log.info("Neuer Report registriert: " + entry);
		
		//TODO: In Stats schreiben
	}
	
	/**
	 * Sanktioniert einen vorhandenen Report-Eintrag. Das Strafmaß liegt im Ermessen des Mods/Sups (nicht <br/>
	 * mein Problem ;D). Der Bestrafte Report wird dem reporteten Spieler zudem in die Stats geschrieben. <br/>
	 * Wenn der Eintrag null oder nicht vorher registriert ({@link #newEntry(ReportEntry)}) ist, passiert nichts.
	 * @param entry Der zu sanktionierende Eintrag.
	 */
	public void sanctionEntry(@Nullable ReportEntry entry) {
		if (entries.remove(entry)) {
			log.info("Report als bestraft markiert: " + entry);
			//TODO: Sanktionierten Report in Stats einfügen
		}
	}
	
	/**
	 * Entfernt einen Report-Eintrag, ohne zu bestrafen. Wenn der Eintrag null oder nicht vorher registriert <br/>
	 * ({@link #newEntry(ReportEntry)}) ist, passiert nichts.
	 * @param entry Der zu löschende Eintrag
	 */
	public void freeEntry(@Nullable ReportEntry entry) {
		entries.remove(entry);
		log.info("Report gelöscht: " + entry);
	}
	
	/**
	 * Gibt den Report-Eintrag mit dieser id zurück. Wenn keiner gefunden wurde, wird null zurückgegeben.
	 * @param id Die ID des Eintrages ({@link ReportEntry#id})
	 * @return Den gesuchten Eintrag oder null
	 */
	public ReportEntry getEntry(UUID id) {
		for (ReportEntry e: entries) if (e.id.equals(id)) return e;
		
		return null;
	}
	
	/**
	 * Gibt die eingegengenen Einträge zurück.
	 * @return Alle geladenen eingegangenen Einträge
	 */
	public List<ReportEntry> getEntries() {
		return entries;
	}
	
	/**
	 * Intern: Testet, wenn neue Gründe registriert wurden, ob unvollständige Einträge aufgelöst werden können
	 */
	private void checkNewReasons() {
		ArrayList<ReportEntry> good = new ArrayList<>();
		for (ReportEntry e: invalidEntries) {
			for (ReportType type: reasons) {
				if (e.type.getInternalName().equalsIgnoreCase(type.getInternalName())) { //Interne Namen stimmen überein
					e.setType(type);
					good.add(e);
				}
			}
		}
		
		invalidEntries.removeAll(good);
	}
}
