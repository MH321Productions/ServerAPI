package io.github.mh321productions.serverapi.api

import io.github.mh321productions.serverapi.module.config.ConfigInfo
import io.github.mh321productions.serverapi.module.report.ReportModule
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.logging.Logger

/**
 * Das Interface, das die Sub-Plugins implementieren müssen,
 * um mit der API zu kommunizieren.
 * @author 321Productions
 */
interface SubPlugin {
    /**
     * Ein Enum zum Filtern verschiedener Spielerlisten.
     * @author 321Productions
     */
    enum class PlayerFilter {
        /**
         * Alle Spieler in der Welt, auch nicht im Plugin registrierte und versteckte Spieler. <br></br>
         * Quasi äquivalent zu [World.getPlayers].
         */
        AllPlayers,

        /**
         * Alle im Plugin registrierten Spieler in der Welt, auch nicht sichtbare <br></br>
         * (vorm Spieler versteckte) Spieler. (z.B. Getötete Spieler im Spectator). <br></br>
         * Keine nicht registrierten Spieler (wie Admins oder Mods im Vanish Spectator).
         */
        AllRegistered,

        /**
         * Alle im Plugin registrierten Spieler in der Welt, die der Spieler auch sehen kann.
         */
        OnlyVisible
    }

    /**
     * Gibt den internen Namen des Sub-Plugins zurück, siehe [Plugin.getName]
     * @return Der Plugin-Name des Sub-Plugins
     */
	val name: String

    /**
     * Gibt den Datenordner des Sub-Plugins zurück, siehe [Plugin.getDataFolder]
     * @return Der Datenordner
     */
    val dataFolder: File

    /**
     * Gibt den internen Logger des Sub-Plugins zurück, siehe [Plugin.getLogger]
     * @return Den internen Logger
     */
	val logger: Logger

    /**
     * Die Klassen der Konfiguration und Statistiken, die für jeden Spieler gespeichert werden sollen
     * @see ServerSubPlugin
     */
    val configInfo: ConfigInfo

    /**
     * Gibt zurück, ob ein Spieler unter Kontrolle des Plugins ist (a.k.a. im Spiel/in der Wartelobby etc.). <br></br>
     * Wird für Pluginspezifische Operationen benutzt (siehe z.B. [ReportModule])
     * @param player Der abzufragende Spieler
     * @return Ob der Spieler zurzeit unter Kontrolle des Plugins ist
     */
    fun isPlayerInGame(player: Player): Boolean

    /**
     * Gibt den Namen der abgefragten Welt zurück. Wenn ein Weltgenerator wie bei z.B. Skywars oder <br></br>
     * BlockBall vorhanden ist, der die Welten dynamisch generiert, soll der Name der Weltvorlage <br></br>
     * zurückgegeben werden, sonst einfach der Name der Welt ([World.getName]).
     * @param world Die von der API abgefragte Welt
     * @return Der Weltname oder der Name der Vorlage
     */
    fun getWorldName(world: World): String

    /**
     * Gibt alle Spieler relativ zu einem zurück, nach verschiedenen Filterkriterien. Beispielsweise alle Spieler, die <br></br>
     * dieser reporten kann.
     * @param focus Der Spieler, von dem die Suche ausgehen soll
     * @param filter Der Filter, der angewendet wird (siehe [PlayerFilter])
     * @return Eine Liste aller dem Filter entsprechenden Spieler, oder eine leere Liste
     */
    fun getPlayersRelativeTo(focus: Player, filter: PlayerFilter): List<Player>
}
