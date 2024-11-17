package io.github.mh321productions.serverapi.util.config

import org.bukkit.configuration.file.FileConfiguration

abstract class ConfigRegistry(config: FileConfiguration)

class PluginConfig(config: FileConfiguration) : ConfigRegistry(config) {

    class Modules(config: FileConfiguration) : ConfigRegistry(config) {

        class Friend(config: FileConfiguration) : ConfigRegistry(config) {

            @JvmField
            val maxFriends = config.getInt("modules.friend.maxFriends")
            @JvmField
            val friendsPerPage = config.getInt("modules.friend.friendsPerPage")

        }

        @JvmField
        val friend = Friend(config)
    }

    @JvmField
    val modules = Modules(config)
}