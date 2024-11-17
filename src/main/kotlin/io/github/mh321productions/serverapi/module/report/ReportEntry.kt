package io.github.mh321productions.serverapi.module.report

import io.github.mh321productions.serverapi.api.ServerSubPlugin
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.util.formatting.StringFormatter.formatChatLocation
import io.github.mh321productions.serverapi.util.formatting.StringFormatter.formatDateTime
import io.github.mh321productions.serverapi.util.formatting.StringFormatter.formatDateTimeSave
import io.github.mh321productions.serverapi.util.formatting.StringFormatter.formatLocation
import io.github.mh321productions.serverapi.util.formatting.StringReader.readDateTime
import io.github.mh321productions.serverapi.util.formatting.StringReader.readLocation
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.ZonedDateTime
import java.util.*

/**
 * Diese Klasse bezeichnet einen Report-Eintrag. Dieser wird von den Spielern <br></br>
 * automatisch erstellt und kann von den Sups/Mods eingesehen werden. Diese <br></br>
 * können den Eintrag entweder als "bestraft" oder "nicht bestraft" markieren. <br></br>
 * Das Strafmaß liegt bei den Sups/Mods und ist nicht mein Problem ;D Sobald ein <br></br>
 * Eintrag bearbeitet wurde, wird er in die Statistiken des Spielers einfließen.
 * @author 321Productions
 */
class ReportEntry {
    /**
     * Muss nicht der Weltname sein (siehe [SubPlugin.getWorldName])
     */
    val worldName: String

    /**
     * Die einzigartige ID des Eintrages
     */
	val id: UUID
    val subName: String
    val time: ZonedDateTime
    val reportedPlayer: UUID
    val reporter: UUID
    val loc: Location
    var type: ReportType
        private set

    private lateinit var title: TextComponent
    private lateinit var zeitComp: TextComponent
    private lateinit var reasonComp: TextComponent
    private lateinit var locComp: TextComponent
    private lateinit var worldComp: TextComponent
    private lateinit var subComp: TextComponent
    private lateinit var showComps: Array<BaseComponent>
    private lateinit var logString: String

    private val sanctionComp = TextComponent("\n\n§4[Bestrafen] ")
    private val freeComp = TextComponent("§2[Löschen]")

    /**
     * Intern: erstellt einen vorhandenen Eintrag aus einem gespeicherten Eintrag (z.B bei Reloads)
     * @param entryLine Die Zeile aus dem ReportFile
     * @param module Das Reportmodul
     * @throws IllegalArgumentException Wenn die Zeile kein Report-Eintrag ist
     */
    internal constructor(entryLine: String, module: ReportModule) {
        val comp = entryLine.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        require(comp.size == 8) { "Die Zeile \"$entryLine\" ist kein gültiger Report-Eintrag" }


        //Konstruieren
        id = UUID.fromString(comp[0])
        type = module.getInvalidType(comp[1])
        time = readDateTime(comp[2])!!
        reportedPlayer = UUID.fromString(comp[3])
        reporter = UUID.fromString(comp[4])
        loc = readLocation(comp[5])!!
        worldName = comp[6]
        subName = comp[7]

        //Testen, ob der Grund Valide ist
        //if (!type.isValid);

        init()
    }

    /**
     * Erstellt einen neuen Eintrag
     * @param reported Der Spieler, der reportet wurde
     * @param reporter Der Spieler, der Reportet hat
     * @param type Der Reportgrund
     */
    constructor(reported: Player, reporter: Player, type: ReportType) {
        time = ZonedDateTime.now()
        reportedPlayer = reported.uniqueId
        this.reporter = reporter.uniqueId
        loc = reported.location
        this.type = type
        id = UUID.randomUUID()


        //Sub-Plugin und Weltname anhand des Reporttyps identifizieren
        val temp = if (type.plugin == null) ServerSubPlugin(reported)
        else type.plugin
        worldName = temp.getWorldName(loc.world!!)
        subName = temp.name

        init()
    }

