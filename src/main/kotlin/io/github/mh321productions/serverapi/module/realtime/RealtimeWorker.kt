package io.github.mh321productions.serverapi.module.realtime

import org.bukkit.scheduler.BukkitRunnable
import java.time.LocalTime

class RealtimeWorker(private val module: RealtimeModule) : BukkitRunnable() {
    override fun run() {
        if (module.worlds.isEmpty()) return

        val t = LocalTime.now()

        //Formel: Stunden*1000 - 6000
        val secRaw = t.minute * 60 + t.second
        val seconds = Math.round(secRaw / 3.6f)

        val timeRaw = t.hour * 1000 + seconds
        var time = timeRaw - 6000
        if (time < 0) time += 24000

        module.worlds.forEach { it.time = time.toLong() }
    }
}
