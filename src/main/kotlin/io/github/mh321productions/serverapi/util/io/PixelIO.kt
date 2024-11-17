package io.github.mh321productions.serverapi.util.io

import java.io.*
import java.nio.charset.StandardCharsets

/**
 * Eine Utility-Klasse, die Einige IO-Operationen zur Verfügung stellt
 * @author 321Productions
 */
object PixelIO {
    /**
     * Liest alle Zeilen aus einer Datei im UTF-8-Format aus
     * @param file Die auszulesende Datei
     * @return Eine Liste der Zeilen
     * @throws IOException Wenn es einen Fehler beim Lesen gibt (es wird versucht, noch alles noch zu schließen)
     */
    @JvmStatic
	@Throws(IOException::class)
    fun readLines(file: File): List<String> {
        var reader: BufferedReader? = null
        var ex: IOException? = null
        val ausgabe = ArrayList<String>()

        try {
            reader = BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8))
            var line: String

            while ((reader.readLine().also { line = it }) != null) {
                ausgabe.add(line)
            }
        } catch (e: IOException) {
            ex = e
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    if (ex == null) ex = e
                }
            }
        }

        if (ex != null) throw ex

        return ausgabe
    }

    /**
     * Schreibt Zeilen in eine Datei im UTF-8-Format
     * @param file Die Datei, in die geschrieben werden soll (wird automatisch erstellt)
     * @param lines Die Zeilen, die geschrieben werden sollen
     * @throws IOException Wenn es einen Fehler beim Schreiben gibt (es wird versucht, noch alles noch zu schließen)
     */
    @Throws(IOException::class)
    fun writeLines(file: File, lines: List<String>) {
        var writer: BufferedWriter? = null
        var ex: IOException? = null

        try {
            file.createNewFile()
            writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8))

            for (l in lines) {
                writer.write(l)
                writer.newLine()
            }
        } catch (e: IOException) {
            ex = e
        } finally {
            if (writer != null) {
                try {
                    writer.close()
                } catch (e: IOException) {
                    if (ex == null) ex = e
                }
            }
        }

        if (ex != null) throw ex
    }
}
