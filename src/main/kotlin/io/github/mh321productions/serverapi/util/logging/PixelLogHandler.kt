package io.github.mh321productions.serverapi.util.logging

import io.github.mh321productions.serverapi.util.formatting.StringFormatter.formatDateTime
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.logging.Handler
import java.util.logging.LogRecord

class PixelLogHandler(logFile: File) : Handler() {

    companion object {
        private const val format = "%s %s %s %s" //Datum/Zeit Level Message Exception
    }

    private val writer = BufferedWriter(OutputStreamWriter(FileOutputStream(logFile), StandardCharsets.UTF_8))

    override fun publish(record: LogRecord) {
        val datum = formatDateTime(record.instant)
        val level = record.level.name
        val message = record.message
        var throwable = ""

        if (record.thrown != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            pw.println()
            record.thrown.printStackTrace(pw)
            pw.close()
            throwable = sw.toString()
        }

        try {
            writer.write(String.format(format, datum, level, message, throwable))
            writer.newLine()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        flush()
    }

    override fun flush() {
        try {
            writer.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(SecurityException::class)
    override fun close() {
        try {
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
