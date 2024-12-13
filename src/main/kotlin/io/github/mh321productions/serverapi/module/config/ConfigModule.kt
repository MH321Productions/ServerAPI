package io.github.mh321productions.serverapi.module.config

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.ServerSubPlugin
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import io.github.mh321productions.serverapi.module.friend.FriendModule
import io.github.mh321productions.serverapi.util.formatting.StringFormatter
import io.github.mh321productions.serverapi.util.functional.KotlinBukkitRunnable
import io.github.mh321productions.serverapi.util.message.MessageBuilder
import io.github.mh321productions.serverapi.util.message.MessageFormatter
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask
import java.util.*

class ConfigModule (plugin: Main, api: APIImplementation) : Module(ModuleType.Config, plugin, api), Listener {

    lateinit var files : ConfigFilesystem

    private val configs = mutableMapOf<UUID, PlayerConfig>()
    private val stats = mutableMapOf<UUID, PlayerStats>()
    private val autoSaveTasks = mutableMapOf<UUID, BukkitTask>()
    internal val pluginConfigClasses = mutableMapOf<SubPlugin, ConfigInfo>()

    override fun initIntern(): Boolean {
        plugin.server.pluginManager.registerEvents(this, plugin)

        pluginConfigClasses[ServerSubPlugin.instance] = ServerSubPlugin.instance.configInfo

        log.info("Initializing config directories")
        files = ConfigFilesystem(plugin, this)
        return files.init()
    }

    override fun stopIntern() {
        log.info("Unloading ${configs.size} configs and ${stats.size} stats")
        autoSaveTasks.forEach { (_, v) -> v.cancel() }
        configs.forEach { (_, v) -> v.saveToFile() }
        stats.forEach { (_, v) -> v.saveToFile() }

        autoSaveTasks.clear()
        configs.clear()
        stats.clear()
    }

    override fun registerSubPlugin(sub: SubPlugin, func: () -> Unit): Boolean {
        pluginConfigClasses[sub] = sub.configInfo

        return addIntern(sub, func)
    }
    override fun unregisterSubPlugin(sub: SubPlugin) {
        pluginConfigClasses.remove(sub)
        removeIntern(sub)
    }

    fun getPlayerConfig(uuid: UUID) = configs[uuid] ?: run {
        loadPlayerConfig(uuid)
        configs[uuid]!!
    }

    fun getPlayerStats(uuid: UUID) = stats[uuid] ?: run {
        loadPlayerStats(uuid)
        stats[uuid]!!
    }

    fun isPlayerConfigLoaded(uuid: UUID) = configs.containsKey(uuid)
    fun arePlayerStatsLoaded(uuid: UUID) = stats.containsKey(uuid)

    private fun loadPlayerConfig(uuid: UUID, checkOnlineStatus: Boolean = true) : Boolean {
        if (configs.containsKey(uuid)) return configs[uuid]!!.loadFromFile()
        if (checkOnlineStatus && !isPlayerOnline(uuid)) KotlinBukkitRunnable {unloadPlayerConfig(uuid)}.runTaskLater(plugin, 20 * 60 * 10)

        val conf = PlayerConfig(plugin, this, uuid)
        configs[uuid] = conf
        val res = conf.loadFromFile()
        return res
    }

    private fun loadPlayerStats(uuid: UUID, checkOnlineStatus: Boolean = true) : Boolean {
        if (stats.containsKey(uuid)) return stats[uuid]!!.loadFromFile()
        if (checkOnlineStatus && !isPlayerOnline(uuid)) KotlinBukkitRunnable {unloadPlayerStats(uuid)}.runTaskLater(plugin, 20 * 60 * 10)

        val conf = PlayerStats(plugin, this, uuid)
        stats[uuid] = conf
        return conf.loadFromFile()
    }

    private fun unloadPlayerConfig(uuid: UUID, save: Boolean = true) : Boolean {
        val entry = configs.remove(uuid)
        return if (save && entry != null) entry.saveToFile() else true
    }

    private fun unloadPlayerStats(uuid: UUID, save: Boolean = true) : Boolean {
        val entry = stats.remove(uuid)
        return if (save && entry != null) entry.saveToFile() else true
    }

    private fun isPlayerOnline(uuid: UUID) = plugin
        .server
        .onlinePlayers
        .map { it.uniqueId }
        .contains(uuid)

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        log.info("Loading stats and configs from player ${event.player.name}")

        val uuid = event.player.uniqueId
        loadPlayerConfig(uuid, false)
        loadPlayerStats(uuid, false)

        val friendJoinMsg = MessageBuilder()
            .setPrefixes(FriendModule.msgPrefix)
            .addComponent("${StringFormatter.formatPlayerName(uuid, plugin.perms.getHighestRank(uuid))} §7ist nun §aonline")
            .build()

        MessageFormatter.sendMessage(
            getPlayerConfig(event.player.uniqueId)
                .server.friends.friends
                .mapNotNull { plugin.server.getPlayer(it) },
            friendJoinMsg
        )

        autoSaveTasks[event.player.uniqueId] = KotlinBukkitRunnable {
            log.info("Auto-saving configs and stats of player ${event.player.name}")

            configs[event.player.uniqueId]?.saveToFile()
            stats[event.player.uniqueId]?.saveToFile()
        }.runTaskTimer(plugin, 20 * 60 * 60, 20 * 60 * 60)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        log.info("Unloading stats and configs from player ${event.player.name}")

        val uuid = event.player.uniqueId
        val friendLeaveMsg = MessageBuilder()
            .setPrefixes(FriendModule.msgPrefix)
            .addComponent("${StringFormatter.formatPlayerName(uuid, plugin.perms.getHighestRank(uuid))} §7ist nun §coffline")
            .build()

        MessageFormatter.sendMessage(
            getPlayerConfig(event.player.uniqueId)
                .server.friends.friends
                .mapNotNull { plugin.server.getPlayer(it) },
            friendLeaveMsg
        )

        unloadPlayerConfig(uuid)
        unloadPlayerStats(uuid)

        autoSaveTasks.remove(event.player.uniqueId)?.cancel()
    }
}
