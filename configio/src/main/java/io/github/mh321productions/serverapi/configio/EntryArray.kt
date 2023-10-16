package io.github.mh321productions.serverapi.configio

import io.github.mh321productions.serverapi.configio.ConfigEntry.WrongTypeException
import io.github.mh321productions.serverapi.configio.conversion.toByteArray
import io.github.mh321productions.serverapi.configio.conversion.toInt
import java.util.*

class EntryArray private constructor(val type: EntryType, entries: List<Any>) {

    private var entries : MutableList<Any> = entries.toMutableList()
        set(value) {
            field = value
            callback()
        }
    var callback: () -> Unit = {}
    val size : Int
        get() = entries.size

    val encodedSize: Int
        get() = HEADER_SIZE + when(type) {
            EntryType.Boolean,
            EntryType.Byte -> entries.size
            EntryType.Int,
            EntryType.Float -> entries.size * 4
            EntryType.Long,
            EntryType.Double -> entries.size * 8
            EntryType.String -> stringEntries.map { it.toByteArray(Charsets.UTF_8).size + 1 }.reduce { a, b -> a + b }
            EntryType.UUID -> entries.size * 16
            EntryType.Array -> arrayEntries.map { it.encodedSize }.reduce { a, b -> a + b }
        }

    var boolEntries : MutableList<Boolean>
        get() = if (checkType(EntryType.Boolean)) WrappedCallbackMutableList(callback, entries) else throw WrongTypeException(type, EntryType.Boolean)
        set(v) = if (checkType(EntryType.Boolean)) entries = v.toMutableList() else throw WrongTypeException(type, EntryType.Boolean)

    var byteEntries : MutableList<Byte>
        get() = if (checkType(EntryType.Byte)) WrappedCallbackMutableList(callback, entries) else throw WrongTypeException(type, EntryType.Byte)
        set(v) = if (checkType(EntryType.Byte)) entries = v.toMutableList() else throw WrongTypeException(type, EntryType.Byte)

    var intEntries : MutableList<Int>
        get() = if (checkType(EntryType.Int)) WrappedCallbackMutableList(callback, entries) else throw WrongTypeException(type, EntryType.Int)
        set(v) = if (checkType(EntryType.Int)) entries = v.toMutableList() else throw WrongTypeException(type, EntryType.Int)

    var longEntries : MutableList<Long>
        get() = if (checkType(EntryType.Long)) WrappedCallbackMutableList(callback, entries) else throw WrongTypeException(type, EntryType.Long)
        set(v) = if (checkType(EntryType.Long)) entries = v.toMutableList() else throw WrongTypeException(type, EntryType.Long)

    var floatEntries : MutableList<Float>
        get() = if (checkType(EntryType.Float)) WrappedCallbackMutableList(callback, entries) else throw WrongTypeException(type, EntryType.Float)
        set(v) = if (checkType(EntryType.Float)) entries = v.toMutableList() else throw WrongTypeException(type, EntryType.Float)

    var doubleEntries : MutableList<Double>
        get() = if (checkType(EntryType.Double)) WrappedCallbackMutableList(callback, entries) else throw WrongTypeException(type, EntryType.Double)
        set(v) = if (checkType(EntryType.Double)) entries = v.toMutableList() else throw WrongTypeException(type, EntryType.Double)

    var stringEntries : MutableList<String>
        get() = if (checkType(EntryType.String)) WrappedCallbackMutableList(callback, entries) else throw WrongTypeException(type, EntryType.String)
        set(v) = if (checkType(EntryType.String)) entries = v.toMutableList() else throw WrongTypeException(type, EntryType.String)

    var uuidEntries : MutableList<UUID>
        get() = if (checkType(EntryType.UUID)) WrappedCallbackMutableList(callback, entries) else throw WrongTypeException(type, EntryType.UUID)
        set(v) = if (checkType(EntryType.UUID)) entries = v.toMutableList() else throw WrongTypeException(type, EntryType.UUID)

    var arrayEntries : MutableList<EntryArray>
        get() = if (checkType(EntryType.Array)) WrappedCallbackMutableList(callback, entries) else throw WrongTypeException(type, EntryType.Array)
        set(v) = if (checkType(EntryType.Array)) entries = v.toMutableList() else throw WrongTypeException(type, EntryType.Array)


    private fun checkType(type: EntryType) = this.type == type

    fun encode() : ByteArray {
        val res = mutableListOf(type.index)

        res.addAll(entries.size.toByteArray().toList())

        res.addAll(
            entries
                .map { type.encodeValue(it) }
                .flatMap { it.toList() }
        )

        return res.toByteArray()
    }

    companion object {

        private const val HEADER_SIZE = 1 + 4

        fun emptyArray(type: EntryType) = EntryArray(type, listOf())
        fun boolArray(entries: List<Boolean>) = EntryArray(EntryType.Boolean, entries)
        fun byteArray(entries: List<Byte>) = EntryArray(EntryType.Byte, entries)
        fun intArray(entries: List<Int>) = EntryArray(EntryType.Int, entries)
        fun longArray(entries: List<Long>) = EntryArray(EntryType.Long, entries)
        fun floatArray(entries: List<Float>) = EntryArray(EntryType.Float, entries)
        fun doubleArray(entries: List<Double>) = EntryArray(EntryType.Double, entries)
        fun stringArray(entries: List<String>) = EntryArray(EntryType.String, entries)
        fun uuidArray(entries: List<UUID>) = EntryArray(EntryType.UUID, entries)
        fun arrayArray(entries: List<EntryArray>) = EntryArray(EntryType.Array, entries)

        fun decode(data: List<Byte>) : Pair<EntryArray, Int> {
            val type = EntryType.getType(data[0])
            val entryCount = data.subList(1, 5).toByteArray().toInt()
            val entries = mutableListOf<Any>()
            var readBytes = HEADER_SIZE

            for (i: Int in 1..entryCount) {
                try {
                    val (_, dec, size) = type.decodeData("dummy", data.subList(readBytes, data.size))

                    readBytes += size
                    entries.add(dec)
                } catch (ex: IllegalArgumentException) {
                    continue
                }
            }

            return Pair(EntryArray(type, entries), readBytes)
        }
    }
}