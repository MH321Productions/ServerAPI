package io.github.mh321productions.serverapi.api

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.SubPlugin.PlayerFilter
import io.github.mh321productions.serverapi.module.config.ConfigInfo
import io.github.mh321productions.serverapi.module.config.server.ServerConfig
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.File
import java.util.logging.Logger

/**
 * Interne Klasse, implementiert das SubPlugin-Interface, um den <br></br>
 * Server zu nutzen (kann mit anderen SubPlugins instanziiert werden, <br></br>
 * um z.B. Reportgründe zu spezifizieren)
 * @author 321Productions
 */
class ServerSubPlugin : SubPlugin {

    companion object {
        val emptyServer = ServerSubPlugin(null)
        private var plugin: Main? = null

        fun setMain(main: Main) {
            if (plugin != null) return
            plugin = main
        }
    }

    override var name: String
    private val sub: SubPlugin?

    /**
     * Initialisiert eine Instanz mit einem Sub-Plugin
     * @param sub Das Sub-Plugin
     */
    constructor(sub: SubPlugin?) {
        this.sub = sub

        name = if (sub != null) "Server (" + sub.name + ")"
        else "Server"
    }

    /**
     * Initialisiert eine Instanz mit einem Spieler und sucht nach dem Sub-Plugin
     * @param player Der Spieler
     */
    constructor(player: Player) {
        sub = plugin?.api?.loadedSubs
            ?.find { it.isPlayerInGame(player) }

        name = if (sub != null) "Server (${sub.name})" else "Server"
    }

    override val dataFolder = plugin!!.dataFolder
    override val logger = plugin!!.logger
    override val configInfo = ConfigInfo("server", ServerConfig::class.java, null)

    override fun isPlayerInGame(player: Player): Boolean {
        return true
    }

    override fun getWorldName(world: World): String {
        if (sub != null) return sub.getWorldName(world)

        return world.name
    }

    override fun getPlayersRelativeTo(focus: Player, filter: PlayerFilter): List<Player> {
        return focus.world.players
    }
}
