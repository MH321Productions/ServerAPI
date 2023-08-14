package io.github.mh321productions.serverapi.util.permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import io.github.mh321productions.serverapi.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;

/**
 * Eine Util-Klasse, die direkt mit LuckPerms interagiert,
 * um Ränge, Rangnamen und Permissions <br>
 * besser zugänglich zu machen als durch Vault
 * @author 321Productions
 *
 */
public class PermissionHandler {
	
	private Main plugin;
	private LuckPerms lp;
	private ArrayList<Rank> ranks;
	private Logger log;
	private boolean isLoaded = false;
	
	public PermissionHandler(Main plugin) {
		this.plugin = plugin;
		log = plugin.getLogger();
		plugin.getServer().getPluginManager().registerEvents(new PermissionLoadListener(), plugin);
		
		RegisteredServiceProvider<LuckPerms> provider = plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) lp = provider.getProvider();
		else {
			lp = null;
			plugin.getLogger().severe("Konnte nicht mit LuckPerms verbinden");
			return;
		}
		
		//Ränge laden
		ranks = new ArrayList<>(lp.getGroupManager().getLoadedGroups().size());
		for (Group g: lp.getGroupManager().getLoadedGroups()) {
			ranks.add(new Rank(plugin, g, lp));
		}
		ranks.sort(Rank::sortByWeight);
		
