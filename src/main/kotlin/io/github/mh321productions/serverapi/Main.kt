package io.github.mh321productions.serverapi

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import io.github.mh321productions.serverapi.api.APIImplementation
import io.github.mh321productions.serverapi.api.ServerSubPlugin
import io.github.mh321productions.serverapi.command.PixelExecutor
import io.github.mh321productions.serverapi.util.config.PluginConfig
import io.github.mh321productions.serverapi.util.io.PixelFilesystem
import io.github.mh321productions.serverapi.util.logging.PixelLogManager
import io.github.mh321productions.serverapi.util.permission.PermissionHandler
import io.github.mh321productions.serverapi.util.testing.NodeTest
import io.github.mh321productions.serverapi.util.testing.ResourcePackTest
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	lateinit var filesystem: PixelFilesystem
    private lateinit var logManager: PixelLogManager
	lateinit var api: APIImplementation
	lateinit var cmd: PixelExecutor<Main>
	lateinit var perms: PermissionHandler
    lateinit var protocol: ProtocolManager
    lateinit var conf: PluginConfig

    override fun onEnable() {
        logger.info("Starte Plugin")

        saveDefaultConfig()

        protocol = ProtocolLibrary.getProtocolManager()

        //Utility-Klassen starten
        ServerSubPlugin.init(this)
        conf = PluginConfig(config)
        filesystem = PixelFilesystem(this)
        logManager = PixelLogManager(this)
        cmd = PixelExecutor(this)
        perms = PermissionHandler(this)

        //API starten
        api = APIImplementation(this)

        //Testing
        cmd.registerCommand("pack", ResourcePackTest(this, api))
        cmd.registerCommand("node", NodeTest(this, api))
    }

    override fun onDisable() {
        //API stoppen
        api.stop()

        logger.info("Stoppe Plugin")

        logManager.stop()

        logger.info("Plugin gestoppt")
    }
}
