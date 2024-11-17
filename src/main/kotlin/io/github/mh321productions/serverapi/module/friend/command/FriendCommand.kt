package io.github.mh321productions.serverapi.module.friend.command

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.command.APISubCommand
import io.github.mh321productions.serverapi.module.friend.FriendModule
import io.github.mh321productions.serverapi.util.formatting.StringFormatter
import io.github.mh321productions.serverapi.util.message.Message
import io.github.mh321productions.serverapi.util.message.MessageBuilder
import io.github.mh321productions.serverapi.util.message.MessageFormatter
import io.github.mh321productions.serverapi.util.message.MessagePrefix
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

abstract class AbstractFriendCommand(main: Main, api: APIImplementation, protected val module: FriendModule) : APISubCommand(main, api) {

    data class PlayerQueryResult(val isOnline: Boolean, val uuid: UUID, val name: String)

    protected companion object {
        val msgRequestSent: Message = MessageBuilder().setPrefixes(FriendModule.msgPrefix).addComponent("Freundschaftsanfrage gesendet").build()
        val msgRequestSentAlready: Message = MessageBuilder()
            .setPrefixes(FriendModule.msgPrefix)
            .addComponent("§cDu hast diesem Spieler bereits eine Anfrage gesendet!")
            .build()
        val msgNoIncomingRequest: Message = MessageBuilder().setPrefixes(FriendModule.msgPrefix).addComponent("§cEs gibt keine Anfrage dieses Spielers").build()
        val msgNoFriend: Message = MessageBuilder().setPrefixes(FriendModule.msgPrefix).addComponent("§cDu bist nicht mit diesem Spieler befreundet").build()
        val msgAlreadyFriend: Message = MessageBuilder().setPrefixes(FriendModule.msgPrefix).addComponent("§cDu bist bereits mit diesem Spieler befreundet").build()
    }

    fun msgFriendAdded(other: String): Message = MessageBuilder().setPrefixes(FriendModule.msgPrefix).addComponent("§7Du bist nun mit $other §7befreundet!").build()
    fun msgFriendAdded(other: Player) = msgFriendAdded(StringFormatter.formatPlayerName(other, plugin.perms.getHighestRank(other)))
    fun msgFriendAdded(other: UUID) = msgFriendAdded(StringFormatter.formatPlayerName(other, plugin.perms.getHighestRank(other)))
    fun msgIncomingRequestDenied(other: String): Message = MessageBuilder()
        .setPrefixes(FriendModule.msgPrefix)
        .addComponent("§7Du hast die Anfrage von $other §7abgelehnt.")
        .build()
    fun msgIncomingRequestDenied(other: Player) = msgIncomingRequestDenied(StringFormatter.formatPlayerName(other, plugin.perms.getHighestRank(other)))
    fun msgIncomingRequestDenied(other: UUID) = msgIncomingRequestDenied(StringFormatter.formatPlayerName(other, plugin.perms.getHighestRank(other)))
    fun msgFriendRemoved(other: String) : Message = MessageBuilder()
        .setPrefixes(MessagePrefix.Server)
        .addComponent("§7Du bist nun nicht mehr mit $other §7befreundet!")
        .build()
    fun msgFriendRemoved(other: Player) = msgFriendRemoved(StringFormatter.formatPlayerName(other, plugin.perms.getHighestRank(other)))
    fun msgFriendRemoved(other: UUID) = msgFriendRemoved(StringFormatter.formatPlayerName(other, plugin.perms.getHighestRank(other)))

    protected fun queryPlayer(arg: String) : PlayerQueryResult {
        val online = plugin.server.getPlayer(arg)

        if (online != null) return PlayerQueryResult(true, online.uniqueId, online.name)

        val uuid =
            try {
                UUID.fromString(arg)
            } catch (_: IllegalArgumentException) {
                null
            }
        val offline = if (uuid != null) plugin.server.getOfflinePlayer(uuid) else plugin.server.getOfflinePlayer(arg)

        return PlayerQueryResult(offline.isOnline, offline.uniqueId, offline.name ?: offline.uniqueId.toString())
    }
}

class FriendCommand(main: Main, api: APIImplementation, module: FriendModule) : AbstractFriendCommand(main, api, module) {

    init {
        sub["add"] = FriendAddCommand(main, api, module)
        sub["accept"] = FriendAcceptCommand(main, api, module)
        sub["deny"] = FriendDenyCommand(main, api, module)
        sub["list"] = FriendListCommand(main, api, module)
        sub["jump"] = FriendJumpCommand(main, api, module)
        sub["remove"] = FriendRemoveCommand(main, api, module)
    }

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = false

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = tabSubCommands(args[0])
}

