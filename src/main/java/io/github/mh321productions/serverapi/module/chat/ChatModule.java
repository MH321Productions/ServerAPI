package io.github.mh321productions.serverapi.module.chat;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.api.SubPlugin;
import io.github.mh321productions.serverapi.module.chat.command.ChatCommand;
import io.github.mh321productions.serverapi.module.chat.command.MsgCommand;
import io.github.mh321productions.serverapi.module.chat.standardchats.AbstractChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.google.common.collect.Lists;

import io.github.mh321productions.serverapi.module.Module;
import io.github.mh321productions.serverapi.module.ModuleStopFunction;
import io.github.mh321productions.serverapi.module.ModuleType;
import io.github.mh321productions.serverapi.module.chat.Chat.Flags;

public class ChatModule extends Module implements Listener {
	
	private HashMap<SubPlugin, ArrayList<Chat>> registeredChats;
	private HashMap<Player, Chat> playerChats;
	
	public ChatModule(Main plugin, APIImplementation api) {
		super(ModuleType.Chat, plugin, api);
	}

	@Override
	protected boolean init() {
		registeredChats = new HashMap<>();
		playerChats = new HashMap<>();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		//Commands registrieren
		ChatCommand cmd = new ChatCommand(plugin, api, this);
		MsgCommand msg = new MsgCommand(plugin, api, this);
		plugin.cmd.registerCommand("chat", cmd);
		plugin.cmd.registerCommand("message", msg);
		
		return true;
	}

	@Override
	protected void stopIntern() {
		
	}

	@Override
	public boolean registerSubPlugin(SubPlugin sub, ModuleStopFunction func) {
		return addIntern(sub, func);
	}

	@Override
	public void unregisterSubPlugin(@Nullable SubPlugin sub) {
		removeIntern(sub);
	}
	
	/**
	 * Registriert einen Chat, damit Spieler dem zugewiesen werden können
	 * @param sub Das Sub-Plugin, dem der Chat gehört
	 * @param chat Der Chat
	 */
	public void registerChat(@Nonnull SubPlugin sub, @Nonnull Chat chat) {
		if (sub == null || chat == null) return;
		
		if (!registeredChats.containsKey(sub)) {
			registeredChats.put(sub, Lists.newArrayList(chat));
		} else {
			ArrayList<Chat> l = registeredChats.get(sub);
			if (l.contains(chat)) return;
			
			l.add(chat);
		}
	}
	
	/**
	 * Schließt einen Chat und entfernt ihn
	 * @param sub Das Sub-Plugin, dem der Chat gehört
	 * @param chat Der Chat
	 */
	public void unregisterChat(@Nonnull SubPlugin sub, @Nonnull Chat chat) {
		if (sub == null || chat == null || !registeredChats.containsKey(sub)) return;
		
		ArrayList<Chat> l = registeredChats.get(sub);
		if (!l.remove(chat)) return;
		
		if (l.isEmpty()) registeredChats.remove(sub);
	}
	
	
	/**
	 * Weist einem Spieler einen Chat zu, sprich: Die Nachrichten werden nur über diesen Chat gesendet. <br/>
	 * Wenn der übergebene Chat <code>null</code> ist, wird der Spieler dem globalen (normalen) Chat zugewiesen. <br/>
	 * Sollte der Spieler bereits in einem Chat registriert sein, wird er nur entfernt, wenn er vom neuen <br/>
	 * Chat akzeptiert wird (siehe {@link AbstractChat#canJoin(Player)}).
	 * @param player Der Spieler
	 * @param chat Der neue Chat, oder <code>null</code>
	 * @return Ob die Zuweisung erfolgreich war
	 */
	public boolean setPlayerChat(@Nonnull Player player, @Nullable Chat chat) {
		if (player == null) return false;
		
		//Ist Spieler bereits in einem Chat? Ist der neue Chat nicht der globale Chat?
		boolean hasChat = playerChats.containsKey(player);
		boolean validChat = chat != null;
		boolean isAllowed = validChat ? chat.canJoin(player) : true; //Bei validem Chat wird gecheckt, bei globalem Chat immer true
		
		//Ist der Spieler nicht vom Chat zugelassen, passiert nichts
		if (!isAllowed) return false;
		
		if (hasChat) { //Bereits zugewiesen
			Chat old = playerChats.get(player);
			old.removeMember(player);
			
			if (validChat) { //Neuer Chat -> Ersetze Eintrag
				chat.addMember(player);
				playerChats.put(player, chat);
			} else { //Globaler Chat -> Entferne Eintrag
				playerChats.remove(player);
			}
		} else if (validChat) { //Nicht zugewiesen, neuer Chat -> Erstelle Eintrag
			chat.addMember(player);
			playerChats.put(player, chat);
		} //Wenn Der Spieler dem globalen Chat zugewiesen wird, aber in keinem ist, wird true zurückgegeben (es passiert nichts)
		
		return true;
	}
	
	/**
	 * Convenience-Funktion für {@link #setPlayerChat(Player, Chat)}: Ruft die Funktion mit <code>null</code> als Chat auf
	 * @param player Der Spieler, der in den globalen Chat gesetzt werden soll
	 */
	public void removePlayerChat(@Nonnull Player player) {
		setPlayerChat(player, null);
	}
	
	/**
	 * Sucht nach Chats
	 * @param internalName Der interne Name des Chats
	 * @return Der geladene Chat oder <code>null</code>
	 */
	public Chat getChat(String internalName) {
		for (ArrayList<Chat> l: registeredChats.values()) {
			for (Chat c: l) {
				if (c.getInternalName().equals(internalName)) return c;
			}
		}
		
		return null;
	}
	
	public ArrayList<Chat> getVisibleChats(Player player) {
		ArrayList<Chat> ret = new ArrayList<>();
		for (ArrayList<Chat> l: registeredChats.values()) {
			for (Chat c: l) {
				if (c.canSee(player)) ret.add(c);
			}
		}
		
		return ret;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		String msg = event.getMessage();
		
		//Spieler ist in Chat, Event canceln und Chat übernehmen lassen
		if (playerChats.containsKey(p)) {
			event.setCancelled(true);
			playerChats.get(p).sendMessage(p, msg);
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player p = event.getPlayer();
		if (playerChats.containsKey(p) && playerChats.get(p).checkFlag(Flags.removeOnWorldChange)) removePlayerChat(p);
	}
}