		log.info(ranks.size() + " Ränge geladen:");
		for (Rank r: ranks) log.info(r.toDisplayString());
	}
	
	/**
	 * Fragt ab, ob ein Spieler eine Permission hat
	 * @param user Die LuckPerms-Instanz des Spielers
	 * @param perm Die Permission
	 * @return Ob die Permission vorhanden und aktiviert ist
	 */
	public boolean hasPermission(User user, String perm) {
		for (Rank r: getRanks(user)) if (r.definesPermission(perm)) return r.hasPermission(perm);
		return false;
		/*if (user == null) return false;
		for (Node n: user.getNodes()) {
			if (n.getKey().equalsIgnoreCase(perm)) return n.getValue();
		}
		
		return false;*/
	}
	
	/**
	 * Fragt ab, ob ein Spieler eine Permission hat
	 * @param p Der Spieler
	 * @param perm Die Permission
	 * @return Ob die Permission vorhanden und aktiviert ist
	 */
	public boolean hasPermission(Player p, String perm) {
		//p.hasPermission(perm);
		for (Rank r: getRanks(p)) if (r.definesPermission(perm)) return r.hasPermission(perm);
		return false;
		/*if (p == null) return false;
		return p.hasPermission(perm);*/
		//return hasPermission(lp.getUserManager().getUser(p.getUniqueId()), perm);
	}
	
	/**
	 * Fragt ab, ob ein Spieler eine Permission hat
	 * @param uuid Die UUID des Spielers
	 * @param perm Die Permission
	 * @return Ob die Permission vorhanden und aktiviert ist
	 */
	public boolean hasPermission(UUID uuid, String perm) {
		for (Rank r: getRanks(uuid)) if (r.definesPermission(perm)) return r.hasPermission(perm);
		return false;
		/*if (uuid == null) return false;
		return hasPermission(lp.getUserManager().getUser(uuid), perm);*/
	}
	
	/**
	 * Fragt ab, ob ein {@link CommandSender} eine Permission hat (hat nur einen Effekt auf Spieler)
	 * @param sender Der Sender
	 * @param perm Die Permission
	 * @return Ob die Permission vorhanden und aktiviert ist (<code>true</code>, wenn es kein Spieler ist)
	 */
	public boolean hasPermission(CommandSender sender, String perm) {
		if (sender instanceof Player) return hasPermission((Player) sender, perm);
		return true;
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
	 * @param p Der Spieler
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(Player p, Rank rank) {
		for (Rank r: getRanks(p)) {
			if (r.equals(rank)) return true;
			
			for (String s: r.getSubGroups()) if (rank.getName().equals(s)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(p.getUniqueId()), "group." + rank.getName());
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
	 * @param uuid Die UUID des Spielers
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(UUID uuid, Rank rank) {
		for (Rank r: getRanks(uuid)) {
			if (r.equals(rank)) return true;
			
			for (String s: r.getSubGroups()) if (rank.getName().equals(s)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(uuid), "group." + rank.getName());
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
	 * @param u Die LuckPerms-Instanz des Spielers
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(User u, Rank rank) {
		for (Rank r: getRanks(u)) {
			if (r.equals(rank)) return true;
			
			for (String s: r.getSubGroups()) if (rank.getName().equals(s)) return true;
		}
		return false;
		//return hasPermission(u, "group." + rank.getName());
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
	 * @param p Der Spieler
	 * @param rank Der interne Name des Rangs
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(Player p, String rank) {
		for (Rank r: getRanks(p)) {
			if (r.getName().equals(rank)) return true;
			
			for (String s: r.getSubGroups()) if (rank.equals(s)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(p.getUniqueId()), "group." + rank);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
	 * @param uuid Die UUID des Spielers
	 * @param rank Der interne Name des Rangs
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(UUID uuid, String rank) {
		for (Rank r: getRanks(uuid)) {
			if (r.getName().equals(rank)) return true;
			
			for (String s: r.getSubGroups()) if (rank.equals(s)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(uuid), "group." + rank);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
	 * @param u Die LuckPerms-Instanz des Spielers
	 * @param rank Der interne Name des Rangs
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(User u, String rank) {
		for (Rank r: getRanks(u)) {
			if (r.getName().equals(rank)) return true;
			
			for (String s: r.getSubGroups()) if (rank.equals(s)) return true;
		}
		return false;
		//return hasPermission(u, "group." + rank);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
	 * @param p Der Spieler
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(Player p, DefaultRank rank) {
		for (Rank r: getRanks(p)) {
			if (r.getName().equals(rank.name)) return true;
			
			for (String s: r.getSubGroups()) if (rank.name.equals(s)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(p.getUniqueId()), "group." + rank.name);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
	 * @param uuid Die UUID des Spielers
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(UUID uuid, DefaultRank rank) {
		for (Rank r: getRanks(uuid)) {
			if (r.getName().equals(rank.name)) return true;
			
			for (String s: r.getSubGroups()) if (rank.name.equals(s)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(uuid), "group." + rank.name);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
	 * @param u Die LuckPerms-Instanz des Spielers
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(User u, DefaultRank rank) {
		for (Rank r: getRanks(u)) {
			if (r.getName().equals(rank.name)) return true;
			
			for (String s: r.getSubGroups()) if (rank.name.equals(s)) return true;
		}
		return false;
		//return hasPermission(u, "group." + rank.name);
	}
	
	/**
	 * Fragt ab, ob ein Spieler mindestens einen der gegebenen Ränge hat (beinhaltet vererbte Gruppen)
	 * @param p Der Spieler
	 * @param ranks Die Ränge
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(Player p, DefaultRank... ranks) {
		for (Rank r: getRanks(p)) {
			for (DefaultRank dr: ranks) 
				if (r.getName().equals(dr.name)) return true;
			
			for (String s: r.getSubGroups()) 
				for (DefaultRank dr: ranks) 
					if (dr.name.equals(s)) return true;
		}
		return false;
		/*for (DefaultRank r: ranks) if (hasPermission(lp.getUserManager().getUser(p.getUniqueId()), "group." + r.name)) {
			p.sendMessage("Du hast den Rang " + r.name);
			return true;
		}
		return false;*/
	}
	
	/**
	 * Fragt ab, ob ein Spieler mindestens einen der gegebenen Ränge hat (beinhaltet vererbte Gruppen)
	 * @param uuid Die UUID des Spielers
	 * @param ranks Die Ränge
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(UUID uuid, DefaultRank... ranks) {
		for (Rank r: getRanks(uuid)) {
			for (DefaultRank dr: ranks) 
				if (r.getName().equals(dr.name)) return true;
			
			for (String s: r.getSubGroups()) 
				for (DefaultRank dr: ranks) 
					if (dr.name.equals(s)) return true;
		}
		return false;
		/*for (DefaultRank r: ranks) if (hasPermission(lp.getUserManager().getUser(uuid), "group." + r.name)) return true;
		return false;*/
	}
	
	/**
	 * Fragt ab, ob ein Spieler mindestens einen der gegebenen Ränge hat (beinhaltet vererbte Gruppen)
	 * @param u Die LuckPerms-Instanz des Spielers
	 * @param ranks Die Ränge
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasRank(User u, DefaultRank... ranks) {
		for (Rank r: getRanks(u)) {
			for (DefaultRank dr: ranks) 
				if (r.getName().equals(dr.name)) return true;
			
			for (String s: r.getSubGroups()) 
				for (DefaultRank dr: ranks) 
					if (dr.name.equals(s)) return true;
		}
		return false;
		/*for (DefaultRank r: ranks) if (hasPermission(u, "group." + r.name)) return true;
		return false;*/
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
	 * @param p Der Spieler
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(Player p, Rank rank) {
		for (Rank r: getRanks(p)) {
			if (r.equals(rank)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(p.getUniqueId()), "group." + rank.getName());
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
	 * @param uuid Die UUID des Spielers
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(UUID uuid, Rank rank) {
		for (Rank r: getRanks(uuid)) {
			if (r.equals(rank)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(uuid), "group." + rank.getName());
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
	 * @param u Die LuckPerms-Instanz des Spielers
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(User u, Rank rank) {
		for (Rank r: getRanks(u)) {
			if (r.equals(rank)) return true;
		}
		return false;
		//return hasPermission(u, "group." + rank.getName());
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
	 * @param p Der Spieler
	 * @param rank Der interne Name des Rangs
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(Player p, String rank) {
		for (Rank r: getRanks(p)) {
			if (r.getName().equals(rank)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(p.getUniqueId()), "group." + rank);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
	 * @param uuid Die UUID des Spielers
	 * @param rank Der interne Name des Rangs
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(UUID uuid, String rank) {
		for (Rank r: getRanks(uuid)) {
			if (r.getName().equals(rank)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(uuid), "group." + rank);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
	 * @param u Die LuckPerms-Instanz des Spielers
	 * @param rank Der interne Name des Rangs
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(User u, String rank) {
		for (Rank r: getRanks(u)) {
			if (r.getName().equals(rank)) return true;
		}
		return false;
		//return hasPermission(u, "group." + rank);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
	 * @param p Der Spieler
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(Player p, DefaultRank rank) {
		for (Rank r: getRanks(p)) {
			if (r.getName().equals(rank.name)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(p.getUniqueId()), "group." + rank.name);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
	 * @param uuid Die UUID des Spielers
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(UUID uuid, DefaultRank rank) {
		for (Rank r: getRanks(uuid)) {
			if (r.getName().equals(rank.name)) return true;
		}
		return false;
		//return hasPermission(lp.getUserManager().getUser(uuid), "group." + rank.name);
	}
	
	/**
	 * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
	 * @param u Die LuckPerms-Instanz des Spielers
	 * @param rank Der Rang
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(User u, DefaultRank rank) {
		for (Rank r: getRanks(u)) {
			if (r.getName().equals(rank.name)) return true;
			
			for (String s: r.getSubGroups()) if (rank.name.equals(s)) return true;
		}
		return false;
		//return hasPermission(u, "group." + rank.name);
	}
	
	/**
	 * Fragt ab, ob ein Spieler mindestens einen der gegebenen expliziten Ränge hat (ohne vererbte Gruppen)
	 * @param p Der Spieler
	 * @param ranks Die Ränge
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(Player p, DefaultRank... ranks) {
		for (Rank r: getRanks(p)) {
			for (DefaultRank dr: ranks) 
				if (r.getName().equals(dr.name)) return true;
		}
		return false;
		/*for (DefaultRank r: ranks) if (hasPermission(lp.getUserManager().getUser(p.getUniqueId()), "group." + r.name)) {
			p.sendMessage("Du hast den Rang " + r.name);
			return true;
		}
		return false;*/
	}
	
	/**
	 * Fragt ab, ob ein Spieler mindestens einen der gegebenen expliziten Ränge hat (ohne vererbte Gruppen)
	 * @param uuid Die UUID des Spielers
	 * @param ranks Die Ränge
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(UUID uuid, DefaultRank... ranks) {
		for (Rank r: getRanks(uuid)) {
			for (DefaultRank dr: ranks) 
				if (r.getName().equals(dr.name)) return true;
		}
		return false;
		/*for (DefaultRank r: ranks) if (hasPermission(lp.getUserManager().getUser(uuid), "group." + r.name)) return true;
		return false;*/
	}
	
	/**
	 * Fragt ab, ob ein Spieler mindestens einen der gegebenen expliziten Ränge hat (ohne vererbte Gruppen)
	 * @param u Die LuckPerms-Instanz des Spielers
	 * @param ranks Die Ränge
	 * @return Ob der Rang zugewiesen ist
	 */
	public boolean hasExplicitRank(User u, DefaultRank... ranks) {
		for (Rank r: getRanks(u)) {
			for (DefaultRank dr: ranks) 
				if (r.getName().equals(dr.name)) return true;
		}
		return false;
		/*for (DefaultRank r: ranks) if (hasPermission(u, "group." + r.name)) return true;
		return false;*/
	}
	
	private int searchRank(String internalName) {
		for (int i = 0; i < ranks.size(); i++) {
			if (ranks.get(i).getName().equals(internalName)) return i;
		}
		
		return -1;
	}
	
	/**
	 * Fragt einen Rang nach der Permission Node ab
	 * @param node Die LuckPerms {@link InheritanceNode}
	 * @return Der Rang oder <code>null</code>
	 */
	public Rank getRank(InheritanceNode node) {
		int index = searchRank(node.getKey().substring(6));
		
		if (index != -1) return ranks.get(index);
		else return null;
	}
	
	/**
	 * Fragt einen Rang nach dem internen Namen ab
	 * @param internalName Der interne Name des Rangs
	 * @return Der Rang oder <code>null</code>
	 */
	public Rank getRank(String internalName) {
		int index = searchRank(internalName);
		
		if (index != -1) return ranks.get(index);
		else return null;
	}
	
	public ArrayList<Rank> getRanks() {
		return ranks;
	}
	
	/**
	 * Fragt mehrere Ränge nach den Permission Nodes ab
	 * @param nodes Die LuckPerms {@link InheritanceNode}
	 * @return Eine Liste mit validen Rängen oder eine leere Liste
	 */
	public ArrayList<Rank> getRanks(Collection<InheritanceNode> nodes) {
		ArrayList<Rank> ret = new ArrayList<>(nodes.size());
		
		Rank r;
		for (InheritanceNode n: nodes) {
			r = getRank(n);
			if (r != null) ret.add(r);
		}
		
		return ret;
	}
	
	/**
	 * Fragt mehrere Ränge nach den internen Namen ab
	 * @param internalNames Die LuckPerms {@link InheritanceNode}
	 * @return Eine Liste mit validen Rängen oder eine leere Liste
	 */
	public ArrayList<Rank> getRanks(List<String> internalNames) {
		ArrayList<Rank> ret = new ArrayList<>(internalNames.size());
		
		Rank r;
		for (String n: internalNames) {
			r = getRank(n);
			if (r != null) ret.add(r);
		}
		
		return ret;
	}
	
	/**
	 * Gibt alle Ränge eines Spielers zurück
	 * @param user Die LuckPerms-Instanz des Spielers
	 * @return Die Liste der Ränge oder eine leere Liste
	 */
	public ArrayList<Rank> getRanks(User user) {
		ArrayList<Rank> ranks = getRanks(user.getNodes(NodeType.INHERITANCE));
		ranks.sort(Rank::sortByWeight);
		return ranks;
	}
	
	/**
	 * Gibt alle Ränge eines Spielers zurück
	 * @param player Der Spieler
	 * @return Die Liste der Ränge oder eine leere Liste
	 */
	public ArrayList<Rank> getRanks(Player player) {
		return getRanks(lp.getUserManager().getUser(player.getUniqueId()));
	}
	
	/**
	 * Gibt alle Ränge eines Spielers zurück
	 * @param uuid Die UUID des Spielers
	 * @return Die Liste der Ränge oder eine leere Liste
	 */
	public ArrayList<Rank> getRanks(UUID uuid) {
		return getRanks(lp.getUserManager().getUser(uuid));
	}
	
	/**
	 * Gibt den höchsten Rang eines Spielers zurück
	 * @param u Die LuckPerms-Instanz des Spielers
	 * @return Der höchste Rang oder default
	 */
	public Rank getHighestRank(User u) {
		Collection<InheritanceNode> nodes = u.getNodes(NodeType.INHERITANCE);
		if (nodes.isEmpty()) return ranks.get(ranks.size() - 1); //Fallback auf default
		
		ArrayList<Rank> ranks = getRanks(nodes);
		if (ranks.isEmpty()) return ranks.get(ranks.size() - 1); //Fallback auf default
		
		ranks.sort(Rank::sortByWeight);
		return ranks.get(0);
	}
	
	/**
	 * Gibt den höchsten Rang eines Spielers zurück
	 * @param p Der Spieler
	 * @return Der höchste Rang oder default
	 */
	public Rank getHighestRank(Player p) {
		return getHighestRank(lp.getUserManager().getUser(p.getUniqueId()));
	}
	
	/**
	 * Gibt den höchsten Rang eines Spielers zurück
	 * @param uuid Die UUID des Spielers
	 * @return Der höchste Rang oder default
	 */
	public Rank getHighestRank(UUID uuid) {
		return getHighestRank(lp.getUserManager().getUser(uuid));
	}
	
	/**
	 * Gibt den default (Spieler) Rank zurück
	 * @return Der Rang
	 */
	public Rank getDefaultRank() {
		return ranks.get(ranks.size() - 1);
	}
	
	/**
	 * Intern: Lädt die Permissions, nachdem der Server gestartet ist,
	 * damit alle Wildcards und Child-Permissions aufgelöst werden können
	 * @author 321Productions
	 *
	 */
	class PermissionLoadListener implements Listener {
		
		@EventHandler
		public void onLoad(ServerLoadEvent event) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if (isLoaded) return;
					
					log.info("Lade Permissions");
					for (Rank r: ranks) r.loadPermissions();
					isLoaded = true;
					log.info("Permissions geladen");
				}
			}.runTask(plugin);
		}
	}
}
