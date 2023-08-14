package io.github.mh321productions.serverapi.module.report.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.command.SubCommand;
import io.github.mh321productions.serverapi.util.message.MessageBuilder;
import io.github.mh321productions.serverapi.util.message.MessageFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import io.github.mh321productions.serverapi.module.report.ReportEntry;
import io.github.mh321productions.serverapi.module.report.ReportModule;
import io.github.mh321productions.serverapi.util.message.Message;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Intern: Der Sub-Command für /reportlist
 * @author 321Productions
 *
 */
public class ReportsCommand extends SubCommand {
	
	private static final Message noReport = new MessageBuilder().addComponent("§cEs gibt keinen Report mit dieser ID!").setPrefixes(ReportModule.prefix).build();
	private static final Message reportEmpty = new MessageBuilder().addComponent("§7Es sind keine Reports vorhanden").setPrefixes(ReportModule.prefix).build();
	private static final Message reportDeleted = new MessageBuilder().addComponent("§7Der Report wurde gelöscht. Der Spieler wird aus der Reportliste §aentfernt§7.").setPrefixes(ReportModule.prefix).build();
	private static final Message reportSanctioned =
			new MessageBuilder()
			.addComponent("§7Der Report wurde als §4§lbestraft §7markiert. Die Bestrafung liegt nun bei dir.")
			.setPrefixes(ReportModule.prefix)
			.build();

	public ReportsCommand(Main plugin, APIImplementation api, ReportModule module) {
		super(plugin, api);
		
		//Sub-Commands hinzufügen
		sub.put("list", new ReportsListCommand(plugin, api, module));
		sub.put("show", new ReportsShowCommand(plugin, api, module));
		sub.put("sanction", new ReportsSanctionCommand(plugin, api, module));
		sub.put("free", new ReportsFreeCommand(plugin, api, module));
	}

	@Override
	protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		return false;
	}

	@Override
	protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		ArrayList<String> ausgabe = new ArrayList<>();
		
		for (String s: sub.keySet()) if (s.toLowerCase().startsWith(args.get(0).toLowerCase())) ausgabe.add(s); 
		
		return ausgabe;
	}
	
	private static class ReportsListCommand extends SubCommand {
		
		private static final TextComponent line = new TextComponent("§8---------------------------------------");
		
		private ReportModule module;

		public ReportsListCommand(Main plugin, APIImplementation api, ReportModule module) {
			super(plugin, api);
			this.module = module;
		}
		
		private int getPages() {
			List<ReportEntry> entries = module.getEntries();
			int count = entries.size() / 5;
			if (entries.size() % 5 != 0) count++;
			
			return count;
		}

		@Override
		protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) {
			if (!isPlayer) {
				MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers);
				//sender.sendMessage("Nur Spieler können diesen Command ausführen");
				return true;
			}
			
			Player player = (Player) sender;
			int page;
			if (args.size() == 0) page = 0;
			else {
				try {
					page = Integer.parseInt(args.get(0));
					page--;
				} catch (NumberFormatException e) {
					MessageFormatter.sendMessage(player, new MessageBuilder().setPrefixes(ReportModule.prefix).addComponent("§c\"" + args.get(0) + "\" ist keine Zahl!").build());
					return true;
				}
			}
			
			//Range Check
			int max = getPages();
			if (page < 0 || page >= max) {
				if (max == 0) MessageFormatter.sendMessage(player, reportEmpty);
				else MessageFormatter.sendMessage(player, new MessageBuilder().setPrefixes(ReportModule.prefix).addComponent("§cDie Seitenzahl muss zwischen 1 und " + max + " liegen!").build());
				
				return true;
			}
			
			ArrayList<BaseComponent> message = Lists.newArrayList(new TextComponent("§8----------§cReports§8: §7Seite §e" + (page + 1) + " §7von §e" + max + "§8----------\n\n"));
			List<ReportEntry> entries = module.getEntries();
			
			for (int index = page * 5; index < page * 5 + 5; index++) { //5 Einträge
				if (index >= entries.size()) break;
				
				message.add(entries.get(index).toListString());
			}
			
			message.add(line);
			
			BaseComponent[] array = new BaseComponent[message.size()];
			message.toArray(array);
			player.spigot().sendMessage(array);
			
			return true;
		}

		@Override
		protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
			int count = getPages();
			if (count == 0) return emptyList;
			
			return Lists.newArrayList("[1;" + count + "]");
		}
	}

	private static class ReportsShowCommand extends SubCommand {
		
		private ReportModule module;

		public ReportsShowCommand(Main plugin, APIImplementation api, ReportModule module) {
			super(plugin, api);
			this.module = module;
		}

		@Override
		protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) {
			if (!isPlayer) {
				MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers);
				return true;
			} else if (args.size() == 0) return false;
			
			Player player = (Player) sender;
			ReportEntry entry = module.getEntry(UUID.fromString(args.get(0)));
			if (entry == null) {
				MessageFormatter.sendMessage(player, noReport);
			} else {
				player.spigot().sendMessage(entry.toShowString());
			}
			
			return true;
		}

		@Override
		protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
			List<ReportEntry> entries = module.getEntries();
			ArrayList<String> ausgabe = new ArrayList<>(entries.size());
			
			if (args.size() == 1) for (ReportEntry e: entries) if (e.id.toString().toLowerCase().startsWith(args.get(0).toLowerCase())) ausgabe.add(e.id.toString());
			
			return ausgabe;
		}
	}
	
	private static class ReportsSanctionCommand extends SubCommand {
		
		private ReportModule module;

		public ReportsSanctionCommand(Main plugin, APIImplementation api, ReportModule module) {
			super(plugin, api);
			this.module = module;
		}

		@Override
		protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) {
			if (args.size() == 0) return false;
			
			ReportEntry entry = module.getEntry(UUID.fromString(args.get(0)));
			if (entry == null) {
				MessageFormatter.sendSimpleMessage(sender, noReport);
			} else {
				module.sanctionEntry(entry);
				MessageFormatter.sendSimpleMessage(sender, reportSanctioned);
			}
			
			return true;
		}

		@Override
		protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
			List<ReportEntry> entries = module.getEntries();
			ArrayList<String> ausgabe = new ArrayList<>(entries.size());
			
			if (args.size() == 1) for (ReportEntry e: entries) if (e.id.toString().toLowerCase().startsWith(args.get(0).toLowerCase())) ausgabe.add(e.id.toString());
			
			return ausgabe;
		}
	}
	
	private static class ReportsFreeCommand extends SubCommand {

		private ReportModule module;

		public ReportsFreeCommand(Main plugin, APIImplementation api, ReportModule module) {
			super(plugin, api);
			this.module = module;
		}

		@Override
		protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) {
			if (args.size() == 0) return false;
			
			ReportEntry entry = module.getEntry(UUID.fromString(args.get(0)));
			if (entry == null) {
				MessageFormatter.sendSimpleMessage(sender, noReport);
			} else {
				module.freeEntry(entry);
				MessageFormatter.sendSimpleMessage(sender, reportDeleted);
			}
			
			return true;
		}

		@Override
		protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
			List<ReportEntry> entries = module.getEntries();
			ArrayList<String> ausgabe = new ArrayList<>(entries.size());
			
			if (args.size() == 1) for (ReportEntry e: entries) if (e.id.toString().toLowerCase().startsWith(args.get(0).toLowerCase())) ausgabe.add(e.id.toString());
			
			return ausgabe;
		}
	}
	
}