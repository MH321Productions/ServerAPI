package io.github.mh321productions.serverapi.module.chat.command;

import java.util.List;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.command.SubCommand;
import io.github.mh321productions.serverapi.util.message.MessageBuilder;
import io.github.mh321productions.serverapi.util.message.MessageFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.module.chat.Chat;
import io.github.mh321productions.serverapi.module.chat.ChatModule;
import io.github.mh321productions.serverapi.util.message.Message;
import io.github.mh321productions.serverapi.util.message.MessagePrefix;

public class ChatCommand extends SubCommand {
	
	private ChatModule module;
	private static final Message noChatWithName = new MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cEs existiert kein Chat mit diesem Namen!").build();
	private static final Message noEnterChat = new MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cDu kannst diesem Chat nicht beitreten!").build();

	public ChatCommand(Main plugin, APIImplementation api, ChatModule module) {
		super(plugin, api);
		this.module = module;
	}

	@Override
	protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		if (!isPlayer) {
			MessageFormatter.sendSimpleMessage(sender, StdMessages.onlyPlayers);
			return true;
		} else if (args.isEmpty()) {
			MessageFormatter.sendSimpleMessage(sender, StdMessages.argsTooFew);
			return false;
		}
		
		Player player = (Player) sender;
		Chat c = module.getChat(args.get(0));
		
		if (c == null) MessageFormatter.sendMessage(player, noChatWithName);
		else if (!module.setPlayerChat(player, c)) MessageFormatter.sendMessage(player, noEnterChat);
		
		return true;
	}

	@Override
	protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		if (!isPlayer) return emptyList;
		
		return tabCollection(args.get(0), module.getVisibleChats((Player) sender));
	}

}
