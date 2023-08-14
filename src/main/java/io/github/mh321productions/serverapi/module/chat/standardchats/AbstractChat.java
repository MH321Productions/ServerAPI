package io.github.mh321productions.serverapi.module.chat.standardchats;

import java.util.ArrayList;
import java.util.Collection;

import io.github.mh321productions.serverapi.util.message.MessageFormatter;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.module.chat.Chat;
import io.github.mh321productions.serverapi.util.message.Message;

/**
 * Abstrakter Wrapper für das {@link Chat}-Interface
 * @author 321Productions
 *
 */
public abstract class AbstractChat implements Chat {
	
	protected ArrayList<Player> members;
	
	private byte actionFlags;
	private String internalName;
	
	public AbstractChat(byte flags, String name) {
		actionFlags = flags;
		internalName = name;
		members = new ArrayList<>();
	}
	
	public String getInternalName() {
		return internalName;
	}
	
	public void addMember(Player newMember) {members.add(newMember);}
	public void addMember(Collection<? extends Player> newMembers) {members.addAll(newMembers);}
	public void removeMember(Player oldMember) {members.remove(oldMember);}
	public void removeMember(Collection<? extends Player> remove) {members.removeAll(remove);}
	
	/**
	 * Formatiert die Nachricht intern (soll von {@link #sendMessage(Player, String)} aufgerufen werden)
	 * @param player Der Spieler, der die Nachricht verschickt hat
	 * @param message Die rohe Nachricht
	 * @return Die formatierte Message, die verschickt werden kann
	 */
	protected abstract Message formatMessage(Player player, String message);
	
	public boolean checkFlag(byte flag) {
		return (actionFlags & flag) != 0;
	}
	
	@Override
	public void sendMessage(Player sender, String message) {
		Message send = formatMessage(sender, message);
		MessageFormatter.sendMessage(members, send);
	}
	
	@Override
	public String toString() {
		return internalName;
	}
}
