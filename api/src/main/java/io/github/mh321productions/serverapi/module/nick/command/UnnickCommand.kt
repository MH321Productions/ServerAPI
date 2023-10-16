package io.github.mh321productions.serverapi.module.nick.command

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.command.APISubCommand
import io.github.mh321productions.serverapi.module.nick.NickModule
import io.github.mh321productions.serverapi.util.message.MessageBuilder
import io.github.mh321productions.serverapi.util.message.MessageFormatter
import io.github.mh321productions.serverapi.util.message.MessagePrefix
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UnnickCommand(main: Main, api: APIImplementation, private val module: NickModule) : APISubCommand(main, api) {

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        }

        val player = sender as Player
        module.removeNick(player)

        //TODO: Message verfeinern
        MessageFormatter.sendMessage(player, MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("Dein Nickname wurde entfernt").build())

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = emptyList
}