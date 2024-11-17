package io.github.mh321productions.serverapi.api

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import io.github.mh321productions.serverapi.module.chat.ChatModule
import io.github.mh321productions.serverapi.module.config.ConfigModule
import io.github.mh321productions.serverapi.module.friend.FriendModule
import io.github.mh321productions.serverapi.module.log.LogModule
import io.github.mh321productions.serverapi.module.nick.NickModule
import io.github.mh321productions.serverapi.module.npc.NPCModule
import io.github.mh321productions.serverapi.module.realtime.RealtimeModule
import io.github.mh321productions.serverapi.module.report.ReportModule
import io.github.mh321productions.serverapi.util.collection.CounterSet
import io.github.mh321productions.serverapi.util.permission.PermissionHandler
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicePriority
import java.util.*

/**
 * Intern: Die Implementierung der [ServerAPI]
 * @author 321Productions
 */
class APIImplementation(private val plugin: Main) : ServerAPI {

    override val loadedModules = mutableListOf<Module>()
    val loadedSubs: CounterSet<SubPlugin> = CounterSet()

    private val nickModule: NickModule

    init {
        //API als RegisteredServiceProvider registrieren
        plugin.server.servicesManager.register(
            ServerAPI::class.java, this,
            plugin, ServicePriority.Normal
        )
        plugin.logger.info("API gestartet und betriebsbereit")

        //TODO: Module starten
        loadedModules.add(LogModule(plugin, this))
        loadedModules.add(ConfigModule(plugin, this))
        loadedModules.add(ReportModule(plugin, this))
        loadedModules.add(ChatModule(plugin, this))
        loadedModules.add(RealtimeModule(plugin, this))
        loadedModules.add(NPCModule(plugin, this))
        nickModule = NickModule(plugin, this)
        loadedModules.add(nickModule)
        loadedModules.add(FriendModule(plugin, this))
    }

    fun stop() {
        plugin.logger.info("Stoppe API")


        //Die Module werden in umgekehrter Reihenfolge entladen (das Logging-Modul zuletzt)
        Collections.reverse(loadedModules)
        for (mod in loadedModules) mod.stop()


        //API als RegisteredServiceProvider stoppen
        plugin.server.servicesManager.unregister(ServerAPI::class.java, this)
        plugin.logger.info("API gestoppt")
    }

    override fun isModuleLoaded(type: ModuleType<*>): Boolean {
        for (m in loadedModules) {
            if (m.type == type && m.initialized) return true
        }

        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Module> getModule(type: ModuleType<T>): T {
        val module = loadedModules.find { it.type == type && it.initialized }
        if (module != null) return module as T
        else throw IllegalStateException("The module $type is not ready")
    }

    fun addSub(s: SubPlugin) {
        loadedSubs.add(s)
    }

    fun removeSub(s: SubPlugin) {
        loadedSubs.remove(s)
    }

    fun getControllingPlugin(player: Player): SubPlugin? {
        for (sub in loadedSubs) if (sub.isPlayerInGame(player)) {
            //plugin.getLogger().info(player.getName() + " wird vom Sub-Plugin \"" + sub.getName() + "\" kontrolliert");
            return sub
        }


        //plugin.getLogger().info(player.getName() + " wird von keinem Sub-Plugin kontrolliert");
        return null
    }

    override val permissionHandler: PermissionHandler
        get() = plugin.perms

    override fun isPlayerNicked(player: Player): Boolean {
        return nickModule.isPlayerNicked(player) ?: false
    }

    override fun getNickname(player: Player): String {
        return nickModule.getNickname(player) ?: player.name
    }
}
