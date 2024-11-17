package io.github.mh321productions.serverapi.module.chat.command

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.command.APISubCommand
import io.github.mh321productions.serverapi.module.chat.ChatModule
import io.github.mh321productions.serverapi.util.message.MessageBuilder
import io.github.mh321productions.serverapi.util.message.MessageFormatter.sendMessage
import io.github.mh321productions.serverapi.util.message.MessageFormatter.sendSimpleMessage
import io.github.mh321productions.serverapi.util.message.MessagePrefix
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChatCommand(plugin: Main, api: APIImplementation, private val module: ChatModule) : APISubCommand(plugin, api) {

    companion object {
        private val noChatWithName = MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cEs existiert kein Chat mit diesem Namen!").build()
        private val noEnterChat = MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cDu kannst diesem Chat nicht beitreten!").build()
    }

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        } else if (args.isEmpty()) {
            sendSimpleMessage(sender, StdMessages.argsTooFew)
            return false
        }

        val player = sender as Player
        val c = module.getChat(args.first())

        if (c == null) sendMessage(player, noChatWithName)
        else if (!module.setPlayerChat(player, c)) sendMessage(player, noEnterChat)

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        if (!isPlayer) return emptyList

        return tabCollection(args.first(), module.getVisibleChats(sender as Player))
    }
}
