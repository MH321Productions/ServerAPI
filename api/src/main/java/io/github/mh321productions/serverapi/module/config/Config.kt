package io.github.mh321productions.serverapi.module.config

import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.configio.ConfigEntry
import io.github.mh321productions.serverapi.configio.ConfigIO
import io.github.mh321productions.serverapi.module.config.server.ServerConfigEntries
import java.io.File
import java.io.IOException
import java.util.*
import java.util.logging.Level

/**
 * The abstract wrapper for a configuration file
 */
abstract class Config(protected val main: Main, protected val module: ConfigModule, protected val file: File) {

    private val entries = mutableMapOf<String, ConfigEntry>()
    private var dirty = false

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

    fun setDefaultEntry(entry: ConfigEntry) {
        if (!entries.containsKey(entry.name)) {
            entry.dirtyCallback = { dirty = true }
            entries[entry.name] = entry
        }
    }

    fun setDefaultEntries(collection: DefaultEntryCollection) {
        collection.getDefaultEntries().forEach(::setDefaultEntry)
    }

    fun loadFromFile() : Boolean {
        return try {
            val list = if (file.exists() && file.isFile()) ConfigIO.loadFile(file) else listOf()
            entries.clear()
            list.forEach {
                it.dirtyCallback = { dirty = true }
                entries[it.name] = it
            }
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
        if (!dirty) return true
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

/**
 * The wrapper for a player's changeable settings
 */
class PlayerConfig(main: Main, module: ConfigModule, val uuid: UUID) : Config(main, module, module.files.getPlayerConfigFile(uuid))

/**
 * The wrapper for a player's statistics, only changeable by plugins
 */
class PlayerStats(main: Main, module: ConfigModule, val uuid: UUID) : Config(main, module, module.files.getPlayerStatFile(uuid))

/**
 * A minimal interface for default entries
 * @see ServerConfigEntries
 */
interface DefaultEntryCollection {
    /**
     * Returns a list of default entries
     */
    fun getDefaultEntries() : List<ConfigEntry>
}