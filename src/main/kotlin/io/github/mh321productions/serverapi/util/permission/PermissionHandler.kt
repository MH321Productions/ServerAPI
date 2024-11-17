package io.github.mh321productions.serverapi.util.permission

import io.github.mh321productions.serverapi.Main
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.logging.Level
import kotlin.Comparator

/**
 * Eine Util-Klasse, die direkt mit LuckPerms interagiert,
 * um Ränge, Rangnamen und Permissions <br></br>
 * besser zugänglich zu machen als durch Vault
 * @author 321Productions
 */
class PermissionHandler(private val plugin: Main) {
    private val lp: LuckPerms
    private val log = plugin.logger
    private var isLoaded = false
    val ranks: List<Rank>

    init {
        val provider = plugin.server.servicesManager.getRegistration(LuckPerms::class.java)
        lp = if (provider != null) provider.provider
        else {
            log.warning("LuckPerms Provider nicht gefunden. Versuche Singleton")
            try {
                LuckPermsProvider.get()
            } catch (ex: IllegalStateException) {
                log.log(Level.SEVERE, "Konnte nicht mit LuckPerms verbinden:", ex)
                throw ex
            }
        }


        //Ränge laden
        ranks = lp.groupManager.loadedGroups
            .map { Rank(plugin, it, lp) }
            .sortedWith(Comparator(Rank::sortByWeight))

        log.info("${ranks.size} Ränge geladen:")
        for (r in ranks) log.info(r.toDisplayString())

        plugin.server.pluginManager.registerEvents(PermissionLoadListener(), plugin)
        LuckPermsListener(plugin, this, lp).registerEvents()
    }

    /**
     * Fragt ab, ob ein Spieler eine Permission hat
     * @param user Die LuckPerms-Instanz des Spielers
     * @param perm Die Permission
     * @return Ob die Permission vorhanden und aktiviert ist
     */
    fun hasPermission(user: User, perm: String) = getRanks(user).firstOrNull { it.definesPermission(perm) }?.hasPermission(perm) ?: false

    /**
     * Fragt ab, ob ein Spieler eine Permission hat
     * @param p Der Spieler
     * @param perm Die Permission
     * @return Ob die Permission vorhanden und aktiviert ist
     */
    fun hasPermission(p: Player, perm: String) = getRanks(p).firstOrNull { it.definesPermission(perm) }?.hasPermission(perm) ?: false

    /**
     * Fragt ab, ob ein Spieler eine Permission hat
     * @param uuid Die UUID des Spielers
     * @param perm Die Permission
     * @return Ob die Permission vorhanden und aktiviert ist
     */
    fun hasPermission(uuid: UUID, perm: String) = getRanks(uuid).firstOrNull { it.definesPermission(perm) }?.hasPermission(perm) ?: false

    /**
     * Fragt ab, ob ein [CommandSender] eine Permission hat (hat nur einen Effekt auf Spieler)
     * @param sender Der Sender
     * @param perm Die Permission
     * @return Ob die Permission vorhanden und aktiviert ist (`true`, wenn es kein Spieler ist)
     */
    fun hasPermission(sender: CommandSender, perm: String) = if (sender is Player) hasPermission(sender, perm) else true

