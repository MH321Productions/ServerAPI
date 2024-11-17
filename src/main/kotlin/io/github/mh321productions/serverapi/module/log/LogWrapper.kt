package io.github.mh321productions.serverapi.module.log

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.util.logging.PixelLogHandler
import java.io.File
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Eine Wrapper-Klasse für das [Logging-Modul][LogModule]
 * @author 321Productions
 */
class LogWrapper(plugin: Main, val sub: SubPlugin) {

    val log: Logger = sub.logger
    val handler: PixelLogHandler
    private val logFile: File = plugin.filesystem.getLogFile(sub)

    init {
        log.info("Starte API Logger")

        handler = try {
            val handler = PixelLogHandler(logFile)
            log.addHandler(handler)
            log.info("API Logger gestartet")
            handler
        } catch (e: IOException) {
            log.log(Level.SEVERE, "Der API Logger konnte nicht gestartet werden:", e)
            throw e
        }
    }

    fun end() {
        log.info("Stoppe API Logger")

        handler.close()
        log.removeHandler(handler)

        log.info("API Logger gestoppt")
    }
}
