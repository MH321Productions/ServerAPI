package io.github.mh321productions.serverapi.util.visibility

import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

/**
 * Eine Sammlung von [VisibilityGroup]s
 */
object StandardVisibilityGroups {

    /**
     * Eine "leere" Implementierung, mit der man nichts machen kann
     */
    class EmptyVisibilityGroup : VisibilityGroup()

    /**
     * This group focuses on a [World], i.e. all players in this world are part of the group
     */
    class WorldVisibilityGroup(plugin: Plugin, val world: World) : VisibilityGroup(), Listener {

        init {
            plugin.server.pluginManager.registerEvents(this, plugin)
            players.addAll(world.players)
        }

        @EventHandler
        fun onWorldChange(event: PlayerChangedWorldEvent) {
            if (!players.contains(event.player)) return

            if (event.from != world && event.player.world == world) {
                players.add(event.player)
                notifyAddPlayer(event.player)
            } else if (event.from == world && event.player.world != world) {
                players.remove(event.player)
                notifyRemovePlayer(event.player)
            }
        }
    }

    /**
     * This group contains all online players on the server
     */
    class ServerVisibilityGroup(plugin: Plugin) : VisibilityGroup(), Listener {

        init {
            plugin.server.pluginManager.registerEvents(this, plugin)
            players.addAll(plugin.server.onlinePlayers)
        }

        @EventHandler
        fun onJoin(event: PlayerJoinEvent) {
            players.add(event.player)
            notifyAddPlayer(event.player)
        }

        @EventHandler
        fun onQuit(event: PlayerQuitEvent) {
            players.remove(event.player)
            notifyRemovePlayer(event.player)
        }
    }
}