package io.github.mh321productions.serverapi.configio

import io.github.mh321productions.serverapi.configio.ConfigIO.ConfigDecodeResult
import io.github.mh321productions.serverapi.configio.conversion.*

/**
 * A representation of a config entry
 */
class ConfigEntry private constructor(val name: String, val type: EntryType, value: Any) {

    enum class EntryType(val index : kotlin.Byte, val defaultValue : Any, val encodeValue : (Any) -> ByteArray,
                         val decodeData: (kotlin.String, List<kotlin.Byte>) -> (ConfigDecodeResult)) {

        Boolean(
            0, false,
            {value -> (value as kotlin.Boolean).toByteArray()},
            {name, data ->
                if (data.isNotEmpty()) ConfigDecodeResult(ConfigEntry(name, data[0].toBoolean()), 1)
                else throw IllegalArgumentException("Not enough data")
            }
        ),

        Byte(
            1, 0.toByte(),
            {value -> byteArrayOf(value as kotlin.Byte)},
            {name, data ->
                if (data.isNotEmpty()) ConfigDecodeResult(ConfigEntry(name, data[0]), 1)
                else throw IllegalArgumentException("Not enough data")
            }
        ),

        Int(
            2, 0,
            {value -> (value as kotlin.Int).toByteArray()},
            {name, data ->
                if (data.size >= 4) ConfigDecodeResult(ConfigEntry(name, data.subList(0, 4).toByteArray().toInt()), 4)
                else throw IllegalArgumentException("Not enough data")
            }
        ),

        Long(
            3, 0L,
            {value -> (value as kotlin.Long).toByteArray()},
            {name, data ->
                if (data.size >= 8) ConfigDecodeResult(ConfigEntry(name, data.subList(0, 8).toByteArray().toLong()), 8)
                else throw IllegalArgumentException("Not enough data")
            }
        ),

        Float(
            4, 0.0f,
            {value -> (value as kotlin.Float).toRawBits().toByteArray()},
            {name, data ->
                if (data.size >= 4) ConfigDecodeResult(ConfigEntry(name, kotlin.Float.fromBits(data.subList(0, 4).toByteArray().toInt())), 4)
                else throw IllegalArgumentException("Not enough data")
            }
        ),

        Double(
            5, 0.0,
            {value -> (value as kotlin.Double).toRawBits().toByteArray()},
            {name, data ->
                if (data.size >= 8) ConfigDecodeResult(ConfigEntry(name, kotlin.Double.fromBits(data.subList(0, 8).toByteArray().toLong())), 8)
                else throw IllegalArgumentException("Not enough data")
            }
        ),

        String(
            6, "",
            {value -> (value as kotlin.String).toNullTerminatedByteArray(Charsets.UTF_8)},
            {name, data ->
                val index = data.indexOf(0)

                if (index != -1) ConfigDecodeResult(ConfigEntry(name, data.subList(0, index).toByteArray().toString(Charsets.UTF_8)), index + 1)
                else throw IllegalArgumentException("Not enough data")
            }
        );

        companion object {
            const val SIZE = 1L

            fun getType(index: kotlin.Byte) = when(index) {
                Boolean.index -> Boolean
                Byte.index -> Byte
                Int.index -> Int
                Long.index -> Long
                Float.index -> Float
                Double.index -> Double
                String.index -> String
                else -> throw IllegalArgumentException("The Byte ${index.toInt()} is not a valid type")
            }
        }
    }

    class WrongTypeException(givenType: EntryType, neededType: EntryType)
        : IllegalStateException("The entry is of type $neededType, not $givenType")

    constructor(name: String, value: Boolean) : this(name, EntryType.Boolean, value)
    constructor(name: String, value: Byte) : this(name, EntryType.Byte, value)
    constructor(name: String, value: Int) : this(name, EntryType.Int, value)
    constructor(name: String, value: Long) : this(name, EntryType.Long, value)
    constructor(name: String, value: Float) : this(name, EntryType.Float, value)
    constructor(name: String, value: Double) : this(name, EntryType.Double, value)
    constructor(name: String, value: String) : this(name, EntryType.String, value)
    constructor(name: String, type: EntryType) : this(name, type, type.defaultValue)

    init {
        if (name.isEmpty()) throw IllegalArgumentException("The name cannot be empty")
    }

    private var value: Any = value
        set(value) {
            field = value
            dirtyCallback()
        }

    var boolValue : Boolean
        get() = if (checkType(EntryType.Boolean)) value as Boolean else throw WrongTypeException(EntryType.Boolean, type)
        set(v) = if (checkType(EntryType.Boolean)) value = v else throw WrongTypeException(EntryType.Boolean, type)

    var byteValue : Byte
        get() = if (checkType(EntryType.Byte)) value as Byte else throw WrongTypeException(EntryType.Byte, type)
        set(v) = if (checkType(EntryType.Byte)) value = v else throw WrongTypeException(EntryType.Byte, type)

    var intValue : Int
        get() = if (checkType(EntryType.Int)) value as Int else throw WrongTypeException(EntryType.Int, type)
        set(v) = if (checkType(EntryType.Int)) value = v else throw WrongTypeException(EntryType.Int, type)

    var longValue : Long
        get() = if (checkType(EntryType.Long)) value as Long else throw WrongTypeException(EntryType.Long, type)
        set(v) = if (checkType(EntryType.Long)) value = v else throw WrongTypeException(EntryType.Long, type)

    var floatValue : Float
        get() = if (checkType(EntryType.Float)) value as Float else throw WrongTypeException(EntryType.Float, type)
        set(v) = if (checkType(EntryType.Float)) value = v else throw WrongTypeException(EntryType.Float, type)

    var doubleValue : Double
        get() = if (checkType(EntryType.Double)) value as Double else throw WrongTypeException(EntryType.Double, type)
        set(v) = if (checkType(EntryType.Double)) value = v else throw WrongTypeException(EntryType.Double, type)

    var stringValue : String
        get() = if (checkType(EntryType.String)) value as String else throw WrongTypeException(EntryType.String, type)
        set(v) = if (checkType(EntryType.String)) value = v else throw WrongTypeException(EntryType.String, type)

    var dirtyCallback : () -> Unit = {}

    fun encode() : ByteArray {
        val nameArray = name.toNullTerminatedByteArray(Charsets.UTF_8)
        val valArray = type.encodeValue(value)

        return byteArrayOf(*nameArray, type.index, *valArray)
    }

    fun getBinarySize() : Long {
        val nameSize = name.toByteArray(Charsets.UTF_8).size + 1
        val valueSize = when(type) {
            EntryType.Boolean -> 1
            EntryType.Byte -> 1
            EntryType.Int,
            EntryType.Float-> 4
            EntryType.Long,
            EntryType.Double-> 8
            EntryType.String -> (value as String).toByteArray(Charsets.UTF_8).size + 1
        }

        return nameSize + EntryType.SIZE + valueSize
    }

    private fun checkType(type: EntryType) = this.type == type
}