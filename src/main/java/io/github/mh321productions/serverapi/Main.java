package io.github.mh321productions.serverapi;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Starting");
    }

    @Override
    public void onDisable() {
        getLogger().info("Stopping");
    }
}
