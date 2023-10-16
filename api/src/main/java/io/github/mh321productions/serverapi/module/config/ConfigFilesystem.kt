package io.github.mh321productions.serverapi.module.config

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.module.ModuleType
import java.io.File
import java.util.*

class ConfigFilesystem(private val main: Main, private val module: ConfigModule) {

    private var rootFolder = File("")
    private var statsFolder = File("")
    private var configFolder = File("")

    fun init() : Boolean {
        rootFolder = main.filesystem.getModuleFolder(ModuleType.Config)
        statsFolder = File(rootFolder, "stats")
        configFolder = File(rootFolder, "config")

        var res = true
        if (!statsFolder.exists() && !statsFolder.mkdir()) res = false
        if (!configFolder.exists() && !configFolder.mkdir()) res = false

        return res
    }

    fun getPlayerConfigFile(uuid: UUID) : File = File(configFolder, "${uuid.toString()}.sac")

    fun getPlayerStatFile(uuid: UUID) : File = File(statsFolder, "${uuid.toString()}.sac")
}