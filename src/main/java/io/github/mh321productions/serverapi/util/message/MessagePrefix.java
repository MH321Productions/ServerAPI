package io.github.mh321productions.serverapi.util.message;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Diese Klasse definiert ein Präfix, welches vom {@link MessageFormatter} <br>
 * verwendet wird, um Chat-Messages an Spieler zu vereinheitlichen. <br>
 * Jedes Sub-Plugin kann eigene Präfixe erstellen und ohne Registrierung <br>
 * direkt nutzen.
 * @author 321Productions
 *
 */
public final class MessagePrefix {
	
	/**
	 * Ein Enum, das die verschiedenen Präfixformate enthält
	 * @author 321Productions
	 *
	 */
	public enum PrefixFormat {
		/**
		 * Ein Hauptpräfix, das standardmäßig im Chat gesendet werden: &ltPräfix&gt §8»§r
		 */
		Main,
		
		/**
		 * Ein Nebenpräfix, meist kombiniert mit anderen Präfixen: §8[&ltPräfix&gt§8]§r
		 */
		Secondary,
		
		/**
		 * Keine spezielle Formatierung
		 */
		None
	}
	
	private static final String mainFormat = " §8»§r", secondaryFormat = "§8[§r%s§8]§r";
	
	public final String simplePrefix;
	public final BaseComponent spigotPrefix;
	
	/**
	 * Erstellt ein neues Präfix, entweder mit oder ohne Chat-Format
	 * @param prefix Das rohe Präfix
	 * @param useFormat Ob das Chat-Format hinzugefügt werden soll
	 */
	public MessagePrefix(String prefix, PrefixFormat format) {
		switch (format) {
			case Main:
				simplePrefix = prefix + mainFormat;
				break;
			case Secondary:
				simplePrefix = String.format(secondaryFormat, prefix);
				break;
			default:
				simplePrefix = prefix;
		}
		
		spigotPrefix = new TextComponent(simplePrefix);
	}
	
	/**
	 * Das Server-Präfix: <br/>
	 * <span style="color: #5555FF; font-size: 30">Server</span> <span style="color: #555555; font-size: 30">» </span>...
	 */
	public static final MessagePrefix Server = new MessagePrefix("§9Server", PrefixFormat.Main);
	
	/**
	 * Das [+]-Präfix: <br/>
	 * <span style="color: #555555; font-size: 30">[</span><span style="color:#55FF55; font-size: 30; font-weight:bold">+</span><span style="color: #555555; font-size: 30">]</span> ...
	 */
	public static final MessagePrefix Plus = new MessagePrefix("§a§l+", PrefixFormat.Secondary);
	
	/**
	 * Das [-]-Präfix: <br/>
	 * <span style="color: #555555; font-size: 30">[</span><span style="color:#FF5555; font-size: 30; font-weight:bold">-</span><span style="color: #555555; font-size: 30">]</span> ...
	 */
	public static final MessagePrefix Minus = new MessagePrefix("§c§l-", PrefixFormat.Secondary);
	
	/**
	 * Ein leeres Präfix
	 */
	public static final MessagePrefix Plain = new MessagePrefix("", PrefixFormat.None);
}
