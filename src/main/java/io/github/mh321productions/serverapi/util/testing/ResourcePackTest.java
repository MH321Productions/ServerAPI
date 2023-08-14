package io.github.mh321productions.serverapi.util.testing;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.github.mh321productions.serverapi.Main;
import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

public class ResourcePackTest extends SubCommand {
	
	private static final ArrayList<String> sub = Lists.newArrayList("set", "reset");
	private String link;

	public ResourcePackTest(Main plugin, APIImplementation api) {
		super(plugin, api);
		link = "https://github.com/MH321Productions/AndyChi.aternos.me-MurderMinigame/raw/master/Murder%20Resource%20Pack.zip";
	}

	@Override
	protected boolean executeIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		if (!isPlayer) {
			sender.sendMessage("Nur Spieler können diesen Command ausführen");
			return true;
		} else if (args.size() == 0) return false;
		
		Player player = (Player) sender;
		
		if (args.get(0).equalsIgnoreCase(sub.get(0))) { //set
			player.sendMessage("Setze Resource Pack");
			try {
				URL url = new URL(link);
				MessageDigest sha = MessageDigest.getInstance("SHA1");
				byte[] hash = sha.digest(url.openStream().readAllBytes());
				player.setResourcePack(link, hash);
				
			} catch (IOException | NoSuchAlgorithmException e) {
				player.sendMessage("Das Pack konnte nicht geladen werden: " + e.getClass().getSimpleName() + " -> " + e.getLocalizedMessage());
				player.setResourcePack(link);
				e.printStackTrace();
			}
			
		} else if (args.get(0).equalsIgnoreCase(sub.get(1))) { //reset
			player.sendMessage("Setze Resource Pack zurück");
			player.setResourcePack("https://www.google.com");
		}
		
		return true;
	}

	@Override
	protected List<String> tabIntern(CommandSender sender, boolean isPlayer, List<String> args) {
		if (!isPlayer) return emptyList;
		
		ArrayList<String> ausgabe = new ArrayList<>();
		
		if (args.size() == 1) for (String s: sub) if (s.toLowerCase().startsWith(args.get(0).toLowerCase())) ausgabe.add(s);
		
		return ausgabe;
	}

}
