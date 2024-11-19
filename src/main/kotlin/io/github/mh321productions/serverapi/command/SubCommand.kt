package io.github.mh321productions.serverapi.command

import io.github.mh321productions.serverapi.util.message.Message
import io.github.mh321productions.serverapi.util.message.MessageBuilder
import io.github.mh321productions.serverapi.util.message.MessagePrefix
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.min

/**
 * Intern: Eine Klasse für einen (Sub-)Command. Durch Rekursion lässt sich <br></br>
 * ein Tiefenbaum erstellen.
 * @author 321Productions
 */
abstract class SubCommand<PLUGIN : JavaPlugin>(@JvmField protected val plugin: PLUGIN) {
    /**
     * Eine Sammlung von oft benutzten Command-Messages
     * @author 321Productions
     */
    protected object StdMessages {
        @JvmField
        val onlyPlayers: Message = MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("Nur Spieler dürfen diesen Command ausführen").build()

        @JvmField
        val argsTooFew: Message = MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cZu wenig Argumente!").build()

        @JvmField
        val argsTooMuch: Message = MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cZu viele Argumente!").build()

        @JvmField
        val noPlayerWithName: Message = MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cEs existiert kein Spieler mit diesem Namen!").build()

        @JvmField
        val noWorldWithName: Message = MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cEs existiert keine Welt mit diesem Namen!").build()

        @JvmField
        val noPermission: Message = MessageBuilder().setPrefixes(MessagePrefix.Server).addComponent("§cDu darfst diesen Befehl nicht ausführen!").build()
    }

    companion object {
        @JvmField
        val emptyList = listOf<String>()
    }

    /**
     * Alle Subcommands (rekursiv)
     */
    @JvmField
    protected val sub = mutableMapOf<String, SubCommand<PLUGIN>>()
    protected abstract fun executeIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean
    protected abstract fun tabIntern(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String>
    fun onExecute(sender: CommandSender, isPlayer: Boolean, args: List<String>): Boolean {
        if (args.isNotEmpty()) {
            for ((key, value) in sub) {
                if (key.equals(args[0], ignoreCase = true)) {
                    return value.onExecute(sender, isPlayer, args.subList(1, args.size))
                }
            }
        }
        return executeIntern(sender, isPlayer, args)
    }

    fun onTab(sender: CommandSender, isPlayer: Boolean, args: List<String>): List<String> {
        if (args.isNotEmpty()) {
            for ((key, value) in sub) {
                if (key.equals(args[0], ignoreCase = true)) {
                    return value.onTab(sender, isPlayer, args.subList(1, args.size))
                }
            }
        }
        return tabIntern(sender, isPlayer, args)
    }
    /**
     * Utility-Methode: Gibt eine Tabliste mit passenden Spielern zurück
     * @param arg Das Argument, das geprüft werden soll
     * @param players Die zu prüfenden Spieler (standardmäßig alle Spieler auf dem Server)
     * @return Die fertige Liste
     */
    protected fun tabPlayers(arg: String, players: Collection<Player> = plugin.server.onlinePlayers): List<String> {
        return players
            .map { it.name }
            .filter { it.startsWith(arg, true) }
            .toList()
    }

    protected fun tabPlayers(arg: String) : List<String> = tabPlayers(arg, plugin.server.onlinePlayers)

    /**
     * Utility-Methode: Gibt eine Tabliste mit den Subcommands zurück
     * @param arg Das Argument, das geprüft werden soll
     */
    protected fun tabSubCommands(arg: String) : List<String> {
        return sub
            .map { it.key }
            .filter { it.startsWith(arg, true) }
            .toList()
    }

    /**
     * Utility-Methode: Gibt eine Tabliste mit passenden Objekten zurück
     * @param T Der Typ der Objekte, sollte [Object.toString] überschreiben
     * @param arg Das Argument, das geprüft werden soll
     * @param coll Die zu prüfenden Objekte
     * @return Die fertige Liste
     */
    protected fun <T> tabCollection(arg: String, coll: Collection<T>): List<String> {
        return coll
            .map { it.toString() }
            .filter { it.startsWith(arg, true) }
            .toList()
    }

    fun <T> List<T>.paginate(page: Int, entriesPerPage: Int): List<T> {
        if (entriesPerPage <= 0 || page < 0 || isEmpty()) return listOf()

        val startIndex = min(page * entriesPerPage, size - 1)
        val endIndex = min((page + 1) * entriesPerPage, size)
        if (endIndex == startIndex) return listOf()

        return subList(startIndex, endIndex)
    }

    fun <T> List<T>.getPageCount(entriesPerPage: Int): Int {
        val num = size
        val count = num / entriesPerPage
        return if (num % entriesPerPage == 0) count else count + 1
    }
}
