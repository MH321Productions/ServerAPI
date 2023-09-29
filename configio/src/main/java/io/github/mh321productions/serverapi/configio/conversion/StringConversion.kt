package io.github.mh321productions.serverapi.configio.conversion

import java.nio.charset.Charset

fun String.toNullTerminatedByteArray(charset: Charset = Charsets.UTF_8) = byteArrayOf(*toByteArray(charset), 0)

fun ByteArray.toNullTerminatedString(charset: Charset = Charsets.UTF_8) : String {
    val index = indexOf(0)

    return if (index == -1) toString(charset)
    else copyOfRange(0, index).toString(charset)
}