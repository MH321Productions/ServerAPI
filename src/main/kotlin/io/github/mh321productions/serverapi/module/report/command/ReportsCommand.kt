package io.github.mh321productions.serverapi.module.report.command

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.command.APISubCommand
import io.github.mh321productions.serverapi.module.report.ReportModule
import io.github.mh321productions.serverapi.module.report.ReportType
import io.github.mh321productions.serverapi.util.message.Message
import io.github.mh321productions.serverapi.util.message.MessageBuilder
import io.github.mh321productions.serverapi.util.message.MessageFormatter
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.min

abstract class ReportsBaseCommand(main: Main, api: APIImplementation, protected val module: ReportModule) : APISubCommand(main, api) {
    protected companion object {
        val noReport: Message = MessageBuilder().addComponent("§cEs gibt keinen Report mit dieser ID!").setPrefixes(ReportModule.prefix).build()
        val reportEmpty: Message = MessageBuilder().addComponent("§7Es sind keine Reports vorhanden").setPrefixes(ReportModule.prefix).build()
        val reportDeleted: Message = MessageBuilder()
            .addComponent("§7Der Report wurde gelöscht. Der Spieler wird aus der Reportliste §aentfernt§7.")
            .setPrefixes(ReportModule.prefix)
            .build()
        val reportSanctioned: Message = MessageBuilder()
            .addComponent("§7Der Report wurde als §4§lbestraft §7markiert. Die Bestrafung liegt nun bei dir.")
            .setPrefixes(ReportModule.prefix)
            .build()
    }
}

class ReportsCommand(main: Main, api: APIImplementation, module: ReportModule) : ReportsBaseCommand(main, api, module) {

    init {
        sub["list"] = ReportsListCommand(main, api, module)
        sub["show"] = ReportsShowCommand(main, api, module)
        sub["sanction"] = ReportsSanctionCommand(main, api, module)
        sub["free"] = ReportsFreeCommand(main, api, module)
    }

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = false

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>) = tabSubCommands(args[0])
}

class ReportsListCommand(main: Main, api: APIImplementation, module: ReportModule) : ReportsBaseCommand(main, api, module) {

    companion object {
        val lineBottom = TextComponent("§8---------------------------------------")
    }

    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        }

        val player = sender as Player

        //Parse page
        var page = 0
        if (args.isNotEmpty()) {
            try {
                page = args[0].toInt() - 1
            } catch (e: NumberFormatException) {
                MessageFormatter.sendMessage(player, MessageBuilder().setPrefixes(ReportModule.prefix).addComponent("§c\"${args[0]}\" ist keine Zahl!").build())
                return true
            }
        }

        //Range check
        val max = module.getEntries().getPageCount(plugin.conf.modules.friend.friendsPerPage)
        if (page < 0 || page >= max) {
            if (max == 0)
                MessageFormatter.sendMessage(player, reportEmpty)
            else
                MessageFormatter.sendMessage(player,
                    MessageBuilder()
                        .setPrefixes(ReportModule.prefix)
                        .addComponent("§cDie Seitenzahl muss zwischen 1 und $max liegen!")
                        .build()
                )

            return true
        }

        //Construct Message
        val msg = MessageBuilder()
            .addComponent("§8----------§cReports§8: §7Seite §e${(page + 1)} §7von §e$max§8----------\n\n") //Header

        module.getEntries()
            .paginate(page, 5)
            .forEach { msg.addComponent(it.toListString()) } //Entries

        msg.addComponent(lineBottom) //Footer

        MessageFormatter.sendMessage(player, msg.build())

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        val count = module.getEntries().getPageCount(plugin.conf.modules.friend.friendsPerPage)
        return if (count == 0) emptyList else listOf("[1;$count]")
    }

}

class ReportsShowCommand(main: Main, api: APIImplementation, module: ReportModule) : ReportsBaseCommand(main, api, module) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (!isPlayer) {
            MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers)
            return true
        } else if (args.isEmpty()) return false

        val player = sender as Player
        val entry = try {
            module.getEntry(UUID.fromString(args[0]))
        } catch (_: IllegalArgumentException) {
            null
        }

        if (entry == null) MessageFormatter.sendMessage(player, noReport)
        else MessageFormatter.sendMessage(player, Message(entry.toShowString()))

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        return if (args.size == 1)
            tabCollection(args[0], module.getEntries().map { it.id })
        else
            emptyList
    }
}

class ReportsSanctionCommand(main: Main, api: APIImplementation, module: ReportModule) : ReportsBaseCommand(main, api, module) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (args.isEmpty()) return false

        val entry = try {
            module.getEntry(UUID.fromString(args[0]))
        } catch (_: IllegalArgumentException) {
            null
        }

        if (entry == null) MessageFormatter.sendSimpleMessage(sender, noReport)
        else MessageFormatter.sendSimpleMessage(sender, reportSanctioned)

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        return if (args.size == 1)
            tabCollection(args[0], module.getEntries().map { it.id })
        else
            emptyList
    }
}

class ReportsFreeCommand(main: Main, api: APIImplementation, module: ReportModule) : ReportsBaseCommand(main, api, module) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (args.isEmpty()) return false

        val entry = try {
            module.getEntry(UUID.fromString(args[0]))
        } catch (_: IllegalArgumentException) {
            null
        }

        if (entry == null) MessageFormatter.sendSimpleMessage(sender, noReport)
        else MessageFormatter.sendSimpleMessage(sender, reportDeleted)

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        return if (args.size == 1)
            tabCollection(args[0], module.getEntries().map { it.id })
        else
            emptyList
    }
}