    /**
     * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
     * @param u Die LuckPerms-Instanz des Spielers
     * @param rank Der interne Name des Rangs
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(u: User?, rank: String): Boolean {
        return getRanks(u)
            .map { listOf(it.name, *it.subGroups.toTypedArray()) }
            .flatten()
            .contains(rank)
    }

    /**
     * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
     * @param u Die LuckPerms-Instanz des Spielers
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(u: User?, rank: Rank) = hasRank(u, rank.name)

    /**
     * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
     * @param p Der Spieler
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(p: Player, rank: Rank) = hasRank(lp.userManager.getUser(p.uniqueId), rank)

    /**
     * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
     * @param uuid Die UUID des Spielers
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(uuid: UUID, rank: Rank) = hasRank(lp.userManager.getUser(uuid), rank.name)

    /**
     * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
     * @param p Der Spieler
     * @param rank Der interne Name des Rangs
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(p: Player, rank: String) = hasRank(lp.userManager.getUser(p.uniqueId), rank)

    /**
     * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
     * @param uuid Die UUID des Spielers
     * @param rank Der interne Name des Rangs
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(uuid: UUID, rank: String) = hasRank(lp.userManager.getUser(uuid), rank)

    /**
     * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
     * @param p Der Spieler
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(p: Player, rank: DefaultRank) = hasRank(lp.userManager.getUser(p.uniqueId), rank.rankName)

    /**
     * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
     * @param uuid Die UUID des Spielers
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(uuid: UUID, rank: DefaultRank) = hasRank(lp.userManager.getUser(uuid), rank.rankName)

    /**
     * Fragt ab, ob ein Spieler einen Rang hat (beinhaltet vererbte Gruppen)
     * @param u Die LuckPerms-Instanz des Spielers
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(u: User, rank: DefaultRank) = hasRank(u, rank.rankName)

    /**
     * Fragt ab, ob ein Spieler mindestens einen der gegebenen Ränge hat (beinhaltet vererbte Gruppen)
     * @param p Der Spieler
     * @param ranks Die Ränge
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(p: Player, vararg ranks: DefaultRank) = ranks.any { hasRank(p, it) }

    /**
     * Fragt ab, ob ein Spieler mindestens einen der gegebenen Ränge hat (beinhaltet vererbte Gruppen)
     * @param uuid Die UUID des Spielers
     * @param ranks Die Ränge
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(uuid: UUID, vararg ranks: DefaultRank) = ranks.any { hasRank(uuid, it) }

    /**
     * Fragt ab, ob ein Spieler mindestens einen der gegebenen Ränge hat (beinhaltet vererbte Gruppen)
     * @param u Die LuckPerms-Instanz des Spielers
     * @param ranks Die Ränge
     * @return Ob der Rang zugewiesen ist
     */
    fun hasRank(u: User, vararg ranks: DefaultRank) = ranks.any { hasRank(u, it) }

    /**
     * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
     * @param p Der Spieler
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(p: Player, rank: Rank) = getRanks(p).contains(rank)

    /**
     * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
     * @param uuid Die UUID des Spielers
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(uuid: UUID, rank: Rank) = getRanks(uuid).contains(rank)

    /**
     * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
     * @param u Die LuckPerms-Instanz des Spielers
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(u: User, rank: Rank) = getRanks(u).contains(rank)

    /**
     * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
     * @param p Der Spieler
     * @param rank Der interne Name des Rangs
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(p: Player, rank: String) = getRanks(p).map { it.name }.contains(rank)

    /**
     * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
     * @param uuid Die UUID des Spielers
     * @param rank Der interne Name des Rangs
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(uuid: UUID, rank: String) = getRanks(uuid).map { it.name }.contains(rank)

    /**
     * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
     * @param u Die LuckPerms-Instanz des Spielers
     * @param rank Der interne Name des Rangs
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(u: User, rank: String) = getRanks(u).map { it.name }.contains(rank)

    /**
     * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
     * @param p Der Spieler
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(p: Player, rank: DefaultRank) = hasExplicitRank(p, rank.rankName)

    /**
     * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
     * @param uuid Die UUID des Spielers
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(uuid: UUID, rank: DefaultRank) = hasExplicitRank(uuid, rank.rankName)

    /**
     * Fragt ab, ob ein Spieler einen expliziten Rang hat (ohne vererbte Gruppen)
     * @param u Die LuckPerms-Instanz des Spielers
     * @param rank Der Rang
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(u: User, rank: DefaultRank) = hasExplicitRank(u, rank.rankName)

    /**
     * Fragt ab, ob ein Spieler mindestens einen der gegebenen expliziten Ränge hat (ohne vererbte Gruppen)
     * @param p Der Spieler
     * @param ranks Die Ränge
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(p: Player, vararg ranks: DefaultRank) = ranks.any { hasExplicitRank(p, it) }

    /**
     * Fragt ab, ob ein Spieler mindestens einen der gegebenen expliziten Ränge hat (ohne vererbte Gruppen)
     * @param uuid Die UUID des Spielers
     * @param ranks Die Ränge
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(uuid: UUID, vararg ranks: DefaultRank) = ranks.any { hasExplicitRank(uuid, it) }

    /**
     * Fragt ab, ob ein Spieler mindestens einen der gegebenen expliziten Ränge hat (ohne vererbte Gruppen)
     * @param u Die LuckPerms-Instanz des Spielers
     * @param ranks Die Ränge
     * @return Ob der Rang zugewiesen ist
     */
    fun hasExplicitRank(u: User, vararg ranks: DefaultRank) = ranks.any { hasExplicitRank(u, it) }

