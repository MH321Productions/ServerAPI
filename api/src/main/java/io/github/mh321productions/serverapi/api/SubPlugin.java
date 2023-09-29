package io.github.mh321productions.serverapi.api;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.mh321productions.serverapi.module.report.ReportModule;

/**
 * Das Interface, das die Sub-Plugins implementieren müssen, <br/>
 * um mit der API zu kommunizieren.
 * @author 321Productions
 */
public interface SubPlugin {
	
	/**
	 * Ein Enum zum Filtern verschiedener Spielerlisten.
	 * @author 321Productions
	 *
	 */
    enum PlayerFilter {
		/**
		 * Alle Spieler in der Welt, auch nicht im Plugin registrierte und versteckte Spieler. <br/>
		 * Quasi äquivalent zu {@link World#getPlayers()}.
		 */
		AllPlayers,
		
		/**
		 * Alle im Plugin registrierten Spieler in der Welt, auch nicht sichtbare <br/>
		 * (vorm Spieler versteckte) Spieler. (z.B. Getötete Spieler im Spectator). <br/>
		 * Keine nicht registrierten Spieler (wie Admins oder Mods im Vanish Spectator).
		 */
		AllRegistered,
		
		/**
		 * Alle im Plugin registrierten Spieler in der Welt, die der Spieler auch sehen kann.
		 */
		OnlyVisible
	}
	
	/**
	 * Gibt den internen Namen des Sub-Plugins zurück, siehe {@link Plugin#getName()}
	 * @return Der Plugin-Name des Sub-Plugins
	 */
    String getName();
	
	/**
	 * Gibt den Datenordner des Sub-Plugins zurück, siehe {@link Plugin#getDataFolder()}
	 * @return Der Datenordner
	 */
    File getDataFolder();
	
	/**
	 * Gibt den internen Logger des Sub-Plugins zurück, siehe {@link Plugin#getLogger()}
	 * @return Den internen Logger
	 */
    Logger getLogger();
	
	/**
	 * Gibt zurück, ob ein Spieler unter Kontrolle des Plugins ist (a.k.a. im Spiel/in der Wartelobby etc.). <br/>
	 * Wird für Pluginspezifische Operationen benutzt (siehe z.B. {@link ReportModule})
	 * @param player Der abzufragende Spieler
	 * @return Ob der Spieler zurzeit unter Kontrolle des Plugins ist
	 */
    boolean isPlayerInGame(Player player);
	
	/**
	 * Gibt den Namen der abgefragten Welt zurück. Wenn ein Weltgenerator wie bei z.B. Skywars oder <br/>
	 * BlockBall vorhanden ist, der die Welten dynamisch generiert, soll der Name der Weltvorlage <br/>
	 * zurückgegeben werden, sonst einfach der Name der Welt ({@link World#getName()}).
	 * @param world Die von der API abgefragte Welt
	 * @return Der Weltname oder der Name der Vorlage
	 */
    String getWorldName(World world);
	
	/**
	 * Gibt alle Spieler relativ zu einem zurück, nach verschiedenen Filterkriterien. Beispielsweise alle Spieler, die <br/>
	 * dieser reporten kann.
	 * @param focus Der Spieler, von dem die Suche ausgehen soll
	 * @param filter Der Filter, der angewendet wird (siehe {@link PlayerFilter})
	 * @return Eine Liste aller dem Filter entsprechenden Spieler, oder eine leere Liste
	 */
    List<Player> getPlayersRelativeTo(Player focus, PlayerFilter filter);
}
