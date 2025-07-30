package io.github.mh321productions.serverapi.util.trigger

import org.bukkit.entity.Player

interface Trigger {
    val hint: String

    fun trigger(p: Player)
}