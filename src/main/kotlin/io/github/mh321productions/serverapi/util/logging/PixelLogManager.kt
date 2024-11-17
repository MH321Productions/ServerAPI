package io.github.mh321productions.serverapi.util.logging

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.module.ModuleType
import java.io.File
import java.io.IOException
import java.util.logging.Level

class PixelLogManager(plugin: Main) {

    private var logHandler: PixelLogHandler? = null
    private val log = plugin.logger

    init {
        log.info("Starte internen Logger")
        try {
            logHandler = PixelLogHandler(File(plugin.filesystem.getModuleFolder(ModuleType.Logging), "API.log"))
            log.addHandler(logHandler)

            log.info("Interner Logger gestartet")
        } catch (e: IOException) {
            log.log(Level.SEVERE, "Konnte den internen Logger nicht starten", e)
        }
    }

    fun stop() {
        if (logHandler != null) {
            log.info("Stoppe internen Logger")

            log.removeHandler(logHandler)
            logHandler!!.close()

            log.info("Interner Logger gestoppt")
        }
    }
}