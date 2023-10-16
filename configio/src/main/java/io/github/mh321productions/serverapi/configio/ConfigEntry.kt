package io.github.mh321productions.serverapi.configio

import io.github.mh321productions.serverapi.configio.conversion.toNullTerminatedByteArray
import java.util.*

/**
 * A representation of a config entry, consisting of a name, a type and a value.
 *
 * The name should be as following:
 *
 * ```<plugin>.<category>.<name, separated by _>```
 *
 * all lowercase
 */
class ConfigEntry private constructor(val name: String, val type: EntryType, value: Any) {

    class WrongTypeException(givenType: EntryType, neededType: EntryType)
        : IllegalStateException("The entry is of type $neededType, not $givenType")

    constructor(name: String, value: Boolean) : this(name, EntryType.Boolean, value)
    constructor(name: String, value: Byte) : this(name, EntryType.Byte, value)
    constructor(name: String, value: Int) : this(name, EntryType.Int, value)
    constructor(name: String, value: Long) : this(name, EntryType.Long, value)
    constructor(name: String, value: Float) : this(name, EntryType.Float, value)
    constructor(name: String, value: Double) : this(name, EntryType.Double, value)
    constructor(name: String, value: String) : this(name, EntryType.String, value)
    constructor(name: String, value: UUID) : this(name, EntryType.UUID, value)
    constructor(name: String, value: EntryArray) : this(name, EntryType.Array, value)
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

    var uuidValue : UUID
        get() = if (checkType(EntryType.UUID)) value as UUID else throw WrongTypeException(EntryType.UUID, type)
        set(v) = if (checkType(EntryType.UUID)) value = v else throw WrongTypeException(EntryType.UUID, type)

    var arrayValue : EntryArray
        get() = if (checkType(EntryType.Array)) value as EntryArray else throw WrongTypeException(EntryType.Array, type)
        set(v) {
            if (checkType(EntryType.Array)) {
                value = v
                v.callback = dirtyCallback
            } else throw WrongTypeException(EntryType.Array, type)
        }

    var dirtyCallback : () -> Unit = {}
        set(v) {
            if (type == EntryType.Array) (value as EntryArray).callback = v
            field = v
        }

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
            EntryType.UUID -> 16
            EntryType.Array -> (value as EntryArray).encodedSize
        }

        return nameSize + EntryType.SIZE + valueSize
    }

    private fun checkType(type: EntryType) = this.type == type
}