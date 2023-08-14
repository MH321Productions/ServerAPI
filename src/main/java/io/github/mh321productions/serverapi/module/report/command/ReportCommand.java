package io.github.mh321productions.serverapi.module.report.command;

import java.util.ArrayList;
import java.util.List;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.api.SubPlugin;
import io.github.mh321productions.serverapi.command.SubCommand;
import io.github.mh321productions.serverapi.util.message.MessageBuilder;
import io.github.mh321productions.serverapi.util.message.MessageFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.module.report.ReportEntry;
import io.github.mh321productions.serverapi.module.report.ReportModule;
import io.github.mh321productions.serverapi.module.report.ReportType;
import io.github.mh321productions.serverapi.util.message.Message;

/**
 * Intern: Der Sub-Command für /report
 * @author 321Productions
 *
 */
public class ReportCommand extends SubCommand {
	
	private ReportModule module;
	
	private static final Message noReportPlayer = new MessageBuilder().setPrefixes(ReportModule.prefix).addComponent("§cDer Spieler existiert nicht oder kann hier nicht reportet werden!").build();
	private static final Message noReportType = new MessageBuilder().setPrefixes(ReportModule.prefix).addComponent("§cDer Grund existiert nicht oder kann hier nicht benutzt werden!").build();
	private static final Message reportSuccess = new MessageBuilder().setPrefixes(ReportModule.prefix).addComponent("§7Dein Report wurde erfolgreich abgeschickt").build();

	public ReportCommand(Main plugin, APIImplementation api, ReportModule module) {
		super(plugin, api);
		this.module = module;
	}

	@Override
	protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) { //TODO: Messages ersetzen
		if (!isPlayer) {
			//sender.sendMessage("Nur Spieler können andere Spieler reporten");
			MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers);
			return true;
		} else if (args.size() < 2 || args.get(0).isEmpty() || args.get(1).isEmpty()) {
			MessageFormatter.sendSimpleMessage(sender, StdMessages.argsTooFew);
			return false;
		}
		
		Player player = (Player) sender;
		Player target = plugin.getServer().getPlayer(args.get(0));
		ReportType type = module.getType(args.get(1));
		SubPlugin sub1 = api.getControllingPlugin(player), sub2 = api.getControllingPlugin(target);
		
		if (target == null || sub1 != sub2 || player.getWorld() != target.getWorld()) {
			//player.sendMessage("Der Spieler \"" + args.get(0) + "\" existiert nicht oder kann hier nicht reportet werden");
			MessageFormatter.sendMessage(player, noReportPlayer);
			return true;
		} else if (type == null || type.getPlugin() != sub1) {
			//player.sendMessage("Der Typ \"" + args.get(1) + "\" existiert nicht oder kann hier nicht benutzt werden");
			MessageFormatter.sendMessage(player, noReportType);
			return true;
		}
		
		module.newEntry(new ReportEntry(target, player, type));
		MessageFormatter.sendMessage(player, reportSuccess);
		
		Message toMods = new MessageBuilder()
				.setPrefixes(ReportModule.prefix)
				.addComponent("§7Der Spieler §e" + player.getName() + " §7hat den Spieler §e" + target.getName() + " §7 für §4" + type.getName() + " §7reportet!")
				.build();
		//TODO: Message an Mods senden
		
		return true;
	}

	@Override
	protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		if (!isPlayer) return emptyList;
		
		ArrayList<String> ausgabe = new ArrayList<>();
		Player p = (Player) sender;
		SubPlugin sub = api.getControllingPlugin(p);
		
		if (args.size() == 1) {
			List<Player> players;
			
			if (sub == null) players = p.getWorld().getPlayers();
			else players = sub.getPlayersRelativeTo(p, SubPlugin.PlayerFilter.AllRegistered);
			
			players.remove(p);
			
			//TODO: Namen mit EVTL Genickten ersetzen
			
			//for (Player pl: players) if (pl.getName().toLowerCase().startsWith(args.get(0).toLowerCase())) ausgabe.add(pl.getName());
			return tabPlayers(args.get(0), players);
		} else if (args.size() == 2) {
			for (ReportType type: module.getReasons(sub)) if (type.getName().toLowerCase().startsWith(args.get(1).toLowerCase())) ausgabe.add(type.getName());
		}
		
		return ausgabe;
	}

}
