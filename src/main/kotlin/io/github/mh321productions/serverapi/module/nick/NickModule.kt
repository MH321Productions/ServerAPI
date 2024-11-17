package io.github.mh321productions.serverapi.module.nick

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.Module
import io.github.mh321productions.serverapi.module.ModuleType
import io.github.mh321productions.serverapi.module.nick.command.NickCommand
import io.github.mh321productions.serverapi.module.nick.command.UnnickCommand
import io.github.mh321productions.serverapi.util.message.MessagePrefix
import org.bukkit.entity.Player

/**
 * The nick module handles the nicknames of players
 */
class NickModule(main: Main, api: APIImplementation) : Module(ModuleType.Nick, main, api) {

    companion object {
        @JvmField
        val msgPrefix = MessagePrefix("§9NickSystem", MessagePrefix.PrefixFormat.Main)
    }

    private val nickedPlayers = mutableMapOf<Player, String>()

    override fun initIntern() : Boolean {
        plugin.cmd.registerCommand("nick", NickCommand(plugin, api, this))
        plugin.cmd.registerCommand("unnick", UnnickCommand(plugin, api, this))

        return true
    }

    override fun stopIntern() {
        nickedPlayers.forEach { (a, _) -> a.setDisplayName(null) }
        nickedPlayers.clear()
    }

    override fun registerSubPlugin(sub: SubPlugin, func: () -> Unit) = addIntern(sub, func)
    override fun unregisterSubPlugin(sub: SubPlugin) = removeIntern(sub)

    /**
     * Checks whether a player is nicked
     * @param player The player to query
     */
    fun isPlayerNicked(player: Player) = nickedPlayers.contains(player)

    /**
     * Returns the nickname of a player (or its normal name when not nicked)
     */
    fun getNickname(player: Player) = nickedPlayers[player] ?: player.name

    /**
     * Adds or modifies a player's nickname
     */
    fun addNick(player: Player, nickname: String) {
        nickedPlayers[player] = nickname
        player.setDisplayName(nickname)
    }

    /**
     * Removes a player's nickname. Does nothing when player isn't nicked
     */
    fun removeNick(player: Player) {
        if (nickedPlayers.contains(player)) {
            player.setDisplayName(null)
            nickedPlayers.remove(player)
        }
    }
}