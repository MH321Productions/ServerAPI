package io.github.mh321productions.serverapi.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import io.github.mh321productions.serverapi.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

/**
 * Intern: Der Command-Executor und Tab-Completer des Plugins
 * @author 321Productions
 *
 */
public class PixelExecutor implements TabExecutor {
	
	private Main plugin;
	private HashMap<String, SubCommand> commands = new HashMap<>();
	
	
	public PixelExecutor(Main plugin) {
		this.plugin = plugin;
	}
	
	public void registerCommand(String command, SubCommand executor) {
		if (commands.containsKey(command)) return;
		
		PluginCommand cmd = plugin.getCommand(command);
		if (cmd == null) return;
		
		commands.put(command, executor);
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (Entry<String, SubCommand> e: commands.entrySet()) {
			if (command.getName().equalsIgnoreCase(e.getKey())) return e.getValue().onExecute(sender, (sender instanceof Player), Lists.newArrayList(args));
		}
		
		sender.sendMessage("Es gab einen Fehler beim Ausführen des Commands, da er intern nicht gefunden werden konnte.");
		sender.sendMessage("Wende dich hierfür ans Dev-Team (aber bitte über die Supporter ;D)");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		for (Entry<String, SubCommand> e: commands.entrySet()) {
			if (command.getName().equalsIgnoreCase(e.getKey())) return e.getValue().onTab(sender, sender instanceof Player, Lists.newArrayList(args));
		}
		
		
		return SubCommand.emptyList;
	}
}
