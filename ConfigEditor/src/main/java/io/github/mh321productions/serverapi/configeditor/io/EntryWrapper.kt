package io.github.mh321productions.serverapi.configeditor.io

import io.github.mh321productions.serverapi.configio.ConfigEntry
import io.github.mh321productions.serverapi.configio.EntryArray
import io.github.mh321productions.serverapi.configio.EntryType
import java.util.*

data class EntryWrapper(var name: String, var type: EntryType, var value: Any) {

    constructor(entry: ConfigEntry) : this(entry.name, entry.type, getEntryValue(entry))
    constructor() : this("", EntryType.Boolean, false)

    fun toEntry() : ConfigEntry {
        //println("Saving entry $name")
        val entry = ConfigEntry(name, type)

        when(type) {
            EntryType.Boolean -> entry.boolValue = value as Boolean
            EntryType.Byte -> entry.byteValue = value as Byte
            EntryType.Int -> entry.intValue = value as Int
            EntryType.Long -> entry.longValue = value as Long
            EntryType.Float -> entry.floatValue = value as Float
            EntryType.Double -> entry.doubleValue = value as Double
            EntryType.String -> entry.stringValue = value as String
            EntryType.UUID -> entry.uuidValue = value as UUID
            EntryType.Array -> entry.arrayValue = value as EntryArray
        }

        return entry
    }

    companion object {
        fun getEntryValue(entry: ConfigEntry) : Any {
            return when(entry.type) {
                EntryType.Boolean -> entry.boolValue
                EntryType.Byte -> entry.byteValue
                EntryType.Int -> entry.intValue
                EntryType.Long -> entry.longValue
                EntryType.Float -> entry.floatValue
                EntryType.Double -> entry.doubleValue
                EntryType.String -> entry.stringValue
                EntryType.UUID -> entry.uuidValue
                EntryType.Array -> entry.arrayValue
            }
        }
    }
}
