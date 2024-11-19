package io.github.mh321productions.serverapi.module.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

object ConfigIo {
    private val testJson = """
        {
            "server": {
                "friends": {
                    "allowRequests": true
                },
                "party": {
                    "allowRequests": false
                }
            },
            "lobby": {
                "showScoreboard": true,
                 "number": 420
            }
        }
    """.trimIndent()

    fun createTestConfig() = TestConfig.from(testJson)
}

class TestConfig {
    companion object {
        fun from(file: File): TestConfig {
            val config = TestConfig()
            config.load(file)
            return config
        }

        fun from(text: String): TestConfig {
            val config = TestConfig()
            config.load(text)
            return config
        }
    }

    private val gson = Gson()
    private var root = JsonObject()

    fun deserialize(section: String, clazz: Class<*>): Any {
        if (!root.has(section)) return clazz.getDeclaredConstructor().newInstance()

        val sub = root.getAsJsonObject(section)
        return gson.fromJson(sub, clazz)
    }

    fun <T> deserializeGeneric(section: String, clazz: Class<T>): T {
        if (!root.has(section)) return clazz.getDeclaredConstructor().newInstance()

        val sub = root.getAsJsonObject(section)
        return gson.fromJson(sub, clazz)
    }

    inline fun <reified T> deserializeGeneric(section: String): T = deserializeGeneric(section, T::class.java)

    fun load(file: File) {
        val reader = InputStreamReader(GZIPInputStream(file.inputStream()), Charsets.UTF_8)
        root = JsonParser.parseReader(reader).asJsonObject
    }

    fun load(text: String) {
        root = JsonParser.parseString(text).asJsonObject
    }

    /*fun save(file: File) {
        val writer = OutputStreamWriter(GZIPOutputStream(file.outputStream()), Charsets.UTF_8)
    }*/
}