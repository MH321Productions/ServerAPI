package io.github.mh321productions.serverapi.module.chat.command

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.command.APISubCommand
import io.github.mh321productions.serverapi.util.message.MessageBuilder
import io.github.mh321productions.serverapi.util.message.MessageFormatter.sendMessage
import io.github.mh321productions.serverapi.util.message.MessageFormatter.sendSimpleMessage
import io.github.mh321productions.serverapi.util.message.MessagePrefix
import io.github.mh321productions.serverapi.util.message.MessagePrefix.PrefixFormat
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Die gesendete Nachricht ist:
 *
 * `§3MSG §8>> <Von mit Rangfarbe> §8➜ <Zu mit Rangfarbe>§8: §f<Nachricht>`
 */
class MsgCommand(plugin: Main, api: APIImplementation) : APISubCommand(plugin, api) {

    companion object {
        private val prefix = MessagePrefix("§3MSG", PrefixFormat.Main)
        private val arrow = TextComponent(" §8➜ ")
        private val dp = TextComponent("§8: §f")
    }

    private val perms = api.permissionHandler

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (args.size < 2) {
            sendSimpleMessage(sender, StdMessages.argsTooFew)
            return false
        }

        val target = plugin.server.getPlayer(args.first())
        if (target == null) {
            sendSimpleMessage(sender, StdMessages.noPlayerWithName)
            return true
        }

        val builder = MessageBuilder().setPrefixes(prefix)

        //Absender
        if (isPlayer) {
            val p = sender as Player
            builder.addComponent(perms.getHighestRank(p).color + p.name)
        } else {
            builder.addComponent("§9Server: " + sender.name)
        }


        //Pfeil und Ziel
        builder
            .addComponent(arrow)
            .addComponent(perms.getHighestRank(target).color + target.name)
            .addComponent(dp)


        //Message
        for (i in 1 until args.size) builder.addComponent(args[i] + " ")

        val msg = builder.build()

        sendMessage(target, msg)
        if (isPlayer) sendMessage(sender as Player, msg)
        else sendSimpleMessage(sender, msg)

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        if (args.size == 1) return tabPlayers(args.first())

        return emptyList
    }
}
