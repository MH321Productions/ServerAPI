package io.github.mh321productions.serverapi;

import io.github.mh321productions.serverapi.util.PixelFilesystem;
import io.github.mh321productions.serverapi.util.PixelLogManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.mh321productions.serverapi.api.APIImplementation;
import io.github.mh321productions.serverapi.api.ServerSubPlugin;
import io.github.mh321productions.serverapi.command.PixelExecutor;
import io.github.mh321productions.serverapi.util.permission.PermissionHandler;
import io.github.mh321productions.serverapi.util.testing.NodeTest;
import io.github.mh321productions.serverapi.util.testing.ResourcePackTest;

public class Main extends JavaPlugin {
	
	public PixelFilesystem filesystem;
	private PixelLogManager logManager;
	public APIImplementation api;
	public PixelExecutor cmd;
	public PermissionHandler perms;
	
	@Override
	public void onEnable() {
		getLogger().info("Starte Plugin");
		
		saveDefaultConfig();
		
		//Utility-Klassen starten
		ServerSubPlugin.setMain(this);
		filesystem = new PixelFilesystem(this);
		logManager = new PixelLogManager(this);
		cmd = new PixelExecutor(this);
		perms = new PermissionHandler(this);

		//API starten
		api = new APIImplementation(this);
		
		//Testing
		cmd.registerCommand("pack", new ResourcePackTest(this, api));
		cmd.registerCommand("node", new NodeTest(this, api));
	}
	
	@Override
	public void onDisable() {
		//API stoppen
		api.stop();
		
		getLogger().info("Stoppe Plugin");
		
		logManager.stop();
		
		getLogger().info("Plugin gestoppt");
	}
}
