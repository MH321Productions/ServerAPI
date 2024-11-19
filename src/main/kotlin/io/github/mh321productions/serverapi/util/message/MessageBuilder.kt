package io.github.mh321productions.serverapi.util.message

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Content

/**
 * Eine Util-Klasse, die das Erstellen von formatierten Messages vereinfachen soll.
 * Sie orientiert sich dabei am [StringBuilder] und funktioniert wie ein
 * Zustandsautomat: Die Events (Click/Hover) werden dem zuletzt hinzugefügten
 * Element zugewiesen, Präfixe der aktuellen Zeile. Darüber hinaus geben alle
 * Funktionsaufrufe das aktuelle Objekt zurück, sodass der folgende Code möglich wird:
 * <code> new MessageBuilder().addComponent("Hallo").setClickEvent(...).setHoverEvent(...).addComponent("Welt").addPrefix(...).build(); </code>
 * Letztendlich lässt sich mit [MessageBuilder.build] eine Message erstellen, die dann verschickt werden kann.
 * @author 321Productions
 */
class MessageBuilder {
    /**
     * Interne Klasse, die eine Messagezeile abbildet
     * @author 321Productions
     */
    private class Line {
        var prefixes = mutableListOf<MessagePrefix>()
        var message = mutableListOf<BaseComponent>()

        fun addPrefix(vararg prefix: MessagePrefix) = prefixes.addAll(prefix)
        fun addPrefix(prefix: Collection<MessagePrefix>) = prefixes.addAll(prefix)
        fun addMessage(comp: BaseComponent) = message.add(comp)

        fun setPrefixes(vararg prefix: MessagePrefix) {
            prefixes.clear()
            prefixes.addAll(prefix)
        }

        fun setPrefixes(prefix: Collection<MessagePrefix>) {
            prefixes.clear()
            prefixes.addAll(prefix)
        }
    }

    private var currentLine = Line()
    private val lines = mutableListOf(currentLine)
    private var currentComp: BaseComponent? = null

    /**
     * Fügt eine neue Komponente hinzu
     * @param comp Die Komponente, kann null sein
     * @return Die eigene Instanz
     */
    fun addComponent(comp: BaseComponent): MessageBuilder {
        currentLine.addMessage(comp)
        currentComp = comp

        return this
    }

    /**
     * Fügt eine neue Komponente hinzu
     * @param message Die Message, wird in [TextComponent] konvertiert
     * @return Die eigene Instanz
     */
    fun addComponent(message: String) = addComponent(TextComponent(message))

    /**
     * Weist der letzten Komponente ein ClickEvent zu
     * @param event Das ClickEvent
     * @return Die eigene Instanz
     */
    fun setClickEvent(event: ClickEvent): MessageBuilder {
        if (currentComp != null) currentComp!!.clickEvent = event

        return this
    }

    /**
     * Erstellt ein neues ClickEvent und weist es der letzten Komponente hinzu
     * @param action Die Aktion, die ausgeführt werden soll
     * @param value Der Wert, der anhand der Aktion verarbeitet wird
     * @return Die eigene Instanz
     */
    fun setClickEvent(action: ClickEvent.Action, value: String) = setClickEvent(ClickEvent(action, value))

    /**
     * Weist der letzten Komponente ein HoverEvent zu
     * @param event Das HoverEvent
     * @return Die eigene Instanz
     */
    fun setHoverEvent(event: HoverEvent): MessageBuilder {
        if (currentComp != null) currentComp!!.hoverEvent = event

        return this
    }

    /**
     * Erstellt ein neues HoverEvent und weist es der letzten Komponente hinzu
     * @param action Die Aktion, die ausgeführt werden soll
     * @param contents Die Inhalte des Events (übersetzen ist schwierig)
     * @return Die eigene Instanz
     */
    fun setHoverEvent(action: HoverEvent.Action, vararg contents: Content) = setHoverEvent(HoverEvent(action, *contents))

    /**
     * Fügt der aktuellen Zeile Präfixe hinzu
     * @param prefixes Die Präfixe
     * @return Die eigene Instanz
     */
    fun addPrefix(vararg prefixes: MessagePrefix): MessageBuilder {
        currentLine.addPrefix(*prefixes)

        return this
    }

    /**
     * Fügt der aktuellen Zeile Präfixe hinzu
     * @param prefixes Die Präfixe
     * @return Die eigene Instanz
     */
    fun addPrefix(prefixes: List<MessagePrefix>): MessageBuilder {
        currentLine.addPrefix(prefixes)

        return this
    }

    /**
     * Überschreibt die Präfixe der aktuellen Zeile
     * @param prefixes Die neuen Präfixe (leer lassen, um Präfixe zu löschen)
     * @return Die eigene Instanz
     */
    fun setPrefixes(vararg prefixes: MessagePrefix): MessageBuilder {
        currentLine.setPrefixes(*prefixes)

        return this
    }

    /**
     * Überschreibt die Präfixe der aktuellen Zeile
     * @param prefixes Die neuen Präfixe (leer lassen, um Präfixe zu löschen)
     * @return Die eigene Instanz
     */
    fun setPrefixes(prefixes: List<MessagePrefix>): MessageBuilder {
        currentLine.setPrefixes(prefixes)

        return this
    }

    /**
     * Fügt eine neue Zeile hinzu
     * @return Die eigene Instanz
     */
    fun newLine(): MessageBuilder {
        currentLine = Line()
        lines.add(currentLine)

        return this
    }

    /**
     * Fügt alle Komponenten zusammen und erstellt eine Message, die dann gesendet werden kann. <br></br>
     * Sollte der Builder leer sein, wird eine leere Message gesendet, sodass <br></br>
     *
     * new MessageBuilder().build()
     * immer eine valide Message liefert.
     * @return Die erstellte Message
     */
    fun build(): Message {
        if (lines.size == 1 && currentLine.message.isEmpty() && currentLine.prefixes.isEmpty()) return Message.emptyMessage

        val comp = mutableListOf<BaseComponent>()
        lines.forEachIndexed { index, line ->
            for (p in line.prefixes) {
                comp.add(p.spigotPrefix)
                comp.add(blankSpace)
            }

            comp.addAll(line.message)

            if (index != lines.size - 1) comp.add(newLine)
        }

        return Message(comp.toTypedArray())
    }

    companion object {
        private val newLine = TextComponent("\n")
        private val blankSpace = TextComponent(" ")
    }
}
