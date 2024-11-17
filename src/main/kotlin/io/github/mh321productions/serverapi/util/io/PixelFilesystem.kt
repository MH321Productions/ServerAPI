package io.github.mh321productions.serverapi.util.io

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.ModuleType
import java.io.File

class PixelFilesystem(private val plugin: Main) {
    val dataFolder: File
    val baseFolder: File

    private val moduleFolders = mutableMapOf<ModuleType<*>, File>()

    init {
        plugin.logger.info("Starte Filesystem")

        dataFolder = plugin.dataFolder
        baseFolder = plugin.dataFolder.parentFile.parentFile //Zwei Ebenen über dem Pluginordner

        //Modulordner erstellen
        ModuleType.values
            .filter { it.folder != null }
            .forEach {
                val file = File(dataFolder, it.folder!!)
                moduleFolders[it] = file
                if (!file.exists()) file.mkdir()
            }
    }

    /**
     * Gibt den Dateiordner eines Moduls zurück
     * @param module Das Modul, dessen Ordner abgefragt werden soll
     * @return Der Ordner des Moduls
     */
    fun getModuleFolder(module: ModuleType<*>): File? {
        return moduleFolders[module]
    }

    /**
     * Gibt die Logdatei eines Sub-Plugins zurück, um über das Logging-Modul reinzuschreiben
     * @param sub Das im Logging-Modul registrierte Sub-Plugin
     * @return Die Logdatei
     */
    fun getLogFile(sub: SubPlugin): File {
        return File(getModuleFolder(ModuleType.Logging), sub.name + ".log")
    }
}