class FriendAddCommand(main: Main, api: APIImplementation, module: FriendModule) : AbstractFriendCommand(main, api, module) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        } else if (args.isEmpty()) {
            MessageFormatter.sendMessage(sender as Player, StdMessages.argsTooFew)
            return false
        }

        val player = sender as Player
        val other = plugin.server.getPlayer(args[0])

        if (other == null) {
            MessageFormatter.sendMessage(player, StdMessages.noPlayerWithName)
            return true
        }

        if (module.addRequest(player.uniqueId, other.uniqueId)) {
            MessageFormatter.sendMessage(player, msgRequestSent)
            MessageFormatter.sendMessage(other, MessageBuilder()
                .setPrefixes(FriendModule.msgPrefix)
                .addComponent("§7Der Spieler ${StringFormatter.formatPlayerName(player, plugin.perms.getHighestRank(player))} §7möchte mit dir befreundet sein.")
                .newLine()
                .setPrefixes(FriendModule.msgPrefix)
                .addComponent("§a§lAnnehmen")
                .setClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept ${player.name}")
                .addComponent(" §8| ")
                .addComponent("§c§lLöschen")
                .setClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny ${player.name}")
                .build()
            )
        } else {
            if (module.hasOutgoingRequest(player.uniqueId, other.uniqueId)) {
                MessageFormatter.sendMessage(player, msgRequestSentAlready)
            } else if (module.hasIncomingRequest(player.uniqueId, other.uniqueId)) {
                //Other player already sent request, add friend
                module.removeRequest(player.uniqueId, other.uniqueId)
                module.addFriend(player.uniqueId, other.uniqueId)
                MessageFormatter.sendMessage(player, msgFriendAdded(other))
                MessageFormatter.sendMessage(other, msgFriendAdded(player))
            } else if (module.hasFriend(player.uniqueId, other.uniqueId)) {
                MessageFormatter.sendMessage(player, msgAlreadyFriend)
            }
        }

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = if (args.size == 1) tabPlayers(args[0]) else emptyList

}

class FriendAcceptCommand(main: Main, api: APIImplementation, module: FriendModule) : AbstractFriendCommand(main, api, module) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        } else if (args.isEmpty()) {
            MessageFormatter.sendMessage(sender as Player, StdMessages.argsTooFew)
            return false
        }

        val player = sender as Player
        val query = queryPlayer(args[0])
        val other = plugin.server.getOfflinePlayer(query.uuid)

        if (!module.hasIncomingRequest(player.uniqueId, query.uuid)) MessageFormatter.sendMessage(player, msgNoIncomingRequest)
        else {
            module.removeRequest(player.uniqueId, query.uuid)
            module.addFriend(player.uniqueId, query.uuid)
            MessageFormatter.sendMessage(player, msgFriendAdded(query.uuid))
            if (query.isOnline) MessageFormatter.sendMessage(other.player!!, msgFriendAdded(player))
        }

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) =
        if (!isPlayer) emptyList
        else module
            .getIncomingRequests((sender as Player).uniqueId)
            .map { plugin.server.getPlayer(it)?.name ?: it.toString() }
            .filter { it.startsWith(args[0], true) }
}

class FriendDenyCommand(main: Main, api: APIImplementation, module: FriendModule) : AbstractFriendCommand(main, api, module) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        } else if (args.isEmpty()) {
            MessageFormatter.sendMessage(sender as Player, StdMessages.argsTooFew)
            return false
        }

        val player = sender as Player
        val query = queryPlayer(args[0])
        val other = plugin.server.getOfflinePlayer(query.uuid)

        if (!module.hasIncomingRequest(player.uniqueId, query.uuid)) MessageFormatter.sendMessage(player, msgNoIncomingRequest)
        else {
            module.removeRequest(player.uniqueId, query.uuid)
            MessageFormatter.sendMessage(player, msgIncomingRequestDenied(query.name))
            if (query.isOnline) MessageFormatter.sendMessage(other.player!!, MessageBuilder()
                .setPrefixes(FriendModule.msgPrefix)
                .addComponent("§7Der Spieler ${StringFormatter.formatPlayerName(player, plugin.perms.getHighestRank(player))}" +
                        "§7hat deine Freundschaftsanfrage abgelehnt.")
                .build()
            )
        }

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) =
        if (!isPlayer) emptyList
        else module
            .getIncomingRequests((sender as Player).uniqueId)
            .map { plugin.server.getOfflinePlayer(it).name ?: it.toString() }
            .filter { it.startsWith(args[0], true) }

}

class FriendListCommand(main: Main, api: APIImplementation, module: FriendModule) : AbstractFriendCommand(main, api, module) {

    init {
        sub["friends"] = FriendListFriendsCommand(main, api, module)
        sub["requests"] = FriendListRequestsCommand(main, api, module)
    }

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = sub["friends"]?.onExecute(sender, isPlayer, args) ?: true

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = tabSubCommands(args.getOrElse(0) { "" })

}

