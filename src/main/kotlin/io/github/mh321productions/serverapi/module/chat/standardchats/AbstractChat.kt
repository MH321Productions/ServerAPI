package io.github.mh321productions.serverapi.module.chat.standardchats

import io.github.mh321productions.serverapi.module.chat.Chat
import io.github.mh321productions.serverapi.util.message.Message
import io.github.mh321productions.serverapi.util.message.MessageFormatter.sendMessage
import org.bukkit.entity.Player
import kotlin.experimental.and

/**
 * Abstrakter Wrapper für das [Chat]-Interface
 * @author 321Productions
 */
abstract class AbstractChat(private val actionFlags: Byte, override val internalName: String) : Chat {

    protected var members = mutableListOf<Player>()

    override fun addMember(newMember: Player) {
        members.add(newMember)
    }

    override fun addMember(newMembers: Collection<Player>) {
        members.addAll(newMembers)
    }

    override fun removeMember(oldMember: Player) {
        members.remove(oldMember)
    }

    override fun removeMember(remove: Collection<Player>) {
        members.removeAll(remove)
    }

    override fun checkFlag(flag: Byte) = (actionFlags and flag) != 0.toByte()

    override fun sendMessage(player: Player, message: String) {
        val send = formatMessage(player, message)
        sendMessage(members, send)
    }

    override fun toString() = internalName

    /**
     * Formatiert die Nachricht intern (soll von [.sendMessage] aufgerufen werden)
     * @param player Der Spieler, der die Nachricht verschickt hat
     * @param message Die rohe Nachricht
     * @return Die formatierte Message, die verschickt werden kann
     */
    protected abstract fun formatMessage(player: Player, message: String): Message
}
