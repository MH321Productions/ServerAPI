package io.github.mh321productions.serverapi.configio

import io.github.mh321productions.serverapi.configio.conversion.*

enum class EntryType(val index : kotlin.Byte, val defaultValue : Any, val encodeValue : (Any) -> ByteArray,
                     val decodeData: (kotlin.String, List<kotlin.Byte>) -> (ConfigIO.ConfigDecodeResult)) {

    Boolean(
        0,false,
        {value -> (value as kotlin.Boolean).toByteArray()},
        {name, data ->
            if (data.isNotEmpty()) ConfigIO.ConfigDecodeResult(ConfigEntry(name, data[0].toBoolean()), data[0].toBoolean(), 1)
            else throw IllegalArgumentException("Not enough data")
        }
    ),

    Byte(
        1, 0.toByte(),
        {value -> byteArrayOf(value as kotlin.Byte)},
        {name, data ->
            if (data.isNotEmpty()) ConfigIO.ConfigDecodeResult(ConfigEntry(name, data[0]), data[0], 1)
            else throw IllegalArgumentException("Not enough data")
        }
    ),

    Int(
        2, 0,
        {value -> (value as kotlin.Int).toByteArray()},
        {name, data ->
            if (data.size >= 4) {
                val int = data.subList(0, 4).toByteArray().toInt()
                ConfigIO.ConfigDecodeResult(
                    ConfigEntry(name, int),
                    int, 4
                )
            }
            else throw IllegalArgumentException("Not enough data")
        }
    ),

    Long(
        3, 0L,
        {value -> (value as kotlin.Long).toByteArray()},
        {name, data ->
            if (data.size >= 8) {
                val long = data.subList(0, 8).toByteArray().toLong()
                ConfigIO.ConfigDecodeResult(
                    ConfigEntry(name, long),
                    long, 8
                )
            }
            else throw IllegalArgumentException("Not enough data")
        }
    ),

    Float(
        4, 0.0f,
        {value -> (value as kotlin.Float).toRawBits().toByteArray()},
        {name, data ->
            if (data.size >= 4) {
                val float = kotlin.Float.fromBits(data.subList(0, 4).toByteArray().toInt())
                ConfigIO.ConfigDecodeResult(
                    ConfigEntry(name, float),
                    float, 4
                )
            }
            else throw IllegalArgumentException("Not enough data")
        }
    ),

    Double(
        5, 0.0,
        {value -> (value as kotlin.Double).toRawBits().toByteArray()},
        {name, data ->
            if (data.size >= 8) {
                val double = kotlin.Double.fromBits(data.subList(0, 8).toByteArray().toLong())
                ConfigIO.ConfigDecodeResult(
                    ConfigEntry(name, double),
                    double, 8
                )
            }
            else throw IllegalArgumentException("Not enough data")
        }
    ),

    String(
        6, "",
        {value -> (value as kotlin.String).toNullTerminatedByteArray(Charsets.UTF_8)},
        {name, data ->
            val index = data.indexOf(0)

            if (index != -1) {
                val str = data.subList(0, index).toByteArray().toString(Charsets.UTF_8)
                ConfigIO.ConfigDecodeResult(
                    ConfigEntry(name, str),
                    str, index + 1
                )
            }
            else throw IllegalArgumentException("Not enough data")
        }
    ),

    UUID(
        7, java.util.UUID.randomUUID(),
        {value ->
            val uuid = value as java.util.UUID

            byteArrayOf(*uuid.mostSignificantBits.toByteArray(), *uuid.leastSignificantBits.toByteArray())
        },
        {name, data ->
            if (data.size < 16) throw IllegalArgumentException("Not enough data")

            val msb = data.subList(0, 8).toByteArray().toLong()
            val lsb = data.subList(8, 16).toByteArray().toLong()
            val uuid = java.util.UUID(msb, lsb)

            ConfigIO.ConfigDecodeResult(ConfigEntry(name, uuid), uuid, 16)
        }
    ),

    Array(
        8, EntryArray.emptyArray(Boolean),
        {value -> (value as EntryArray).encode()},

        {name, data ->
            if (data.size < 5) throw IllegalArgumentException("Not enough data")

            val (arr, read) = EntryArray.decode(data)

            ConfigIO.ConfigDecodeResult(ConfigEntry(name, arr), arr, read)
        }
    )
    ;

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
            UUID.index -> UUID
            Array.index -> Array
            else -> throw IllegalArgumentException("The Byte ${index.toInt()} is not a valid type")
        }
    }
}