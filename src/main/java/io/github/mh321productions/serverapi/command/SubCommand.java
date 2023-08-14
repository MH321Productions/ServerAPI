package io.github.mh321productions.serverapi.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.util.message.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.util.message.Message;
import io.github.mh321productions.serverapi.util.message.MessagePrefix;

/**
 * Intern: Eine Klasse für einen (Sub-)Command. Durch Rekursion lässt sich <br>
 * ein Tiefenbaum erstellen.
 * @author 321Productions
 *
 */
public abstract class SubCommand {
	
	/**
	 * Eine Sammlung von oft benutzten Command-Messages
	 * @author 321Productions
	 *
	 */
	protected static final class StdMessages {
		public static final Message onlyPlayers = new MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("Nur Spieler dürfen diesen Command ausführen").build();
		public static final Message argsTooFew = new MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cZu wenig Argumente!").build();
		public static final Message argsTooMuch = new MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cZu viele Argumente!").build();
		public static final Message noPlayerWithName = new MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cEs existiert kein Spieler mit diesem Namen!").build();
		public static final Message noWorldWithName = new MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cEs existiert keine Welt mit diesem Namen!").build();
		public static final Message noPermission = new MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cDu darfst diesen Befehl nicht ausführen!").build();
	}
	
	protected static final ArrayList<String> emptyList = new ArrayList<>();
	
	/**
	 * Alle Subcommands (rekursiv)
	 */
	protected HashMap<String, SubCommand> sub = new HashMap<>();
	protected Main plugin;
	protected APIImplementation api;
	
	public SubCommand(Main plugin, APIImplementation api) {
		this.plugin = plugin;
		this.api = api;
	}
	
	protected abstract boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args);
	protected abstract List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args);
	
	public boolean onExecute(CommandSender sender, boolean isPlayer, List<String> args) {
		if (args.size() > 0) {
			for (Entry<String, SubCommand> e: sub.entrySet()) {
				if (e.getKey().equalsIgnoreCase(args.get(0))) {
					return e.getValue().onExecute(sender, isPlayer, args.subList(1, args.size()));
				}
			}
		}
		
		return executeIntern(sender, isPlayer, args);
	}
	
	public List<String> onTab(CommandSender sender, boolean isPlayer, List<String> args) {
		if (args.size() > 0) {
			for (Entry<String, SubCommand> e: sub.entrySet()) {
				if (e.getKey().equalsIgnoreCase(args.get(0))) {
					return e.getValue().onTab(sender, isPlayer, args.subList(1, args.size()));
				}
			}
		}
		
		return tabIntern(sender, isPlayer, args);
	}
	
	/**
	 * Utility-Methode: Gibt eine Tabliste mit passenden Spielern zurück
	 * @param arg Das Argument, das geprüft werden soll
	 * @param players Die zu prüfenden Spieler
	 * @return Die fertige Liste
	 */
	protected ArrayList<String> tabPlayers(String arg, Collection<? extends Player> players) {
		ArrayList<String> ret = new ArrayList<>(players.size());
		
		for (Player p: players) if (p.getName().toLowerCase().startsWith(arg.toLowerCase())) ret.add(p.getName());
		
		return ret;
	}
	
	/**
	 * Utility-Methode: Ruft {@link #tabPlayers(String, Collection)} mit allen Spielern auf dem Server auf
	 * @param arg Das Argument, das geprüft werden soll
	 * @return Die fertige Liste
	 */
	protected ArrayList<String> tabPlayers(String arg) {
		return tabPlayers(arg, plugin.getServer().getOnlinePlayers());
	}
	
	/**
	 * Utility-Methode: Gibt eine Tabliste mit passenden Objekten zurück
	 * @param <T> Der Typ der Objekte, sollte {@link Object#toString()} überschreiben
	 * @param arg Das Argument, das geprüft werden soll
	 * @param coll Die zu prüfenden Objekte
	 * @return Die fertige Liste
	 */
	protected <T> ArrayList<String> tabCollection(String arg, Collection<? extends T> coll) {
		ArrayList<String> ret = new ArrayList<>(coll.size());
		
		for (T t: coll) if (t.toString().toLowerCase().startsWith(arg.toLowerCase())) ret.add(t.toString());
		
		return ret;
	}
}
