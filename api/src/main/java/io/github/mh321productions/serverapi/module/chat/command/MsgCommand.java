package io.github.mh321productions.serverapi.module.chat.command;

import java.util.List;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.command.APISubCommand;
import io.github.mh321productions.serverapi.command.SubCommand;
import io.github.mh321productions.serverapi.util.message.MessageBuilder;
import io.github.mh321productions.serverapi.util.message.MessageFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.module.chat.ChatModule;
import io.github.mh321productions.serverapi.util.message.Message;
import io.github.mh321productions.serverapi.util.message.MessagePrefix;
import io.github.mh321productions.serverapi.util.message.MessagePrefix.PrefixFormat;
import io.github.mh321productions.serverapi.util.permission.PermissionHandler;
import net.md_5.bungee.api.chat.TextComponent;

public class MsgCommand extends APISubCommand {
	
	private static final MessagePrefix prefix = new MessagePrefix("§3MSG", PrefixFormat.Main);
	private static final TextComponent arrow = new TextComponent(" §8\u279C "), dp = new TextComponent("§8: §f");
	
	private ChatModule module;
	private PermissionHandler perms;

	public MsgCommand(Main plugin, APIImplementation api, ChatModule module) {
		super(plugin, api);
		this.module = module;
		perms = api.getPermissionHandler();
		
		//§3MSG §8>> <Von mit Rangfarbe> §8\u279C <Zu mit Rangfarbe>§8: §f<Nachricht>
	}

	@Override
	protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		if (args.size() < 2) {
			MessageFormatter.sendSimpleMessage(sender, StdMessages.argsTooFew);
			return false;
		}
		
		Player target = plugin.getServer().getPlayer(args.get(0));
		if (target == null) {
			MessageFormatter.sendSimpleMessage(sender, StdMessages.noPlayerWithName);
			return true;
		}
		
		MessageBuilder builder = new MessageBuilder().setPrefixes(prefix);
		
		//Absender
		if (isPlayer) {
			Player p = (Player) sender;
			builder.addComponent(perms.getHighestRank(p).getColor() + p.getName());
		} else {
			builder.addComponent("§9Server: " + sender.getName());
		}
		
		//Pfeil und Ziel
		builder
		.addComponent(arrow)
		.addComponent(perms.getHighestRank(target).getColor() + target.getName())
		.addComponent(dp);
		
		//Message
		for (int i = 1; i < args.size(); i++) builder.addComponent(args.get(i) + " ");
		
		Message msg = builder.build();
		
		MessageFormatter.sendMessage(target, msg);
		if (isPlayer) MessageFormatter.sendMessage((Player) sender, msg);
		else MessageFormatter.sendSimpleMessage(sender, msg);
		
		return true;
	}

	@Override
	protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		if (args.size() == 1) return tabPlayers(args.get(0));
		
		return emptyList;
	}

}