class FriendListFriendsCommand(main: Main, api: APIImplementation, module: FriendModule) : AbstractFriendCommand(main, api, module) {

    private val ONLINE = "§a§lOnline"
    private val OFFLINE = "§c§lOffline"

    companion object {
        private val msgPageParseError: Message = MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cKonnte Seite nicht erkennen").build()
        private fun msgPageRangeError(maxPage: Int) : Message = MessageBuilder()
            .setPrefixes(MessagePrefix.Server)
            .addComponent("§cDie Seite muss zwischen 1 und $maxPage liegen")
            .build()
    }

    data class FriendInfo(val player: OfflinePlayer, val name: String)

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        }

        val player = sender as Player
        val friends = module.getFriends(player.uniqueId)
        val friendsPerPage = plugin.conf.modules.friend.friendsPerPage
        val maxPage = friends.size / friendsPerPage + 1
        val page = try {
            args.getOrElse(0) {"1"}.toInt() - 1
        } catch (_: NumberFormatException) {
            MessageFormatter.sendMessage(player, msgPageParseError)
            return true
        }

        if (page < 0 || page >= maxPage) {
            MessageFormatter.sendMessage(player, msgPageRangeError(maxPage))
            return true
        }

        val msg = MessageBuilder() //TODO: Überarbeiten
            .setPrefixes(MessagePrefix.Server)
            .addComponent("§7Deine Freundesliste§7:")
            .newLine()
            .addComponent("§7Du hast §e${friends.size} §7Freunde §8(§7Seite §e${page + 1} §7von §e$maxPage§8)")
            .newLine()
            .newLine()

        friends
            .paginate(page, friendsPerPage)
            .map { FriendInfo(plugin.server.getOfflinePlayer(it), StringFormatter.formatPlayerName(it, plugin.perms.getHighestRank(it))) }
            .forEach { msg
                .addComponent("§7● ${it.name} §8(${if (it.player.isOnline) ONLINE else OFFLINE}§8)")
                .newLine()
            }

        MessageFormatter.sendMessage(player, msg.build())

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = emptyList

}

class FriendListRequestsCommand(main: Main, api: APIImplementation, module: FriendModule) : AbstractFriendCommand(main, api, module) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        }

        val player = sender as Player
        val msg = MessageBuilder() //TODO: Überarbeiten
            .setPrefixes(MessagePrefix.Server)
            .addComponent("Deine Ausgehenden Anfragen:")
            .newLine()

        module
            .getOutgoingRequests(player.uniqueId)
            .map { plugin.server.getOfflinePlayer(it).name ?: it.toString() }
            .forEach { msg
                .addComponent(it)
                .newLine()
            }

        msg
            .addComponent("Deine Eingehenden Anfragen:")
            .newLine()

        module
            .getIncomingRequests(player.uniqueId)
            .map { plugin.server.getOfflinePlayer(it).name ?: it.toString() }
            .forEach { msg
                .addComponent(it)
                .newLine()
            }

        MessageFormatter.sendMessage(player, msg.build())

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = emptyList

}

class FriendJumpCommand(main: Main, api: APIImplementation, module: FriendModule) : AbstractFriendCommand(main, api, module) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        } else if (args.isEmpty()) {
            MessageFormatter.sendMessage(sender as Player, StdMessages.argsTooFew)
            return false
        }

        val player = sender as Player

        //TODO: Überarbeiten, wenn SubPlugin Kommunikation mit Jump abgeschlossen ist
        MessageFormatter.sendMessage(player, MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("An diesem Feature wird noch gearbeitet").build())

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) =
        tabPlayers(
            args[0],
            plugin.server.onlinePlayers
            .filter {
                module
                    .getFriends((sender as Player).uniqueId)
                    .contains(it.uniqueId)
            }
        )

}

class FriendRemoveCommand(main: Main, api: APIImplementation, module: FriendModule) : AbstractFriendCommand(main, api, module) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        } else if (args.isEmpty()) {
            MessageFormatter.sendMessage(sender as Player, StdMessages.argsTooFew)
            return false
        }

        val player = sender as Player
        val query = queryPlayer(args[0])
        val other = plugin.server.getOfflinePlayer(query.uuid)

        if (!module.hasFriend(player.uniqueId, query.uuid)) MessageFormatter.sendMessage(player, msgNoFriend)
        else {
            module.removeFriend(player.uniqueId, query.uuid)
            MessageFormatter.sendMessage(player, msgFriendRemoved(query.name))
            if (query.isOnline) MessageFormatter.sendMessage(other.player!!, msgFriendRemoved(player))
        }

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) =
        if (!isPlayer) emptyList
        else module
            .getFriends((sender as Player).uniqueId)
            .map { plugin.server.getOfflinePlayer(it).name ?: it.toString() }
            .filter { it.startsWith(args[0], true) }

}