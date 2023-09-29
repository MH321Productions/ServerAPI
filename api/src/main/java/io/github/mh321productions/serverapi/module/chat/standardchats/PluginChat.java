package io.github.mh321productions.serverapi.module.chat.standardchats;

import java.util.ArrayList;

import io.github.mh321productions.serverapi.api.ServerAPI;
import io.github.mh321productions.serverapi.api.SubPlugin;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.util.message.MessagePrefix;

public class PluginChat extends StandardChat {
	
	private SubPlugin sub;

	public PluginChat(byte flags, String name, ArrayList<MessagePrefix> prefixes, ServerAPI api, SubPlugin sub) {
		super(flags, name, prefixes, api);
		this.sub = sub;
	}

	@Override
	public boolean canSee(Player player) {
		return checkFlag(Flags.forceChat) ? false : sub.isPlayerInGame(player);
	}

	@Override
	public boolean canJoin(Player player) {
		return sub.isPlayerInGame(player);
	}

}
