package io.github.mh321productions.serverapi.module.chat.standardchats;

import java.util.ArrayList;

import io.github.mh321productions.serverapi.api.ServerAPI;
import org.bukkit.World;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.util.message.MessagePrefix;

/**
 * Eine Chatimplementation, die einen Weltchat nachbaut. <br/>
 * Bei den Flags wird {@link Flags#removeOnWorldChange} automatisch gesetzt.
 * @author 321Productions
 *
 */
public class WorldChat extends StandardChat {
	
	private World world;

	public WorldChat(byte flags, String name, ArrayList<MessagePrefix> prefixes, ServerAPI api, World world) {
		super((byte) (flags | Flags.removeOnWorldChange), name, prefixes, api);
	}

	@Override
	public boolean canSee(Player player) {
		return checkFlag(Flags.forceChat) ? false : player.getWorld().equals(world);
	}

	@Override
	public boolean canJoin(Player player) {
		return player.getWorld().equals(world);
	}

}
