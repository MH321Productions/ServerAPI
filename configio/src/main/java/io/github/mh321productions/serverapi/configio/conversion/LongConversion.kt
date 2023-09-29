package io.github.mh321productions.serverapi.configio.conversion

fun Long.toByteArray() : ByteArray {
    val bytes = ByteArray(8)
    bytes[7] = (this and 0xFF).toByte()
    bytes[6] = ((this ushr 8) and 0xFF).toByte()
    bytes[5] = ((this ushr 16) and 0xFF).toByte()
    bytes[4] = ((this ushr 24) and 0xFF).toByte()
    bytes[3] = ((this ushr 32) and 0xFF).toByte()
    bytes[2] = ((this ushr 40) and 0xFF).toByte()
    bytes[1] = ((this ushr 48) and 0xFF).toByte()
    bytes[0] = ((this ushr 56) and 0xFF).toByte()
    return bytes
}

@OptIn(ExperimentalUnsignedTypes::class)
fun ULong.toUByteArray() : UByteArray {
    val bytes = UByteArray(8)
    bytes[7] = (this and 0xFFu).toUByte()
    bytes[6] = ((this shr 8) and 0xFFu).toUByte()
    bytes[5] = ((this shr 16) and 0xFFu).toUByte()
    bytes[4] = ((this shr 24) and 0xFFu).toUByte()
    bytes[3] = ((this shr 32) and 0xFFu).toUByte()
    bytes[2] = ((this shr 40) and 0xFFu).toUByte()
    bytes[1] = ((this shr 48) and 0xFFu).toUByte()
    bytes[0] = ((this shr 56) and 0xFFu).toUByte()
    return bytes
}

fun ByteArray.toLong() = toLong(0, 8)

fun ByteArray.toLong(startIndex: Int, endIndex: Int) : Long {
    return if (isEmpty() || startIndex >= size || startIndex == endIndex) 0
    else if (endIndex - startIndex > 8) convertToLong(copyOfRange(startIndex, startIndex + 8))
    else if (endIndex >= size) convertToLong(copyOfRange(startIndex, size))
    else if (startIndex > endIndex) convertToLong(copyOfRange(endIndex, startIndex))
    else convertToLong(copyOfRange(startIndex, endIndex))
}

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toULong() = toULong(0, 8)

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toULong(startIndex: Int, endIndex: Int) : ULong {
    return if (isEmpty() || startIndex >= size || startIndex == endIndex) 0u
    else if (endIndex - startIndex > 8) convertToULong(copyOfRange(startIndex, startIndex + 8))
    else if (endIndex >= size) convertToULong(copyOfRange(startIndex, size))
    else if (startIndex > endIndex) convertToULong(copyOfRange(endIndex, startIndex))
    else convertToULong(copyOfRange(startIndex, endIndex))
}

private fun convertToLong(arr: ByteArray) : Long {
    return arr
        .map { it.toUByte().toLong() }
        .reduce { a, b -> ((a shl 8) or b) }
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun convertToULong(arr: UByteArray) : ULong {
    return arr
        .map { it.toULong() }
        .reduce { a, b -> ((a shl 8) or b) }
}