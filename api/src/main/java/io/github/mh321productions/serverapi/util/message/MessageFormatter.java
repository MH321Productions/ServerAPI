package io.github.mh321productions.serverapi.util.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Eine Util-Klasse, die das Senden von Chat-Nachrichten an Spieler vereinfachen <br>
 * und vereinheitlichen soll. Mithilfe von {@link MessagePrefix} können Info- <br>
 * und Fehlermeldungen klarer strukturiert werden: <br>
 * &ltprefix 1&gt [prefix 2, 3, ...] » &ltmessage&gt
 * @author 321Productions
 *
 */
public final class MessageFormatter {
	
	/**
	 * Formatiert eine einfache Message, indem die Präfixe hinzugefügt werden
	 * @param message Die Message, die formatiert werden soll
	 * @param prefixes Die zu nutzenden Präfixe
	 * @return Die formatierte Message
	 */
	public static Message formatSimpleMessage(String message, MessagePrefix... prefixes) {
		String s = "";
		for (MessagePrefix p: prefixes) s += p.simplePrefix;
		
		return new Message(s + message);
	}
	
	/**
	 * Formatiert eine einfache Spigot Message, indem die Präfixe und die Message in ein neues Array geschrieben werden.
	 * @param message Die Spigot Message
	 * @param prefixes Die Präfixe
	 * @return Die formatierte Message
	 */
	public static Message formatSpigotMessage(BaseComponent[] message, MessagePrefix... prefixes) {
		if (prefixes.length == 0) return new Message(message);
		
		BaseComponent[] format = new BaseComponent[message.length + prefixes.length];
		int index = 0;
		
		for (MessagePrefix p: prefixes) {
			format[index] = p.spigotPrefix;
			index++;
		}
		
		for (BaseComponent m: message) {
			format[index] = m;
			index++;
		}
		
		return new Message(format);
	}
	
	/**
	 * Sendet einem Spieler eine formatierte Message
	 * @param target Der Spieler
	 * @param message Die formatierte Message
	 */
	public static void sendMessage(Player target, Message message) {
		target.spigot().sendMessage(message.type, message.components);
	}
	
	/**
	 * Sendet mehreren Spielern eine formatierte Message
	 * @param targets Die Spieler
	 * @param message Die formatierte Message
	 */
	public static void sendMessage(Iterable<? extends Player> targets, Message message) {
		for (Player p: targets) p.spigot().sendMessage(message.type, message.components);
	}
	
	/**
	 * Sendet einem {@link CommandSender} eine formatierte Message
	 * @param sender Der Empfänger
	 * @param message Die formatierte Message
	 */
	public static void sendSimpleMessage(CommandSender sender, Message message) {
		sender.spigot().sendMessage(message.components);
	}
	
	/**
	 * Setzt die XP Bar und die Level eines Spielers, um einen Timer darzustellen
	 * @param target Der Spieler
	 * @param timer Der aktuelle Wert des Timers
	 * @param max Der maximale Wert, den der Timer annehmen kann (normalerweise der Startwert)
	 */
	public static void setTimerXpBar(Player target, int timer, int max) {
		float exp = (float) timer / (float) max;
		target.setExp(exp);
		target.setLevel(timer);
	}
	
	/**
	 * Setzt die XP Bar und die Level mehrerer Spieler, um einen Timer darzustellen
	 * @param targets Die Spieler
	 * @param timer Der aktuelle Wert des Timers
	 * @param max Der maximale Wert, den der Timer annehmen kann (normalerweise der Startwert)
	 */
	public static void setTimerXpBar(Iterable<? extends Player> targets, int timer, int max) {
		float exp = (float) timer / (float) max;
		
		for (Player target: targets) {
			target.setExp(exp);
			target.setLevel(timer);
		}
	}
}
