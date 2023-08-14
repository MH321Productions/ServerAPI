package io.github.mh321productions.serverapi.module.chat.standardchats;

import java.util.ArrayList;

import io.github.mh321productions.serverapi.api.ServerAPI;
import io.github.mh321productions.serverapi.util.StringFormatter;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.util.message.MessagePrefix;
import io.github.mh321productions.serverapi.util.permission.PermissionHandler;
import io.github.mh321productions.serverapi.util.permission.Rank;

/**
 * Eine abstrakte Subklasse von {@link PrefixChat}, die den normalen Chat nachbaut. <br/>
 * @author 321Productions
 *
 */
public abstract class StandardChat extends PrefixChat {
	
	protected PermissionHandler handler;

	public StandardChat(byte flags, String name, ArrayList<MessagePrefix> prefixes, ServerAPI api) {
		super(flags, name, prefixes);
		handler = api.getPermissionHandler();
	}

	@Override
	protected void formatAfterPrefix(String message, Player sender) {
		Rank r = handler.getHighestRank(sender);
		builder.addComponent(StringFormatter.formatPlayerName(sender, r)); //Spielerrang
		builder.addComponent("§8: §f");
		builder.addComponent(message);
	}

}
