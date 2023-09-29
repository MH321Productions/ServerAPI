package io.github.mh321productions.serverapi.configio.conversion

fun Int.toByteArray() : ByteArray {
    val bytes = ByteArray(4)
    bytes[3] = (this and 0xFF).toByte()
    bytes[2] = ((this ushr 8) and 0xFF).toByte()
    bytes[1] = ((this ushr 16) and 0xFF).toByte()
    bytes[0] = ((this ushr 24) and 0xFF).toByte()
    return bytes
}

@OptIn(ExperimentalUnsignedTypes::class)
fun UInt.toUByteArray() : UByteArray {
    val bytes = UByteArray(4)
    bytes[3] = (this and 0xFFu).toUByte()
    bytes[2] = ((this shr 8) and 0xFFu).toUByte()
    bytes[1] = ((this shr 16) and 0xFFu).toUByte()
    bytes[0] = ((this shr 24) and 0xFFu).toUByte()
    return bytes
}

fun ByteArray.toInt() = toInt(0, 4)

fun ByteArray.toInt(startIndex: Int, endIndex: Int) : Int {
    return if (isEmpty() || startIndex >= size || startIndex == endIndex) 0
    else if (endIndex - startIndex > 4) convertToInt(copyOfRange(startIndex, startIndex + 4))
    else if (endIndex >= size) convertToInt(copyOfRange(startIndex, size))
    else if (startIndex > endIndex) convertToInt(copyOfRange(endIndex, startIndex))
    else convertToInt(copyOfRange(startIndex, endIndex))
}

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toUInt() = toUInt(0, 4)

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toUInt(startIndex: Int, endIndex: Int) : UInt {
    return if (isEmpty() || startIndex >= size || startIndex == endIndex) 0u
    else if (endIndex - startIndex > 4) convertToUInt(copyOfRange(startIndex, startIndex + 4))
    else if (endIndex >= size) convertToUInt(copyOfRange(startIndex, size))
    else if (startIndex > endIndex) convertToUInt(copyOfRange(endIndex, startIndex))
    else convertToUInt(copyOfRange(startIndex, endIndex))
}

private fun convertToInt(arr: ByteArray) : Int {
    return arr
        .map { it.toUByte().toInt() }
        .reduce { a, b -> ((a shl 8) or b) }
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun convertToUInt(arr: UByteArray) : UInt {
    return arr
        .map { it.toUInt() }
        .reduce { a, b -> ((a shl 8) or b) }
}