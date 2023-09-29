package io.github.mh321productions.serverapi.util.testing;

import java.util.ArrayList;
import java.util.List;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.command.APISubCommand;
import io.github.mh321productions.serverapi.command.SubCommand;
import io.github.mh321productions.serverapi.util.permission.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NodeTest extends APISubCommand {

	public NodeTest(Main plugin, APIImplementation api) {
		super(plugin, api);
	}

	@Override
	protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		Player target;
		String perm;
		if (args.isEmpty()) {
			ArrayList<Rank> ranks = plugin.perms.getRanks();
			sender.sendMessage("Folgende Ränge sind geladen:");
			for (Rank r: ranks) {
				sender.sendMessage(r.toDisplayString().replace('&', '§'));
				for (String s: r.getSubGroups()) sender.sendMessage("-> " + s);
			}
			return true;
		} else if (args.size() == 1) {
			if (!isPlayer) {
				sender.sendMessage("Nur Spieler können den Command ohne Argumente ausführen");
				return true;
			}
			target = (Player) sender;
			perm = args.get(0);
		} else {
			target = plugin.getServer().getPlayer(args.get(0));
			if (target == null) {
				sender.sendMessage("Der Spieler \"" + args.get(0) + "\" existiert nicht oder ist offline");
				return true;
			}
			perm = args.get(1);
		}
		
		ArrayList<Rank> ranks = plugin.perms.getRanks(target);
		sender.sendMessage("Der Spieler \"" + target.getName() + "\" hat die folgenden Ränge:");
		//sender.sendMessage("Folgende Ränge sind geladen:");
		for (Rank r: ranks) {
			sender.sendMessage(r.toDisplayString().replace('&', '§'));
			for (String s: r.getSubGroups()) sender.sendMessage("-> " + s);
			sender.sendMessage("Defined: " + r.definesPermission(perm) + ", Value: " + r.hasPermission(perm));
			/*for (Entry<String, Boolean> e: r.getNodes().entrySet()) {
				sender.sendMessage(e.getKey() + ": " + e.getValue());
			}*/
		}
		
		return true;
	}

	@NotNull
	@Override
	protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		return tabPlayers(args.get(0));
	}

}
