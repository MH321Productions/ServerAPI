package io.github.mh321productions.serverapi.util.formatting

import org.bukkit.Bukkit
import org.bukkit.Location
import java.time.DateTimeException
import java.time.ZoneId
import java.time.ZonedDateTime

object StringReader {
    /**
     * Dekodiert einen Zeit-String in ein Zeitobjekt
     * @param time Der formatierte String (siehe [StringFormatter.formatDateTimeSave])
     * @return Der Zeitpunkt oder null bei einem Fehler
     */
	@JvmStatic
	fun readDateTime(time: String): ZonedDateTime? {
        val comp = time.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (comp.size != 7) return null

        try {
            return ZonedDateTime.of(
                comp[2].toInt(),
                comp[1].toInt(),
                comp[0].toInt(),
                comp[3].toInt(),
                comp[4].toInt(),
                comp[5].toInt(),
                comp[6].toInt(),
                ZoneId.systemDefault()
            )
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return null
        } catch (e: DateTimeException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Dekodiert einen Location-String in eine [Location].
     * @param loc Der formatierte String (siehe [StringFormatter.formatLocation])
     * @return Die Location oder null bei einem Fehler
     */
	@JvmStatic
	fun readLocation(loc: String): Location? {
        val comp = loc.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (comp.size == 6) {
            return try {
                Location(
                    Bukkit.getWorld(comp[0]),
                    comp[1].toDouble(),
                    comp[2].toDouble(),
                    comp[3].toDouble(),
                    comp[4].toFloat(),
                    comp[5].toFloat()
                )
            } catch (e: NumberFormatException) {
                null
            }
        } else if (comp.size == 4) {
            return try {
                Location(
                    Bukkit.getWorld(comp[0]),
                    comp[1].toDouble(),
                    comp[2].toDouble(),
                    comp[3].toDouble(),
                    0f,
                    0f
                )
            } catch (e: NumberFormatException) {
                null
            }
        }

        return null
    }
}
