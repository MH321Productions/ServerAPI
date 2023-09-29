package io.github.mh321productions.serverapi.util.functional

import org.bukkit.scheduler.BukkitRunnable

/**
 * A functional implementation of the [BukkitRunnable] class
 */
class KotlinBukkitRunnable(val func: (obj : KotlinBukkitRunnable) -> Unit) : BukkitRunnable() {
    override fun run() {
        func(this)
    }
}