package io.github.mh321productions.serverapi.module.realtime

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import org.bukkit.World

class RealtimeModule(plugin: Main, api: APIImplementation) : Module(ModuleType.Realtime, plugin, api) {

    private lateinit var worker: RealtimeWorker
    var worlds = mutableListOf<World>()

    override fun init(): Boolean {
        worker = RealtimeWorker(this)
        worker.runTaskTimer(plugin, 0, 20)

        return true
    }

    override fun stopIntern() {
        worker.cancel()
    }

    override fun registerSubPlugin(sub: SubPlugin, func: () -> Unit): Boolean {
        return addIntern(sub, func)
    }

    override fun unregisterSubPlugin(sub: SubPlugin) {
        removeIntern(sub)
    }

    fun addWorld(w: World) {
        worlds.add(w)
    }

    fun addWorld(w: Collection<World>) {
        worlds.addAll(w)
    }

    fun removeWorld(w: World) {
        worlds.remove(w)
    }

    fun removeWorld(w: Collection<World>) {
        worlds.removeAll(w)
    }
}
