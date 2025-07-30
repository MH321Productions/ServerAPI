package io.github.mh321productions.serverapi.module.report

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.ServerSubPlugin
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import io.github.mh321productions.serverapi.module.report.command.ReportCommand
import io.github.mh321productions.serverapi.module.report.command.ReportsCommand
import io.github.mh321productions.serverapi.util.io.PixelIO.readLines
import io.github.mh321productions.serverapi.util.io.PixelIO.writeLines
import io.github.mh321productions.serverapi.util.message.MessagePrefix
import io.github.mh321productions.serverapi.util.message.MessagePrefix.PrefixFormat
import java.io.File
import java.io.IOException
import java.util.*
import java.util.logging.Level

/**
 * Mit dem Report-Modul können Spieler andere Spieler mithilfe des <br></br>
 * Befehls `/report <Spieler> <Grund>` reporten. Jedes Sub-Plugin kann eigene <br></br>
 * Gründe definieren (siehe [ReportType])
 * @author 321Productions
 */
class ReportModule(plugin: Main, api: APIImplementation) : Module(ModuleType.Report, plugin, api) {

    private val reasons = mutableListOf<ReportType>()
    private val entries = mutableListOf<ReportEntry>()
    private val invalidEntries = mutableListOf<ReportEntry>()
    private lateinit var reportFile: File
    private lateinit var cmd: ReportCommand
    private lateinit var manageCmd: ReportsCommand

    override fun initIntern(): Boolean {
        reasons.addAll(ReportType.standardReasons)

        log.info("Lade Reportdatei")
        reportFile = File(main.filesystem.getModuleFolder(ModuleType.Report), "reports.dat")

        cmd = ReportCommand(main, api, this)
        manageCmd = ReportsCommand(main, api, this)
        main.cmd.registerCommand("report", cmd)
        main.cmd.registerCommand("reportlist", manageCmd)

        if (reportFile.exists()) {
            try {
                readLines(reportFile)
                    .filter { it.isNotBlank() }
                    .forEach {
                        try {
                            newEntry(ReportEntry(it, this), false)
                        } catch (e: IllegalArgumentException) {
                            log.severe("Konnte Report-Eintrag nicht laden: \"$it\"")
                        }
                    }
            } catch (e: IOException) {
                log.log(Level.SEVERE, "Konnte Report-Datei nicht laden: ", e)
                return false
            }
        }

        log.info("${entries.size} Einträge geladen, ${invalidEntries.size} davon bisher unvollständig")
        return true
    }

    override fun stopIntern() {
        log.info("Speichere Reportdatei")
        val lines = entries
            .map { it.toSaveString() }

        try {
            writeLines(reportFile, lines)
            log.info("Reportdatei erfolgreich gespeichert")
        } catch (e: IOException) {
            log.log(Level.SEVERE, "Konnte Reportdatei nicht abspeichern: ", e)
        }
    }

    override fun registerSubPlugin(sub: SubPlugin, func: () -> Unit) = addIntern(sub, func)
    override fun unregisterSubPlugin(sub: SubPlugin) = removeIntern(sub)

    /**
     * Fügt eigene Reportgründe hinzu, die vom eigenen Sub-Plugin definiert wurden
     * @param sub Das Sub-Plugin, das die Gründe hinzufügt
     * @param reasons Die eigenen Gründe
     */
    fun addReasons(sub: SubPlugin, reasons: List<ReportType>) {
        if (sub is ServerSubPlugin) return

        var counter = 0
        log.info("Das Sub-Plugin ${sub.name} fügt folgende Reportgründe hinzu:")
        reasons
            .filter { it.plugin == sub }
            .forEach {
                this.reasons.add(it)
                log.info("${it.name} (${it.internalName})")
                counter++
            }

        log.info("$counter Reportgründe hinzugefügt")

        checkNewReasons()
    }

    /**
     * Entfernt die eigenen Reportgründe, die vorher hinzugefügt wurden. Dies entfernt aber nicht die vorhandenen <br></br>
     * Reports
     * @param sub Das Sub-Plugin
     * @param reasons Die zu entfernenden Gründe
     */
    fun removeReasons(sub: SubPlugin, reasons: List<ReportType>) {
        if (sub is ServerSubPlugin) return

        var counter = 0
        log.info("Das Sub-Plugin ${sub.name} entfernt folgende Reportgründe:")
        reasons
            .filter { it.plugin == sub }
            .forEach {
                this.reasons.remove(it)
                log.info("${it.name} (${it.internalName})")
                counter++
            }

        log.info("$counter Reportgründe entfernt")
    }

