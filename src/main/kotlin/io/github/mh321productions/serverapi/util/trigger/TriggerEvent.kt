package io.github.mh321productions.serverapi.util.trigger

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class TriggerEvent(val player: Player, val trigger: Trigger) : Event() {

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        @Suppress("unused")
        fun getHandlerList() = handlerList
    }

    override fun getHandlers() = handlerList
}