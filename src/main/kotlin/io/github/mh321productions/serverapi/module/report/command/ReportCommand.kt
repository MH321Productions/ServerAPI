package io.github.mh321productions.serverapi.module.report.command

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.command.APISubCommand
import io.github.mh321productions.serverapi.module.report.ReportEntry
import io.github.mh321productions.serverapi.module.report.ReportModule
import io.github.mh321productions.serverapi.util.message.MessageBuilder
import io.github.mh321productions.serverapi.util.message.MessageFormatter.sendMessage
import io.github.mh321productions.serverapi.util.message.MessageFormatter.sendSimpleMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * Der Sub-Command für /report
 * @author 321Productions
 */
class ReportCommand(plugin: Main, api: APIImplementation, private val module: ReportModule) : APISubCommand(plugin, api) {

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean { //TODO: Messages ersetzen
        if (!isPlayer) {
            sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        } else if (args.size < 2 || args[0].isEmpty() || args[1].isEmpty()) {
            sendSimpleMessage(sender, StdMessages.argsTooFew)
            return false
        }

        val player = sender as Player
        val target = plugin.server.getPlayer(args[0])
        val type = module.getType(args[1])
        val sub1 = api.getControllingPlugin(player)
        val sub2 = api.getControllingPlugin(target!!)

        if (sub1 !== sub2 || player.world !== target.world) {
            sendMessage(player, noReportPlayer)
            return true
        } else if (type == null || type.plugin !== sub1) {
            sendMessage(player, noReportType)
            return true
        }

        module.newEntry(ReportEntry(target, player, type))
        sendMessage(player, reportSuccess)

        val toMods = MessageBuilder()
            .setPrefixes(ReportModule.prefix)
            .addComponent("§7Der Spieler §e" + player.name + " §7hat den Spieler §e" + target.name + " §7 für §4" + type.name + " §7reportet!")
            .build()

        //TODO: Message an Mods senden
        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        if (!isPlayer) return emptyList

        val p = sender as Player
        val sub = api.getControllingPlugin(p)

        if (args.size == 1) {
            val players = sub?.getPlayersRelativeTo(p, SubPlugin.PlayerFilter.AllRegistered)?.toMutableList() ?: p.world.players.toMutableList()
            players.remove(p)

            //TODO: Namen mit EVTL Genickten ersetzen
            return tabPlayers(args[0], players)
        } else if (args.size == 2) {
            return module.getReasons(sub)
                .filter { it.name.lowercase().startsWith(args[1].lowercase()) }
                .map { it.name }
        }

        return emptyList
    }

    companion object {
        private val noReportPlayer = MessageBuilder().setPrefixes(ReportModule.prefix).addComponent("§cDer Spieler existiert nicht oder kann hier nicht reportet werden!").build()
        private val noReportType = MessageBuilder().setPrefixes(ReportModule.prefix).addComponent("§cDer Grund existiert nicht oder kann hier nicht benutzt werden!").build()
        private val reportSuccess = MessageBuilder().setPrefixes(ReportModule.prefix).addComponent("§7Dein Report wurde erfolgreich abgeschickt").build()
    }
}