    private fun searchRank(internalName: String) = ranks.indexOfFirst { it.name == internalName }

    /**
     * Fragt einen Rang nach dem internen Namen ab
     * @param internalName Der interne Name des Rangs
     * @return Der Rang oder `null`
     */
    fun getRank(internalName: String): Rank? {
        val index = searchRank(internalName)

        return if (index != -1) ranks[index]
        else null
    }

    /**
     * Fragt einen Rang nach der Permission Node ab
     * @param node Die LuckPerms [InheritanceNode]
     * @return Der Rang oder `null`
     */
    fun getRank(node: InheritanceNode) = getRank(node.key.substring(6))

    /**
     * Fragt mehrere Ränge nach den Permission Nodes ab
     * @param nodes Die LuckPerms [InheritanceNode]
     * @return Eine Liste mit validen Rängen oder eine leere Liste
     */
    fun getRanks(nodes: Collection<InheritanceNode>) = nodes.mapNotNull { getRank(it) }

    /**
     * Fragt mehrere Ränge nach den internen Namen ab
     * @param internalNames Die LuckPerms [InheritanceNode]
     * @return Eine Liste mit validen Rängen oder eine leere Liste
     */
    fun getRanks(internalNames: List<String>) = internalNames.mapNotNull { getRank(it) }

    /**
     * Gibt alle Ränge eines Spielers zurück
     * @param user Die LuckPerms-Instanz des Spielers
     * @return Die Liste der Ränge oder eine leere Liste
     */
    fun getRanks(user: User?): List<Rank> {
        if (user == null) return listOf()

        return getRanks(user.getNodes(NodeType.INHERITANCE)).sortedWith(Comparator(Rank::sortByWeight))
    }

    /**
     * Gibt alle Ränge eines Spielers zurück
     * @param player Der Spieler
     * @return Die Liste der Ränge oder eine leere Liste
     */
    fun getRanks(player: Player) = getRanks(lp.userManager.getUser(player.uniqueId))

    /**
     * Gibt alle Ränge eines Spielers zurück
     * @param uuid Die UUID des Spielers
     * @return Die Liste der Ränge oder eine leere Liste
     */
    fun getRanks(uuid: UUID) = getRanks(lp.userManager.getUser(uuid))

    /**
     * Gibt den höchsten Rang eines Spielers zurück
     * @param u Die LuckPerms-Instanz des Spielers
     * @return Der höchste Rang oder default
     */
    fun getHighestRank(u: User?): Rank {
        if (u == null) return ranks.last()

        val nodes = u.getNodes(NodeType.INHERITANCE)
        if (nodes.isEmpty()) return defaultRank //Fallback auf default


        val userRanks = getRanks(nodes).toMutableList()
        if (userRanks.isEmpty()) return defaultRank //Fallback auf default

        return userRanks.maxByOrNull { it.weight }!!
    }

    /**
     * Gibt den höchsten Rang eines Spielers zurück
     * @param p Der Spieler
     * @return Der höchste Rang oder default
     */
    fun getHighestRank(p: Player) = getHighestRank(lp.userManager.getUser(p.uniqueId))

    /**
     * Gibt den höchsten Rang eines Spielers zurück
     * @param uuid Die UUID des Spielers
     * @return Der höchste Rang oder default
     */
    fun getHighestRank(uuid: UUID) = getHighestRank(lp.userManager.getUser(uuid))

    val defaultRank: Rank
        /**
         * Gibt den default (Spieler) Rank zurück
         * @return Der Rang
         */
        get() = ranks.last()

    /**
     * Intern: Lädt die Permissions, nachdem der Server gestartet ist,
     * damit alle Wildcards und Child-Permissions aufgelöst werden können
     * @author 321Productions
     */
    internal inner class PermissionLoadListener : Listener {
        @EventHandler
        fun onLoad(event: ServerLoadEvent?) {
            object : BukkitRunnable() {
                override fun run() {
                    if (isLoaded) return

                    log.info("Lade Permissions")
                    for (r in ranks) r.loadPermissions()
                    isLoaded = true
                    log.info("Permissions geladen")
                }
            }.runTask(plugin)
        }
    }
}
