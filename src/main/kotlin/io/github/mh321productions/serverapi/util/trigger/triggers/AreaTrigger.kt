package io.github.mh321productions.serverapi.util.trigger.triggers

import io.github.mh321productions.serverapi.util.geometry.Area
import io.github.mh321productions.serverapi.util.trigger.AbstractTrigger
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.Plugin

object AreaTrigger {

    class Toggle(val world: World, val area: Area, override val hint: String, plugin: Plugin) : AbstractTrigger(plugin) {

        private val playersInArea = mutableSetOf<Player>()

        @EventHandler
        fun onPlayerMove(event: PlayerMoveEvent) {
            val player = event.player
            val pos = player.location

            if (pos.world == world && pos in area) {
                if (!playersInArea.contains(player)) {
                    playersInArea.add(player)
                    trigger(player)
                }
            } else if (playersInArea.contains(player)) {
                playersInArea.remove(player)
            }
        }

    }

    class Always(val world: World, val area: Area, override val hint: String, plugin: Plugin) : AbstractTrigger(plugin) {
        @EventHandler
        fun onPlayerMove(event: PlayerMoveEvent) {
            val pos = event.player.location

            if (pos.world == world && pos in area) trigger(event.player)
        }
    }
}