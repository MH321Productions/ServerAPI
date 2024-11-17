package io.github.mh321productions.serverapi.util.visibility

import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 * Utility-Klasse: Bestimmt, welche Spieler etwas sehen oder an etwas teilnehmen können
 * (z.B. NPC oder Chat).
 *
 * Es können Listener registriert werden, die per Callback benachrichtigt werden, wenn Spieler
 * hinzugefügt/entfernt werden.
 *
 * @see StandardVisibilityGroups
 */
abstract class VisibilityGroup {
    data class VisibilityListener(val addListener: Consumer<Player>, val removeListener: Consumer<Player>)

    protected val players = mutableSetOf<Player>()

    private val listeners = mutableSetOf<VisibilityListener>()

    fun addListener(listener: VisibilityListener) {
        listeners.add(listener)
    }

    fun addListener(addListener: Consumer<Player>, removeListener: Consumer<Player>): VisibilityListener {
        val res = VisibilityListener(addListener, removeListener)
        listeners.add(res)

        return res
    }

    fun removeListener(listener: VisibilityListener) {
        listeners.remove(listener)
    }

    fun getActivePlayers() = players.toSet()

    protected fun notifyAddPlayer(player: Player) {
        listeners.forEach { it.addListener.accept(player) }
    }

    protected fun notifyRemovePlayer(player: Player) {
        listeners.forEach { it.removeListener.accept(player) }
    }
}