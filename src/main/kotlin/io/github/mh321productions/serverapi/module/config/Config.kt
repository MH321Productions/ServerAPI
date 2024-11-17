package io.github.mh321productions.serverapi.module.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import io.github.mh321productions.serverapi.Main
import io.github.mh321productions.serverapi.api.ServerSubPlugin
import io.github.mh321productions.serverapi.api.SubPlugin
import io.github.mh321productions.serverapi.module.config.server.ServerConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import java.util.logging.Level
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * The abstract wrapper for a configuration file
 */
abstract class Config(protected val main: Main, protected val module: ConfigModule, protected val file: File, private val getDeserializeClass: (ConfigInfo) -> Class<*>) {

    private val entries = mutableMapOf<SubPlugin, Any>()
    private var root = JsonObject()
    private val gson = Gson()

    val server: ServerConfig
        get() = getEntry(ServerSubPlugin.emptyServer) as ServerConfig

    fun hasEntry(subPlugin: SubPlugin) = entries.containsKey(subPlugin)

    fun getEntry(subPlugin: SubPlugin): Any {
        val entry = entries[subPlugin]
        if (entry != null) return entry

        //Try deserializing the json
        val info = module.pluginConfigClasses[subPlugin] ?: throw IllegalStateException("The plugin ${subPlugin.name} is not registered")
        val newEntry =
            if (root.has(info.jsonName)) gson.fromJson(root[info.jsonName].asJsonObject, getDeserializeClass(info))
            else getDeserializeClass(info).getDeclaredConstructor().newInstance()

        entries[subPlugin] = newEntry
        return newEntry
    }

    fun setEntry(subPlugin: SubPlugin, entry: Any) {
        val info = module.pluginConfigClasses[subPlugin] ?: throw IllegalStateException("The plugin ${subPlugin.name} is not registered")
        require(entry.javaClass == getDeserializeClass(info)) {"The config entry for the plugin ${subPlugin.name} doesn't match the registered class" }
        entries[subPlugin] = entry
    }

    fun removeEntry(subPlugin: SubPlugin) {
        entries.remove(subPlugin)
    }

    fun loadFromFile() : Boolean {
        return try {
            root = JsonParser.parseReader(InputStreamReader(GZIPInputStream(FileInputStream(file)), Charsets.UTF_8)) as JsonObject
            true
        } catch (ex: Exception) {
            when(ex) {
                is IOException -> main.logger.log(Level.SEVERE, "Couldn't load the config file \"${file.absolutePath}\":", ex)
                is JsonParseException -> main.logger.log(Level.SEVERE, "Couldn't parse the config file \"${file.absolutePath}\":", ex)
                else -> main.logger.log(Level.SEVERE, "An error occurred while loading the config file \"${file.absolutePath}\"", ex)
            }
            false
        }
    }

    fun saveToFile() : Boolean {
        return try {
            root = JsonObject()
            entries
                .map { (subPlugin, entry) -> Pair(subPlugin, gson.toJsonTree(entry, getDeserializeClass(subPlugin.configInfo)).asJsonObject) }
                .forEach { (subPlugin, json) -> root.add(subPlugin.configInfo.jsonName, json) }

            val writer = OutputStreamWriter(GZIPOutputStream(FileOutputStream(file)), Charsets.UTF_8)
            writer.write(gson.toJson(root))
            writer.close()
            true
        } catch (ex: IOException) {
            main.logger.log(Level.SEVERE, "Couldn't save the config file \"${file.absolutePath}\":", ex)
            false
        }
    }
}

data class ConfigInfo(val jsonName: String, val configClazz: Class<*>?, val statClazz: Class<*>?)

/**
 * The wrapper for a player's changeable settings
 */
class PlayerConfig(main: Main, module: ConfigModule, val uuid: UUID) : Config(main, module, module.files.getPlayerConfigFile(uuid), {info -> info.configClazz ?: throw IllegalStateException("No config class specified")})

/**
 * The wrapper for a player's statistics, only changeable by plugins
 */
class PlayerStats(main: Main, module: ConfigModule, val uuid: UUID) : Config(main, module, module.files.getPlayerStatFile(uuid), {info -> info.statClazz ?: throw IllegalStateException("No stat class specified")})