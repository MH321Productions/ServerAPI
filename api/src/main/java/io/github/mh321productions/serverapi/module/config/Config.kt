package io.github.mh321productions.serverapi.module.config

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.configio.ConfigEntry
import io.github.mh321productions.serverapi.configio.ConfigIO
import java.io.File
import java.io.IOException
import java.util.logging.Level

/**
 * The abstract wrapper for a configuration file
 */
abstract class Config(protected val main: Main, protected val file: File) {

    protected val entries = mutableMapOf<String, ConfigEntry>()
    protected var dirty = false

    fun isDirty() = dirty

    fun hasEntry(name: String) = entries.containsKey(name)

    fun getEntry(name: String) = entries[name] ?: throw IllegalArgumentException("No entry with the name \"$name\" exists")

    fun setEntry(name: String, entry: ConfigEntry) {
        entries[name]?.dirtyCallback = {} //Remove callback from old entry

        entry.dirtyCallback = { dirty = true }
        entries[name] = entry
    }

    fun removeEntry(name: String) {
        entries.remove(name)
    }

    fun loadFromFile() : Boolean {
        return try {
            val list = ConfigIO.loadFile(file)
            entries.clear()
            list.forEach { entries[it.name] = it }
            dirty = false
            true
        } catch (ex: Exception) {
            when(ex) {
                is IOException -> main.logger.log(Level.SEVERE, "Couldn't load the config file \"${file.absolutePath}\":", ex)
                is IllegalArgumentException,
                is ConfigEntry.WrongTypeException -> main.logger.log(Level.SEVERE, "Couldn't decode entries from the config file \"${file.absolutePath}\":", ex)
                else -> main.logger.log(Level.SEVERE, "An error occurred while loading the config file \"${file.absolutePath}\"", ex)
            }
            false
        }
    }

    fun saveToFile() : Boolean {
        return try {
            val list = entries.values.toList()
            ConfigIO.saveFile(file, list)
            dirty = false
            true
        } catch (ex: IOException) {
            main.logger.log(Level.SEVERE, "Couldn't load the config file \"${file.absolutePath}\":", ex)
            false
        }
    }
}
