package io.github.mh321productions.serverapi.command

import com.google.common.collect.Lists
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * Der Command-Executor und Tab-Completer des Plugins
 * @author 321Productions
 */
class PixelExecutor<PLUGIN : JavaPlugin>(private val plugin: PLUGIN) : TabExecutor {

    private val commands = HashMap<String, SubCommand<PLUGIN>>()

    fun registerCommand(command: String, executor: SubCommand<PLUGIN>) {
        if (commands.containsKey(command)) return

        val cmd = plugin.getCommand(command) ?: return
        commands[command] = executor
        cmd.setExecutor(this)
        cmd.tabCompleter = this
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        for ((key, value) in commands) {
            if (command.name.equals(key, ignoreCase = true)) return value.onExecute(
                sender,
                sender is Player,
                Lists.newArrayList(*args)
            )
        }
        sender.sendMessage("Es gab einen Fehler beim Ausführen des Commands, da er intern nicht gefunden werden konnte.")
        sender.sendMessage("Wende dich hierfür ans Dev-Team (aber bitte über die Supporter ;D)")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        for ((key, value) in commands) {
            if (command.name.equals(key, ignoreCase = true)) return value.onTab(
                sender,
                sender is Player,
                Lists.newArrayList(*args)
            )
        }
        return SubCommand.emptyList
    }
}
