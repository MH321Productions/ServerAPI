package io.github.mh321productions.serverapi.util.permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import io.github.mh321productions.serverapi.Main;
import org.bukkit.permissions.Permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;

/**
 * Eine Util-Klasse, die Informationen über einen Spielerrang enthält. <br>
 * Sie wird vom {@link PermissionHandler} geladen.
 * @author 321Productions
 *
 */
public class Rank {
	
	private Group group;
	private LuckPerms lp;
	private int weight;
	private String name, displayName, prefix, suffix, color;
	private HashMap<String, Boolean> nodes;
	private ArrayList<String> subGroups;
	private Main plugin;
	
	Rank(Main plugin, Group group, LuckPerms lp) {
		this.plugin = plugin;
		this.group = group;
		this.lp = lp;
		weight = group.getWeight().orElse(0);
		name = group.getName();
		displayName = group.getDisplayName();
		nodes = new HashMap<>();
		if (displayName == null) displayName = name;
		else displayName = displayName.replace('&', '§');
		
		//Prefix
		Collection<PrefixNode> pre = group.getNodes(NodeType.PREFIX);
		if (!pre.isEmpty()) prefix = pre.stream().toList().get(0).getMetaValue().replace('&', '§');
		else prefix = "";
		
		//Suffix
		Collection<SuffixNode> s = group.getNodes(NodeType.SUFFIX);
		if (!s.isEmpty()) suffix = pre.stream().toList().get(0).getMetaValue().replace('&', '§');
		else suffix = "";
		
		//Farbe
		if (prefix.startsWith("§")) {
			int index = 1;
			StringBuilder builder = new StringBuilder("§");
			builder.append(prefix.charAt(index));
			index++;
			
			while (index < prefix.length() && prefix.charAt(index) == '§') {
				index++;
				builder.append(prefix.charAt(index));
				index++;
			}
			
			color = builder.toString();
			
		} else {
			color = "§7";
		}
	}
	
	void loadPermissions() {
		//Nodes rekursiv einlesen
		//ArrayList<Group> subGroups = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();
		ArrayList<Entry<String, Boolean>> children = new ArrayList<>();
		Group g;
		InheritanceNode i;
		Permission perm;
		Entry<String, Boolean> child;
		
		//subGroups.add(group);
		
		for (Node n: group.resolveInheritedNodes(QueryOptions.nonContextual())) {
			if (nodes.containsKey(n.getKey())) continue; //Wenn der Node bereits enthalten ist
			if (n.getType() == NodeType.INHERITANCE) { //Wenn es eine Gruppe ist
				i = (InheritanceNode) n;
				g = lp.getGroupManager().getGroup(i.getGroupName());
				plugin.getLogger().info("[Rank " + group.getName() + "]: Loading Group " + i.getGroupName());
				if (!names.contains(i.getGroupName()) && g != null) names.add(i.getGroupName());
			} else if (n instanceof PermissionNode && n.getValue()) { //Permission-Node mit evtl Kindern
				nodes.put(n.getKey(), n.getValue()); //Normaler Knoten, einfügen
				perm = plugin.getServer().getPluginManager().getPermission(n.getKey());
				//plugin.getLogger().info("{" + name + "} " + n.getKey() + ": " + n.getValue() + "(" + (perm != null ? "not " : "") + "null)");
				if (perm != null) {
					children.addAll(perm.getChildren().entrySet());
					
					while (!children.isEmpty()) { //Kinder rekursiv durchgehen (Tiefensuche)
						child = children.remove(0);
						if (!nodes.containsKey(child.getKey())) nodes.put(child.getKey(), child.getValue());
						//plugin.getLogger().info("{" + name + "} -> " + child.getKey() + ": " + child.getValue());
						
						perm = plugin.getServer().getPluginManager().getPermission(child.getKey());
						if (perm != null) children.addAll(0, perm.getChildren().entrySet());
					}
				}
			} else {
				nodes.put(n.getKey(), n.getValue()); //Normaler Knoten, einfügen
			}
		}
		
		this.subGroups = names; //Geladene Untergruppen speichern
	}

	public int getWeight() {
		return weight;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}
	
	public String getColor() {
		return color;
	}
	
	public HashMap<String, Boolean> getNodes() {
		return nodes;
	}
	
	public ArrayList<String> getSubGroups() {
		return subGroups;
	}

	/**
	 * Gibt den Wert einer Permission zurück
	 * @param permission Die Permission
	 * @return Den Wert, oder <code>false</code>, wenn die Permission nicht vorhanden ist
	 */
	public boolean hasPermission(String permission) {
		return nodes.getOrDefault(permission, false);
		/*for (Node n: group.getNodes()) {
			if (n.getKey().equalsIgnoreCase(permission)) return n.getValue();
		}
		return false;*/
	}
	
	/**
	 * Frägt ab, ob dieser Rang vom gegebenen erbt
	 * @param group Der Name des abzufragenden Ranges
	 * @return Ob dieser Rang vom gegebenen erbt
	 */
	public boolean hasSubGroup(String group) {
		return subGroups.contains(group);
	}
	
	/**
	 * Gibt zurück, ob eine Permission definiert wird
	 * @param permission Die Permission
	 * @return Ob sie vom Rang definiert wird
	 */
	public boolean definesPermission(String permission) {
		return nodes.containsKey(permission);
	}
	
	/**
	 * Frägt ab, ob dieser Rang vom gegebenen erbt
	 * @param group Der abzufragende Rang
	 * @return Ob dieser Rang vom gegebenen erbt
	 */
	public boolean hasSubGroup(Rank group) {
		return subGroups.contains(group.getName());
	}
	
	public String toDisplayString() {
		return (name  + " " + displayName + " " + prefix + " " + suffix + " " + weight + " " + color).replace('§', '&');
	}
	
	public static int sortByWeight(Rank a, Rank b) {
		return Integer.compare(b.weight, a.weight);
	}
	
	/**
	 * Testet einen Rang nach der Luckperms Permission ("group.&ltname&gt")
	 * oder der LuckPerms {@link Group} 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			String s = (String) obj;
			System.out.println("Testing Rank" + name + " with String " + s);
			if (s.startsWith("group.")) return name.equals(s.substring(6));
			else return name.equals(s);
		} else if (obj instanceof Group) {
			return group.equals((Group) obj);
		} else if (obj instanceof DefaultRank) {
			DefaultRank r = (DefaultRank) obj;
			return name.equals(r.name);
		}
		
		return super.equals(obj);
	}
}
