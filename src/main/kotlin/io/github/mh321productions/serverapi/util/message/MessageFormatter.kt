package io.github.mh321productions.serverapi.util.message

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Eine Util-Klasse, die das Senden von Chat-Nachrichten an Spieler vereinfachen <br></br>
 * und vereinheitlichen soll. Mithilfe von [MessagePrefix] können Info- <br></br>
 * und Fehlermeldungen klarer strukturiert werden: <br></br>
 * &lt;prefix 1&gt; [prefix 2, 3, ...] » &lt;message&gt;
 * @author 321Productions
 */
object MessageFormatter {
    /**
     * Formatiert eine einfache Message, indem die Präfixe hinzugefügt werden
     * @param message Die Message, die formatiert werden soll
     * @param prefixes Die zu nutzenden Präfixe
     * @return Die formatierte Message
     */
    @JvmStatic
    fun formatSimpleMessage(message: String, vararg prefixes: MessagePrefix): Message {
        val s = prefixes
            .map { it.simplePrefix }
            .reduce { a, b -> a + b }

        return Message(s + message)
    }

    /**
     * Formatiert eine einfache Spigot Message, indem die Präfixe und die Message in ein neues Array geschrieben werden.
     * @param message Die Spigot Message
     * @param prefixes Die Präfixe
     * @return Die formatierte Message
     */
    @JvmStatic
    fun formatSpigotMessage(message: Array<BaseComponent>, vararg prefixes: MessagePrefix): Message {
        if (prefixes.isEmpty()) return Message(message)

        val spigotPrefixes = prefixes
            .map { it.spigotPrefix }
            .toTypedArray()

        return Message(arrayOf(*spigotPrefixes, *message))
    }

    /**
     * Sendet einem Spieler eine formatierte Message
     * @param target Der Spieler
     * @param message Die formatierte Message
     */
	@JvmStatic
	fun sendMessage(target: Player, message: Message) {
        target.spigot().sendMessage(message.type, *message.components)
    }

    /**
     * Sendet mehreren Spielern eine formatierte Message
     * @param targets Die Spieler
     * @param message Die formatierte Message
     */
    @JvmStatic
    fun sendMessage(targets: Collection<Player>, message: Message) {
        for (p in targets) p.spigot().sendMessage(message.type, *message.components)
    }

    /**
     * Sendet einem [CommandSender] eine formatierte Message
     * @param sender Der Empfänger
     * @param message Die formatierte Message
     */
	@JvmStatic
	fun sendSimpleMessage(sender: CommandSender, message: Message) {
        sender.spigot().sendMessage(*message.components)
    }

    /**
     * Setzt die XP Bar und die Level eines Spielers, um einen Timer darzustellen
     * @param target Der Spieler
     * @param timer Der aktuelle Wert des Timers
     * @param max Der maximale Wert, den der Timer annehmen kann (normalerweise der Startwert)
     */
    @JvmStatic
    fun setTimerXpBar(target: Player, timer: Int, max: Int) {
        val exp = timer.toFloat() / max.toFloat()
        target.exp = exp
        target.level = timer
    }

    /**
     * Setzt die XP Bar und die Level mehrerer Spieler, um einen Timer darzustellen
     * @param targets Die Spieler
     * @param timer Der aktuelle Wert des Timers
     * @param max Der maximale Wert, den der Timer annehmen kann (normalerweise der Startwert)
     */
    @JvmStatic
    fun setTimerXpBar(targets: Collection<Player>, timer: Int, max: Int) {
        val exp = timer.toFloat() / max.toFloat()

        for (target in targets) {
            target.exp = exp
            target.level = timer
        }
    }
}
