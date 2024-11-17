package io.github.mh321productions.serverapi.module.report

import io.github.mh321productions.serverapi.api.ServerSubPlugin
import io.github.mh321productions.serverapi.api.SubPlugin

/**
 * Diese Klasse bezeichnet den Reportgrund. Jedes Plugin kann eigene Gründe definieren, <br></br>
 * die aber nur ausgewählt werden können, wenn man sich zur Zeit im Hoheitsbereich <br></br>
 * dieses Plugins befindet (siehe [SubPlugin.isPlayerInGame]). Die API <br></br>
 * definiert bereits allgemeine Gründe wie z.B. Hacking. Diese müssen (und dürfen) nicht <br></br>
 * neu definiert werden.
 * @author 321Productions
 */
class ReportType {
    val plugin: SubPlugin?
    val internalName: String
    val name: String
        get() = field.ifEmpty { internalName }

    val pluginName: String
        get() = plugin?.name ?: "?"

    /**
     * Gibt zurück, ob der Grund vollständig/gültig ist
     * @return Ob der Grund gültig ist
     */
    val isValid: Boolean
        get() = plugin != null || name.isNotEmpty()

    /**
     * Erstellt einen Reportgrund
     * @param plugin Das Plugin, das die Kategorie erstellt
     * @param name Der Anzeigename des Grundes, den Sups/Mods beim Abfragen sehen
     * @param internalName Der interne Name, der abgespeichert wird. Dieser muss einzigartig und folgendermaßen aufgebaut sein: `<plugin>.<reason>` (ohne Leerzeichen, alles klein)
     */
    constructor(plugin: SubPlugin, name: String, internalName: String) {
        this.plugin = plugin
        this.name = name
        this.internalName = internalName
    }

    /**
     * Intern: Erstellt einen unvollständigen Reportgrund. Wird benutzt, wenn ein Grund geladen wird, <br></br>
     * das zugehörige Sub-Plugin diesen aber nicht registriert hat.
     * @param internalName Der interne Name
     */
    internal constructor(internalName: String) {
        plugin = null
        name = ""
        this.internalName = internalName
    }

    companion object {
        @JvmField
        val Hacking = ReportType(ServerSubPlugin.instance, "Hacking", "server.hacking")
        @JvmField
        val Chat = ReportType(ServerSubPlugin.instance, "Chat", "server.chat")
        @JvmField
        val Bugusing = ReportType(ServerSubPlugin.instance, "Bugusing", "server.bugusing")
        @JvmField
        val Skin = ReportType(ServerSubPlugin.instance, "Skin", "server.skin")
        @JvmField
        val Username = ReportType(ServerSubPlugin.instance, "Username", "server.name")

        @JvmField
		val standardReasons: List<ReportType> = listOf(Bugusing, Chat, Hacking, Skin, Username)
    }
}
