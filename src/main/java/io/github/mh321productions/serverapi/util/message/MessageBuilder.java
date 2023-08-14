package io.github.mh321productions.serverapi.util.message;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;

/**
 * Eine Util-Klasse, die das Erstellen von formatierten Messages vereinfachen soll. <br>
 * Sie orientiert sich dabei am {@link StringBuilder} und funktioniert wie ein <br>
 * Zustandsautomat: Die Events (Click/Hover) werden dem zuletzt hinzugefügten <br>
 * Element zugewiesen, Präfixe der aktuellen Zeile. Darüber hinaus geben alle <br>
 * Funktionsaufrufe das aktuelle Objekt zurück, sodass der folgende Code möglich wird: <br>
 * <p> new MessageBuilder().addComponent("Hallo").setClickEvent(...).setHoverEvent(...)<br>
 * .addComponent("Welt").addPrefix(...).build(); </p>
 * Letztendlich lässt sich mit {@link #build()} eine Message erstellen, die dann verschickt werden kann.
 * @author 321Productions
 *
 */
public class MessageBuilder {
	
	private static final TextComponent newLine = new TextComponent("\n");
	private static final TextComponent blankSpace = new TextComponent(" ");
	
	private ArrayList<Line> lines;
	private Line currentLine;
	private BaseComponent currentComp;
	
	/**
	 * Interne Klasse, die eine Messagezeile abbildet
	 * @author 321Productions
	 *
	 */
	private static class Line {
		public ArrayList<MessagePrefix> prefixes;
		public ArrayList<BaseComponent> message;
		
		public Line() {
			prefixes = new ArrayList<>();
			message = new ArrayList<>();
		}
		
		public void addPrefix(MessagePrefix... prefix) {
			for (MessagePrefix p: prefix) prefixes.add(p);
		}
		
		public void addPrefix(List<MessagePrefix> prefix) {
			prefixes.addAll(prefix);
		}
		
		public void setPrefixes(MessagePrefix... prefix) {
			prefixes.clear();
			addPrefix(prefix);
		}
		
		public void setPrefixes(List<MessagePrefix> prefix) {
			prefixes.clear();
			prefixes.addAll(prefix);
		}
		
		public void addMessage(BaseComponent comp) {
			if (comp != null) message.add(comp);
		}
		
		public int size() {
			return (prefixes.size() * 2) + message.size();
		}
	}
	
	public MessageBuilder() {
		lines = new ArrayList<>();
		currentLine = new Line();
		lines.add(currentLine);
		currentComp = null;
	}
	
	/**
	 * Fügt eine neue Komponente hinzu
	 * @param comp Die Komponente, kann null sein
	 * @return Die eigene Instanz
	 */
	public MessageBuilder addComponent(@Nullable BaseComponent comp) {
		currentLine.addMessage(comp);
		currentComp = comp;
		
		return this;
	}
	
	/**
	 * Fügt eine neue Komponente hinzu
	 * @param message Die Message, wird in {@link TextComponent} konvertiert
	 * @return Die eigene Instanz
	 */
	public MessageBuilder addComponent(String message) {
		return addComponent(new TextComponent(message));
	}
	
	/**
	 * Weist der letzten Komponente ein ClickEvent zu
	 * @param event Das ClickEvent
	 * @return Die eigene Instanz
	 */
	public MessageBuilder setClickEvent(ClickEvent event) {
		if (currentComp != null) currentComp.setClickEvent(event);
		
		return this;
	}
	
	/**
	 * Erstellt ein neues ClickEvent und weist es der letzten Komponente hinzu
	 * @param action Die Aktion, die ausgeführt werden soll
	 * @param value Der Wert, der anhand der Aktion verarbeitet wird
	 * @return Die eigene Instanz
	 */
	public MessageBuilder setClickEvent(ClickEvent.Action action, String value) {
		return setClickEvent(new ClickEvent(action, value));
	}
	
	/**
	 * Weist der letzten Komponente ein HoverEvent zu
	 * @param event Das HoverEvent
	 * @return Die eigene Instanz
	 */
	public MessageBuilder setHoverEvent(HoverEvent event) {
		if (currentComp != null) currentComp.setHoverEvent(event);
		
		return this;
	}
	
	/**
	 * Erstellt ein neues HoverEvent und weist es der letzten Komponente hinzu
	 * @param action Die Aktion, die ausgeführt werden soll
	 * @param contents Die Inhalte des Events (übersetzen ist schwierig)
	 * @return Die eigene Instanz
	 */
	public MessageBuilder setHoverEvent(HoverEvent.Action action, Content... contents) {
		return setHoverEvent(new HoverEvent(action, contents));
	}
	
	/**
	 * Fügt der aktuellen Zeile Präfixe hinzu 
	 * @param prefixes Die Präfixe
	 * @return Die eigene Instanz
	 */
	public MessageBuilder addPrefix(MessagePrefix... prefixes) {
		if (prefixes.length != 0 && currentLine != null) currentLine.addPrefix(prefixes); 
		
		return this;
	}
	
	/**
	 * Fügt der aktuellen Zeile Präfixe hinzu 
	 * @param prefixes Die Präfixe
	 * @return Die eigene Instanz
	 */
	public MessageBuilder addPrefix(List<MessagePrefix> prefixes) {
		if (!prefixes.isEmpty() && currentLine != null) currentLine.addPrefix(prefixes); 
		
		return this;
	}
	
	/**
	 * Überschreibt die Präfixe der aktuellen Zeile
	 * @param prefixes Die neuen Präfixe (leer lassen, um Präfixe zu löschen)
	 * @return Die eigene Instanz
	 */
	public MessageBuilder setPrefixes(MessagePrefix... prefixes) {
		if (currentLine != null) currentLine.setPrefixes(prefixes);
		
		return this;
	}
	
	/**
	 * Überschreibt die Präfixe der aktuellen Zeile
	 * @param prefixes Die neuen Präfixe (leer lassen, um Präfixe zu löschen)
	 * @return Die eigene Instanz
	 */
	public MessageBuilder setPrefixes(List<MessagePrefix> prefixes) {
		if (currentLine != null) currentLine.setPrefixes(prefixes);
		
		return this;
	}
	
	/**
	 * Fügt eine neue Zeile hinzu
	 * @return Die eigene Instanz
	 */
	public MessageBuilder newLine() {
		currentLine.addMessage(newLine);
		
		currentLine = new Line();
		lines.add(currentLine);
		
		return this;
	}
	
	/**
	 * Fügt alle Komponenten zusammen und erstellt eine Message, die dann gesendet werden kann. <br>
	 * Sollte der Builder leer sein, wird eine leere Message gesendet, so dass <br>
	 * <p>new MessageBuilder().build() </p>
	 * immer eine valide Message liefert.
	 * @return Die erstellte Message
	 */
	public Message build() {
		//Größe des Arrays berechnen
		int size = 0;
		for (Line l: lines) size += l.size();
		
		if (size == 0) return Message.emptyMessage;
		
		size += (lines.size() - 1);
		
		BaseComponent[] comp = new BaseComponent[size];
		int index = 0;
		for (Line l: lines) {
			for (MessagePrefix p: l.prefixes) {
				comp[index] = p.spigotPrefix;
				comp[index + 1] = blankSpace;
				index += 2;
			}
			
			for (BaseComponent c: l.message) {
				comp[index] = c;
				index++;
			}
			
			if (index != size) {
				comp[index] = newLine;
				index++;
			}
		}
		
		return new Message(comp);
	}
}
