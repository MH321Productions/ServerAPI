package io.github.mh321productions.serverapi.configio.conversion

fun Boolean.toByteArray() : ByteArray {
    val byte = if (this) 1.toByte() else 0.toByte()

    return byteArrayOf(byte)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Boolean.toUByteArray() : UByteArray {
    val byte = if (this) 1.toUByte() else 0.toUByte()

    return ubyteArrayOf(byte)
}

fun ByteArray.toBoolean() = toBoolean(0, 1)

fun ByteArray.toBoolean(startIndex: Int, endIndex: Int) : Boolean {
    return if (isEmpty() || startIndex >= size || startIndex == endIndex) false
    else if (endIndex - startIndex > 1) convertToBoolean(copyOfRange(startIndex, startIndex + 1))
    else if (endIndex >= size) convertToBoolean(copyOfRange(startIndex, size))
    else if (startIndex > endIndex) convertToBoolean(copyOfRange(endIndex, startIndex))
    else convertToBoolean(copyOfRange(startIndex, endIndex))
}

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toBoolean() = toBoolean(0, 1)

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toBoolean(startIndex: Int, endIndex: Int) : Boolean {
    return if (isEmpty() || startIndex >= size || startIndex == endIndex) false
    else if (endIndex - startIndex > 1) convertToBoolean(copyOfRange(startIndex, startIndex + 1))
    else if (endIndex >= size) convertToBoolean(copyOfRange(startIndex, size))
    else if (startIndex > endIndex) convertToBoolean(copyOfRange(endIndex, startIndex))
    else convertToBoolean(copyOfRange(startIndex, endIndex))
}

fun Byte.toBoolean() : Boolean = this != 0.toByte()

fun UByte.toBoolean() : Boolean = this != 0.toUByte()

private fun convertToBoolean(arr: ByteArray) : Boolean {
    return arr[0] != 0.toByte()
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun convertToBoolean(arr: UByteArray) : Boolean {
    return arr[0] != 0.toUByte()
}