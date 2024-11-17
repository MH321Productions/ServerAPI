package io.github.mh321productions.serverapi.module.chat

import org.bukkit.entity.Player

/**
 * Hauptinterface für Minichats: <br></br>
 * Unterscheidet zwischen aktiven und passiven Mitgliedern, <br></br>
 * die im Chat sind (bzw. In diesen joinen können). <br></br>
 * Schreibt ein aktiver Spieler eine Nachricht, wird sie <br></br>
 * formatiert und allen aktiven Mitgliedern geschickt
 * @author 321Productions
 */
interface Chat {

    /**
     * Flags, die das Verhalten des Chats steuern. Sie können verodert werden
     * @author 321Productions
     */
    object Flags {
        /**
         * Der Spieler kann den Chat nicht selbstständig wechseln
         */
        const val forceChat: Byte = 0x1

        /**
         * Wechselt der Spieler die Welt (Eigenständig oder nicht), <br></br>
         * wird dieser automatisch entfernt und dem Standardchat zugewiesen
         */
        const val removeOnWorldChange: Byte = 0x2
    }

    /**
     * Gibt einen internen (command-tauglichen), einzigartigen Namen zurück
     * @return Der Name
     */
    val internalName: String

    //Mitglieder hinzufügen/entfernen
    fun addMember(newMember: Player)
    fun addMember(newMembers: Collection<Player>)
    fun removeMember(oldMember: Player)
    fun removeMember(remove: Collection<Player>)

    /**
     * Prüft, ob ein [Flags] gesetzt ist
     * @param flag Das zu prüfende Flag
     * @return Ob es gesetzt ist
     */
    fun checkFlag(flag: Byte): Boolean

    /**
     * Formatiert und sendet eine Nachricht an alle aktiven Mitglieder
     * @param player Der Spieler, der die Nachricht verschickt hat
     * @param message Die rohe Nachricht
     */
    fun sendMessage(player: Player, message: String)

    /**
     * Prüft, ob ein Spieler diesem Chat selbst joinen kann (z.B. per Command). <br></br>
     * Ist das der Fall, wird der Chat dem Spieler beim TabComlete angezeigt und <br></br>
     * er kann dem Chat beitreten (Im gegensatz zum vom Plugin zugewiesen werden)
     * @param player Der zu prüfende Spieler
     * @return Ob der Chat dem Spieler angezeigt werden soll
     */
    fun canSee(player: Player): Boolean

    /**
     * Diese Methode bestimmt, ob ein Spieler diesem Chat überhaupt beitreten darf. <br></br>
     * Wenn es der Chat nicht erlaubt, kann auch kein Plugin den Spieler hinzufügen
     * @param player Der zu prüfende Spieler
     * @return Ob er beitreten darf
     */
    fun canJoin(player: Player): Boolean

    override fun toString(): String
}
