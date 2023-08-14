package io.github.mh321productions.serverapi.api;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import io.github.mh321productions.serverapi.Main;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Interne Klasse, implementiert das SubPlugin-Interface, um den <br/>
 * Server zu nutzen (kann mit anderen SubPlugins instanziiert werden, <br/>
 * um z.B Reportgründe zu spezifizieren)
 * @author 321Productions
 *
 */
public class ServerSubPlugin implements SubPlugin {
	
	public static ServerSubPlugin emptyServer;
	private static Main plugin = null;
	
	private String name;
	private SubPlugin sub;
	
	/**
	 * Initialisiert eine Instanz mit einem Sub-Plugin
	 * @param plugin Das Main-Plugin
	 * @param sub Das Sub-Plugin
	 */
	public ServerSubPlugin(SubPlugin sub) {
		this.sub = sub;
		
		if (sub != null) name = "Server (" + sub.getName() + ")";
		else name = "Server";
	}
	
	/**
	 * Initialisiert eine Instanz mit einem Spieler und sucht nach dem Sub-Plugin
	 * @param plugin Das Main-Plugin
	 * @param player Der Spieler
	 */
	public ServerSubPlugin(Player player) {
		this.sub = null;
		for (SubPlugin sub : plugin.api.getLoadedSubs()) {
			if (sub.isPlayerInGame(player)) {
				this.sub = sub;
				break;
			}
		}
		
		if (sub != null) name = "Server (" + sub.getName() + ")";
		else name = "Server";
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataFolder();
	}

	@Override
	public Logger getLogger() {
		return plugin.getLogger();
	}

	@Override
	public boolean isPlayerInGame(Player player) {
		return true;
	}

	@Override
	public String getWorldName(World world) {
		if (sub != null) return sub.getWorldName(world);
		
		return world.getName();
	}

	public static void setMain(Main main) {
		if (plugin != null) return;
		plugin = main;
		emptyServer = new ServerSubPlugin((SubPlugin) null);
	}

	@Override
	public List<Player> getPlayersRelativeTo(Player focus, PlayerFilter filter) {
		return focus.getWorld().getPlayers();
	}
}
