package io.github.mh321productions.serverapi.api

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.SubPlugin.PlayerFilter
import io.github.mh321productions.serverapi.module.config.ConfigInfo
import io.github.mh321productions.serverapi.module.config.server.ServerConfig
import org.bukkit.World
import org.bukkit.entity.Player

/**
 * Interne Klasse, implementiert das SubPlugin-Interface, um den <br></br>
 * Server zu nutzen (kann mit anderen SubPlugins instanziiert werden, <br></br>
 * um z.B. Reportgründe zu spezifizieren)
 * @author 321Productions
 */
class ServerSubPlugin(plugin: Main) : SubPlugin {

    companion object {
        lateinit var instance: ServerSubPlugin
            private set

        internal fun init(plugin: Main) {
            instance = ServerSubPlugin(plugin)
        }
    }

    override var name: String = "Server"
    override val dataFolder = plugin.dataFolder
    override val logger = plugin.logger
    override val configInfo = ConfigInfo("server", ServerConfig::class.java, null)

    override fun isPlayerInGame(player: Player) = true
    override fun getWorldName(world: World) = world.name
    override fun getPlayersRelativeTo(focus: Player, filter: PlayerFilter) = focus.world.players.toList()
}