    private fun init() {
        title = TextComponent("\n§cReport §b$id")
        zeitComp = TextComponent("\n§7Zeitpunkt§8: §r${formatDateTime(time)}")
        reasonComp = TextComponent("\n§7Grund§8: §4${type.name}")
        locComp = TextComponent("\n§7Position§8: §d${formatChatLocation(loc, false)}")
        worldComp = TextComponent("\n§7Weltname§8: §3$worldName")
        subComp = TextComponent("\n§7Plugin§8: §3$subName")

        sanctionComp.hoverEvent = sanctionHover
        freeComp.hoverEvent = freeHover
        sanctionComp.clickEvent = ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            sanctionCmd + id.toString()
        )
        freeComp.clickEvent = ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            freeCmd + id.toString()
        )

        showComps = arrayOf(
            headerFooterComp,
            title,
            taeterComp, toNamemcString(reportedPlayer),
            opferComp, toNamemcString(reporter),
            zeitComp, reasonComp, locComp, worldComp,
            subComp, sanctionComp, freeComp, headerFooterComp
        )


        //Log String
        val builder = StringBuilder()
        val taeter = Bukkit.getOfflinePlayer(reportedPlayer)
        val opfer = Bukkit.getOfflinePlayer(reporter)

        builder.append(opfer.name)
        builder.append(" -> ")
        builder.append(taeter.name)
        builder.append(": ")
        builder.append(type.internalName)
        builder.append(", ")
        builder.append(formatDateTime(time))

        logString = builder.toString()
    }

    fun setType(type: ReportType) {
        this.type = type
        reasonComp = TextComponent("\n§7Grund§8: §4${type.name}")
        showComps[7] = reasonComp
    }

    fun toSaveString(): String {
        val builder = StringBuilder()

        //ID
        builder.append(id.toString())
        builder.append(':')

        //Reporttype
        builder.append(type.internalName)
        builder.append(':')

        //Zeitpunkt
        builder.append(formatDateTimeSave(time))
        builder.append(':')

        //Spieler, der reportet wurde
        builder.append(reportedPlayer.toString())
        builder.append(':')

        //Spieler, der Reportet hat
        builder.append(reporter.toString())
        builder.append(':')

        //Location des reporteten Spielers
        builder.append(formatLocation(loc))
        builder.append(':')

        //Weltname
        builder.append(worldName)
        builder.append(':')

        //Pluginname
        builder.append(subName)

        return builder.toString()
    }

    /**
     * Formatiert den Eintrag in ein [TextComponent], um diesen beim Aufrufen des Befehls `/reportlist list [Seite]` anzuzeigen.
     * @return Ein Spigot Textkomponent
     */
    fun toListString(): TextComponent {
        val builder = StringBuilder("§8[§7")

        //Zeit
        builder.append(formatDateTime(time))
        builder.append("§8] [")

        //Spieler
        val player = Bukkit.getOfflinePlayer(reportedPlayer)
        builder.append("§e")
        builder.append(player.name)
        builder.append("§8] [")


        //Grund
        builder.append("§4")
        builder.append(type.name)
        builder.append("§8]\n\n")

        //Component
        val text = TextComponent(builder.toString())
        val click = ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            "/reportlist show $id"
        )
        text.hoverEvent = listHover
        text.clickEvent = click

        return text
    }

    /**
     * Formatiert den Eintrag in [TextComponent]s, um diese beim Aufrufen des Befehls `/reportlist show <id>` anzuzeigen.
     * @return Ein Array aus Textkomponenten
     */
    fun toShowString() = showComps

    /**
     * Formatiert den Eintrag in einen String, der in den Log/die Konsole geschrieben werden kann. <br></br>
     * Alternativ kann auch [.toString] verwendet werden.
     * @return Der formatierte String
     */
    fun toLogString() = logString

    override fun toString() = logString

    companion object {
        private val listHover = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Klicken, um mehr anzuzeigen"))
        private val namemcHover = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Klicken, um das NameMC-Profil anzuzeigen"))
        private val sanctionHover = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Klicken, um den Report als \"bestraft\" zu markieren"))
        private val freeHover = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Klicken, um den Report als \"nicht bestraft\" zu markieren"))

        private const val namemcLink = "https://de.namemc.com/search?q="
        private const val sanctionCmd = "/reportlist sanction "
        private const val freeCmd = "/reportlist free "

        private val taeterComp = TextComponent("\n§7Reportet wurde§8: ")
        private val opferComp = TextComponent("\n§7Von§8: ")
        private val headerFooterComp = TextComponent("\n§8-------------------------------------------")

        /**
         * Formatiert einen Spielernamen in ein [TextComponent], das per Klick auf das NameMC-Profil des Spielers <br></br>
         * weiterleitet.
         * @param id Die UUID des Spielers, dessen Profil abgefragt werden soll
         * @return Das generierte [TextComponent]
         */
        fun toNamemcString(id: UUID): TextComponent {
            val player = Bukkit.getOfflinePlayer(id)
            val text = TextComponent("§e" + player.name)
            val click = ClickEvent(ClickEvent.Action.OPEN_URL, namemcLink + id.toString())
            text.clickEvent = click
            text.hoverEvent = namemcHover

            return text
        }
    }
}