    /**
     * Gibt alle Reportgründe, die man über das angegebene Sub-Plugin nutzen kann, zurück.
     * @param sub Das Sub-Plugin, dessen Gründe genutzt werden sollen (zusätzlich zu den allgemeinen), oder `null`
     * @return Die Gründe
     */
    fun getReasons(sub: SubPlugin?): List<ReportType> {
        if (sub == null || sub is ServerSubPlugin) return ReportType.standardReasons

        return reasons
            .filter { it.plugin == sub || ReportType.standardReasons.contains(it) }
    }

    /**
     * Sucht einen Reportgrund aus den geladenen. Falls der Grund noch nicht geladen sein sollte, wird ein
     * unvollständiger/ungültiger Typ zurückgegeben.
     * @param internalName Der interne (einzigartige) Name
     * @return Der geladene Typ, oder ein unvollständiger (zu prüfen mit [ReportType.isValid])
     */
    fun getInvalidType(internalName: String) = reasons.find { it.internalName.equals(internalName, ignoreCase = true) } ?: ReportType(internalName)

    /**
     * Sucht einen Reportgrund aus den geladenen. Falls der Grund noch nicht geladen sein sollte, wird `null`
     * zurückgegeben.
     * @return Der geladene Typ, oder `null`
     * @param displayName Der Anzeigename
     */
    fun getType(displayName: String) = reasons.find { it.name.equals(displayName, ignoreCase = true) }

    /**
     * Registriert einen neuen Report-Eintrag. Dieser muss vorher erstellt werden.
     * @param entry Der erstellte Eintrag
     */
    fun newEntry(entry: ReportEntry, newReport: Boolean = true) {
        entries.add(entry)
        if (!entry.type.isValid) invalidEntries.add(entry)

        if (newReport) log.info("Neuer Report registriert: $entry")
    }

    /**
     * Sanktioniert einen vorhandenen Report-Eintrag. Das Strafmaß liegt im Ermessen des Mods/Sups (nicht
     * mein Problem ;D). Der bestrafte Report wird dem reporteten Spieler zudem in die Stats geschrieben.
     * Wenn der Eintrag nicht vorher registriert ([newEntry]) ist, passiert nichts.
     * @param entry Der zu sanktionierende Eintrag.
     */
    fun sanctionEntry(entry: ReportEntry) {
        if (entries.remove(entry)) {
            log.info("Report als bestraft markiert: $entry")
            //TODO: Sanktionierten Report in Stats einfügen
        }
    }

    /**
     * Entfernt einen Report-Eintrag, ohne zu bestrafen. Wenn der Eintrag nicht vorher registriert ([newEntry]) ist,
     * passiert nichts.
     * @param entry Der zu löschende Eintrag
     */
    fun freeEntry(entry: ReportEntry) {
        entries.remove(entry)
        log.info("Report gelöscht: $entry")
    }

    /**
     * Gibt den Report-Eintrag mit dieser id zurück. Wenn keiner gefunden wurde, wird null zurückgegeben.
     * @param id Die ID des Eintrages ([ReportEntry.id])
     * @return Den gesuchten Eintrag oder null
     */
    fun getEntry(id: UUID) = entries.find { it.id == id }

    /**
     * Gibt die eingegengenen Einträge zurück.
     * @return Alle geladenen eingegangenen Einträge
     */
    fun getEntries() = entries.toList()

    /**
     * Intern: Testet, wenn neue Gründe registriert wurden, ob unvollständige Einträge aufgelöst werden können
     */
    private fun checkNewReasons() {
        val good = ArrayList<ReportEntry>()
        for (e in invalidEntries) {
            for (type in reasons) {
                if (e.type.internalName.equals(type.internalName, ignoreCase = true)) { //Interne Namen stimmen überein
                    e.setType(type)
                    good.add(e)
                }
            }
        }

        invalidEntries.removeAll(good)
    }

    companion object {
        @JvmField
		val prefix: MessagePrefix = MessagePrefix("§cReport", PrefixFormat.Main)
    }
}
