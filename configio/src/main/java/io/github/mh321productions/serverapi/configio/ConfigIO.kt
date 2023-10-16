package io.github.mh321productions.serverapi.configio

import io.github.mh321productions.serverapi.configio.conversion.toByteArray
import io.github.mh321productions.serverapi.configio.conversion.toLong
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

object ConfigIO {

    private val MAGIC_VALUE = "SAC".toByteArray(Charsets.UTF_8)
    private const val MIN_UNCOMPRESSED_SIZE = 4L

    data class ConfigDecodeResult(val entry: ConfigEntry, val data: Any, val readBytes: Int)

    @Throws(IOException::class, IllegalArgumentException::class)
    fun loadFile(file: File) : List<ConfigEntry> {
        val fin = FileInputStream(file)

        //Check Magic Value
        val magic = ByteArray(3)
        if (fin.read(magic) == -1 || !magic.contentEquals(MAGIC_VALUE)) {
            fin.close()
            throw IOException("The Magic value wasn't found")
        }

        //Check uncompressed size
        val sizeArr = ByteArray(8)
        if (fin.read(sizeArr) == -1) {
            fin.close()
            throw IOException("The uncompressed size was not found")
        }
        val size = sizeArr.toLong()
        if (size < MIN_UNCOMPRESSED_SIZE) {
            fin.close()
            throw IllegalArgumentException("No entries are present")
        }

        //Decode entries
        val zip = InflaterInputStream(fin)
        val dataArr = ByteArray(size.toInt())
        zip.read(dataArr)
        zip.close()

        val data = dataArr.toMutableList()
        val entries = mutableListOf<ConfigEntry>()
        var name: String
        var find: Int
        var index : Byte
        var type : EntryType
        var result : ConfigDecodeResult
        while (data.isNotEmpty()) {
            find = data.indexOf(0)
            if (find == -1) break

            name = data.subList(0, find).toByteArray().toString(Charsets.UTF_8)
            index = data[find + 1]
            //data.removeAll(data.subList(0, find + 2))
            for (i in 1..find + 2) data.removeFirst()
            try {
                type = EntryType.getType(index)
                result = type.decodeData(name, data)

                entries.add(result.entry)
                //data.removeAll(data.subList(0, result.readBytes))
                for (i in 1.. result.readBytes) data.removeFirst()
            } catch (ex: IllegalArgumentException) {
                println("The entry \"$name\" Couldn't be decoded (${ex.message}). Skipping")
                continue
            }
        }

        return entries
    }

    @Throws(IOException::class)
    fun saveFile(file: File, entries: List<ConfigEntry>) {
        val out = FileOutputStream(file)
        out.write(MAGIC_VALUE)

        val uncompressedSize = entries
            .map { it.getBinarySize() }
            .reduce { a, b -> a + b }
        out.write(uncompressedSize.toByteArray())


        val zip = DeflaterOutputStream(out)
        entries.forEach { zip.write(it.encode()) }

        zip.close()
    }
}