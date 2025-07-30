package io.github.mh321productions.serverapi.util.trigger

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

abstract class AbstractTrigger(protected val plugin: Plugin) : Trigger, Listener, AutoCloseable {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun trigger(p: Player) {
        val event = TriggerEvent(p, this)
        plugin.server.pluginManager.callEvent(event)
    }

    override fun close() {
        HandlerList.unregisterAll(this)
    }
}