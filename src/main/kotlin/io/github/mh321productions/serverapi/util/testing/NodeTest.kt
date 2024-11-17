package io.github.mh321productions.serverapi.util.testing

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.command.APISubCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NodeTest(plugin: Main, api: APIImplementation) : APISubCommand(plugin, api) {
    override fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        val target: Player?
        val perm: String
        if (args.isEmpty()) {
            val ranks = plugin.perms.ranks
            sender.sendMessage("Folgende Ränge sind geladen:")
            for (r in ranks) {
                sender.sendMessage(r.toDisplayString().replace('&', '§'))
                for (s in r.subGroups) sender.sendMessage("-> $s")
            }
            return true
        } else if (args.size == 1) {
            if (!isPlayer) {
                sender.sendMessage("Nur Spieler können den Command ohne Argumente ausführen")
                return true
            }
            target = sender as Player
            perm = args[0]
        } else {
            target = plugin.server.getPlayer(args[0])
            if (target == null) {
                sender.sendMessage("Der Spieler \"" + args[0] + "\" existiert nicht oder ist offline")
                return true
            }
            perm = args[1]
        }

        val ranks = plugin.perms.getRanks(target)
        sender.sendMessage("Der Spieler \"" + target.name + "\" hat die folgenden Ränge:")
        //sender.sendMessage("Folgende Ränge sind geladen:");
        for (r in ranks) {
            sender.sendMessage(r.toDisplayString().replace('&', '§'))
            for (s in r.subGroups) sender.sendMessage("-> $s")
            sender.sendMessage("Defined: " + r.definesPermission(perm) + ", Value: " + r.hasPermission(perm))
            /*for (Entry<String, Boolean> e: r.getNodes().entrySet()) {
				sender.sendMessage(e.getKey() + ": " + e.getValue());
			}*/
        }

        return true
    }

    override fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        return tabPlayers(args[0])
    }
}
