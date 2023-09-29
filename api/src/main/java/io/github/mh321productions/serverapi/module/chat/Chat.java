package io.github.mh321productions.serverapi.module.chat;

import java.util.Collection;

import org.bukkit.entity.Player;

/**
 * Hauptinterface für Minichats: <br/>
 * Unterscheidet zwischen aktiven und passiven Mitgliedern, <br/>
 * die im Chat sind (bzw. In diesen joinen können). <br/>
 * Schreibt ein aktiver Spieler eine Nachricht, wird sie <br/>
 * formatiert und allen aktiven Mitgliedern geschickt
 * @author 321Productions
 *
 */
public interface Chat {
	
	/**
	 * Flags, die das Verhalten des Chats steuern. Sie können verodert werden
	 * @author 321Productions
	 *
	 */
	public static final class Flags {
		/**
		 * Der Spieler kann den Chat nicht selbstständig wechseln
		 */
		public static final byte forceChat = 0x1;
		
		/**
		 * Wechselt der Spieler die Welt (Eigenständig oder nicht), <br/>
		 * wird dieser automatisch entfernt und dem Standardchat zugewiesen
		 */
		public static final byte removeOnWorldChange = 0x2;
	}
	
	/**
	 * Gibt einen internen (command-tauglichen), einzigartigen Namen zurück
	 * @return Der Name
	 */
	public String getInternalName();
	
	//Mitglieder hinzufügen/entfernen
	public void addMember(Player newMember);
	public void addMember(Collection<? extends Player> newMembers);
	public void removeMember(Player oldMember);
	public void removeMember(Collection<? extends Player> remove);
	
	/**
	 * Prüft, ob ein {@link Flags} gesetzt ist
	 * @param flag Das zu prüfende Flag
	 * @return Ob es gesetzt ist
	 */
	public boolean checkFlag(byte flag);
	
	/**
	 * Formatiert und sendet eine Nachricht an alle aktiven Mitglieder
	 * @param player Der Spieler, der die Nachricht verschickt hat
	 * @param message Die rohe Nachricht
	 */
	public void sendMessage(Player player, String message);
	
	/**
	 * Prüft, ob ein Spieler diesem Chat selbst joinen kann (z.B. per Command). <br/>
	 * Ist das der Fall, wird der Chat dem Spieler beim TabComlete angezeigt und <br/>
	 * er kann dem Chat beitreten (Im gegensatz zum vom Plugin zugewiesen werden)
	 * @param player Der zu prüfende Spieler
	 * @return Ob der Chat dem Spieler angezeigt werden soll
	 */
	public boolean canSee(Player player);
	
	/**
	 * Diese Methode bestimmt, ob ein Spieler diesem Chat überhaupt beitreten darf. <br/>
	 * Wenn es der Chat nicht erlaubt, kann auch kein Plugin den Spieler hinzufügen
	 * @param player Der zu prüfende Spieler
	 * @return Ob er beitreten darf
	 */
	public boolean canJoin(Player player);
	
	@Override
	String toString();
}
