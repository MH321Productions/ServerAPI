package io.github.mh321productions.serverapi.util.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Eine Util-Klasse, die den Inhalt einer formatierten Message enthält
 * @author 321Productions
 *
 */
public class Message {
	
	public static final Message emptyMessage = new Message("");
	
	public final BaseComponent[] components;
	public final ChatMessageType type;
	
	/**
	 * Erstellt eine Message aus einem String und der Art (Chat/Actionbar/System)
	 * @param message Die Message
	 * @param type Die Art der Message
	 */
	public Message(String message, ChatMessageType type) {
		components = new BaseComponent[] {new TextComponent(message)};
		this.type = type;
	}
	
	/**
	 * Erstellt eine einfache Message aus einem einzigen String
	 * @param simpleMessage Die Message
	 */
	public Message(String simpleMessage) {
		this(simpleMessage, ChatMessageType.CHAT);
	}
	
	/**
	 * Erstellt eine Message aus mehreren Spigot Komponenten.
	 * Für das Zusammenbauen empfiehlt sich der <br>
	 * {@link MessageBuilder}
	 * @param components Die Komponenten der Message
	 */
	public Message(BaseComponent[] components) {
		this.components = components;
		type = ChatMessageType.CHAT;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (BaseComponent c: components) {
			builder.append(c.toPlainText());
		}
		
		return builder.toString();
	}
}
