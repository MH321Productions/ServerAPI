package io.github.mh321productions.serverapi.module.npc

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import org.bukkit.entity.Player

class NPCModule(main: Main, api: APIImplementation) : Module(ModuleType.NPC, main, api) {

    private val managers = mutableMapOf<SubPlugin, NPCManager>()
    private var minId = Int.MAX_VALUE
    private val freeIds = mutableSetOf<Int>()

    override fun initIntern() = true

    override fun stopIntern() {
        managers.forEach { (_, b) -> b.unregisterAll() }
        managers.clear()

        freeIds.clear()
        minId = Int.MAX_VALUE
    }

    override fun registerSubPlugin(sub: SubPlugin, func: () -> Unit): Boolean {
        if (isSubPluginLoaded(sub)) return true
        else if (!addIntern(sub, func)) return false

        managers[sub] = NPCManager(plugin, sub, this)

        return true
    }

    override fun unregisterSubPlugin(sub: SubPlugin) {
        if (isSubPluginLoaded(sub)) {
            removeIntern(sub)
            managers[sub]?.unregisterAll()
            managers.remove(sub)
        }
    }

    @Throws(IllegalStateException::class)
    fun getNPCManager(sub: SubPlugin): NPCManager {
        return managers[sub] ?: throw IllegalStateException("The SubPlugin is not registered with the module.")
    }

    /**
     * Checks whether a player is morphed by a Sub-Plugin
     */
    fun isPlayerMorphed(player: Player) : Boolean {
        return managers.values
            .map { it.morphedPlayers }
            .flatten()
            .contains(player)
    }

    fun requestEntityID(): Int {
        return if (freeIds.isEmpty()) minId--
        else {
            val id = freeIds.first()
            freeIds.remove(id)
            id
        }
    }

    fun freeEntityID(id: Int) {
        if (id == minId + 1) minId++
        else if (id > minId) {
            freeIds.add(id)
        } else return

        checkEntityIDs()
    }

    fun freeEntityID(ids: List<Int>) {
        freeIds.addAll(ids)
        checkEntityIDs()
    }

    private fun checkEntityIDs() {
        val sorted = freeIds.sorted().map { it - minId - 1 }
        val difference = sorted.indexOfFirst { it != sorted[it] }
        when (difference) {
            0 -> return
            -1 -> {
                minId = Int.MAX_VALUE
                freeIds.clear()
            }

            else -> {
                minId += difference
                freeIds.removeAll { it <= minId }
            }
        }
    }
}