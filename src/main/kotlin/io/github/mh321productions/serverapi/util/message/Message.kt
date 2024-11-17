package io.github.mh321productions.serverapi.util.message

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent

/**
 * Eine Util-Klasse, die den Inhalt einer formatierten Message enthält
 * @author 321Productions
 */
class Message {

    companion object {
        @JvmField
        val emptyMessage = Message("")
    }

    val components: Array<BaseComponent>
    val type: ChatMessageType

    /**
     * Erstellt eine Message aus einem String und der Art (Chat/Actionbar/System)
     * @param message Die Message
     * @param type Die Art der Message
     */
    constructor(message: String, type: ChatMessageType) {
        components = arrayOf(TextComponent(message))
        this.type = type
    }

    /**
     * Erstellt eine einfache Message aus einem einzigen String
     * @param simpleMessage Die Message
     */
    constructor(simpleMessage: String) : this(simpleMessage, ChatMessageType.CHAT)

    /**
     * Erstellt eine Message aus mehreren Spigot Komponenten.
     * Für das Zusammenbauen empfiehlt sich der <br></br>
     * [MessageBuilder]
     * @param components Die Komponenten der Message
     */
    constructor(components: Array<BaseComponent>) {
        this.components = components
        type = ChatMessageType.CHAT
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (c in components) {
            builder.append(c.toPlainText())
        }

        return builder.toString()
    }
}
