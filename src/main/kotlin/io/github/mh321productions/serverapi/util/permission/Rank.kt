package io.github.mh321productions.serverapi.util.permission

import io.github.mh321productions.serverapi.Main
import net.luckperms.api.LuckPerms
import net.luckperms.api.model.group.Group
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.PermissionNode
import net.luckperms.api.query.QueryOptions
import org.bukkit.permissions.Permission

/**
 * Eine Util-Klasse, die Informationen über einen Spielerrang enthält. <br></br>
 * Sie wird vom [PermissionHandler] geladen.
 * @author 321Productions
 */
class Rank internal constructor(private val plugin: Main, val group: Group, private val lp: LuckPerms) {

    companion object {
        @JvmStatic
        fun sortByWeight(a: Rank, b: Rank) = b.weight.compareTo(a.weight)
    }

    val weight = group.weight.orElse(0)
    val name = group.name
    val displayName = group.displayName?.replace('&', '§') ?: name
    val prefix: String
    val suffix: String
    val color: String

    private val _nodes = mutableMapOf<String, Boolean>()
    private val _subGroups = mutableListOf<String>()

    val nodes: Map<String, Boolean>
        get() = _nodes

    val subGroups: List<String>
    	get() = _subGroups

    init {
        //Prefix
        val pre = group.getNodes(NodeType.PREFIX)
        prefix = if (!pre.isEmpty()) pre.stream().toList()[0].metaValue.replace('&', '§')
        else ""


        //Suffix
        val s = group.getNodes(NodeType.SUFFIX)
        suffix = if (!s.isEmpty()) pre.stream().toList()[0].metaValue.replace('&', '§')
        else ""


        //Farbe
        if (prefix.startsWith("§")) {
            var index = 1
            val builder = StringBuilder("§")
            builder.append(prefix[index])
            index++

            while (index < prefix.length && prefix[index] == '§') {
                index++
                builder.append(prefix[index])
                index++
            }

            color = builder.toString()
        } else {
            color = "§7"
        }
    }

    fun loadPermissions() {
        //Nodes rekursiv einlesen
        val names = mutableListOf<String>()
        val children = mutableListOf<Map.Entry<String, Boolean>>()
        var g: Group?
        var i: InheritanceNode
        var perm: Permission?
        var child: Map.Entry<String, Boolean>


        //subGroups.add(group);
        for (n in group.resolveInheritedNodes(QueryOptions.nonContextual())) {
            if (_nodes.containsKey(n.key)) continue  //Wenn der Node bereits enthalten ist

            if (n.type === NodeType.INHERITANCE) { //Wenn es eine Gruppe ist
                i = n as InheritanceNode
                g = lp.groupManager.getGroup(i.groupName)
                plugin.logger.info("[Rank " + group.name + "]: Loading Group " + i.groupName)
                if (!names.contains(i.groupName) && g != null) names.add(i.groupName)
            } else if (n is PermissionNode && n.getValue()) { //Permission-Node mit evtl Kindern
                _nodes[n.getKey()] = n.getValue() //Normaler Knoten, einfügen
                perm = plugin.server.pluginManager.getPermission(n.getKey())
                if (perm != null) {
                    children.addAll(perm.children.entries)

                    while (children.isNotEmpty()) { //Kinder rekursiv durchgehen (Tiefensuche)
                        child = children.removeAt(0)
                        if (!_nodes.containsKey(child.key)) _nodes[child.key] = child.value

                        perm = plugin.server.pluginManager.getPermission(child.key)
                        if (perm != null) children.addAll(0, perm.children.entries)
                    }
                }
            } else {
                _nodes[n.key] = n.value //Normaler Knoten, einfügen
            }
        }

        _subGroups.clear()
        _subGroups.addAll(names)
    }

    /**
     * Gibt den Wert einer Permission zurück
     * @param permission Die Permission
     * @return Den Wert, oder `false`, wenn die Permission nicht vorhanden ist
     */
    fun hasPermission(permission: String) = _nodes.getOrDefault(permission, false)

    /**
     * Frägt ab, ob dieser Rang vom gegebenen erbt
     * @param group Der Name des abzufragenden Ranges
     * @return Ob dieser Rang vom gegebenen erbt
     */
    fun hasSubGroup(group: String) = _subGroups.contains(group)

    /**
     * Frägt ab, ob dieser Rang vom gegebenen erbt
     * @param group Der abzufragende Rang
     * @return Ob dieser Rang vom gegebenen erbt
     */
    fun hasSubGroup(group: Rank) = hasSubGroup(group.name)

    /**
     * Gibt zurück, ob eine Permission definiert wird
     * @param permission Die Permission
     * @return Ob sie vom Rang definiert wird
     */
    fun definesPermission(permission: String) = _nodes.containsKey(permission)

    fun toDisplayString() = ("$name $displayName $prefix $suffix $weight $color").replace('§', '&')

    /**
     * Testet einen Rang nach der Luckperms Permission ("group.&lt;name&gt;")
     * oder der LuckPerms [Group]
     */
    override fun equals(other: Any?): Boolean {
        when (other) {
            is String -> {
                return if (other.startsWith("group.")) name == other.substring(6)
                else name == other
            }

            is Group -> {
                return group == other
            }

            is DefaultRank -> {
                return name == other.rankName
            }

            else -> return super.equals(other)
        }
    }
}
