package io.github.mh321productions.serverapi.util.formatting

import io.github.mh321productions.serverapi.util.permission.Rank
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

object StringFormatter {
    private const val formatThree = "%.3f"
    private const val formatOne = "%.1f"

    /**
     * Der aktuelle Zeitpunkt
     */
    val currentDateTime: ZonedDateTime
        get() = ZonedDateTime.now(ZoneId.systemDefault())

    /**
     * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy, hh:mm:ss
     * @param time Der Zeitpunkt
     * @return Ein String, den man dem User ausgeben kann (z.B im Chat)
     */
    /**
     * Formatiert den jetzigen Zeitpunkt folgendermaßen: tt.mm.yyyy, hh:mm:ss
     * @return Ein String, den man dem User ausgeben kann (z.B im Chat)
     */
    @JvmStatic
	@JvmOverloads
    fun formatDateTime(time: ZonedDateTime = currentDateTime): String {
        val builder = StringBuilder()

        val tag = time.dayOfMonth
        val monat = time.monthValue
        val jahr = time.year
        val h = time.hour
        val min = time.minute
        val sec = time.second


        //Tag
        if (tag < 10) builder.append(0)
        builder.append(tag)
        builder.append('.')


        //Monat
        if (monat < 10) builder.append(0)
        builder.append(monat)
        builder.append('.')


        //Jahr
        builder.append(jahr)
        builder.append(", ")


        //Stunde
        if (h < 10) builder.append(0)
        builder.append(h)
        builder.append(':')


        //Minute
        if (min < 10) builder.append(0)
        builder.append(min)
        builder.append(':')


        //Sekunde
        if (sec < 10) builder.append(0)
        builder.append(sec)

        return builder.toString()
    }

    /**
     * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy, hh:mm:ss
     * @param time Der Zeitpunkt
     * @return Ein String, den man dem User ausgeben kann (z.B im Chat)
     */
    fun formatDateTime(time: LocalDateTime): String {
        return formatDateTime(ZonedDateTime.of(time, ZoneId.systemDefault()))
    }

    /**
     * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy, hh:mm:ss
     * @param instant Der Zeitpunkt
     * @return Ein String, den man dem User ausgeben kann (z.B im Chat)
     */
	@JvmStatic
	fun formatDateTime(instant: Instant): String {
        return formatDateTime(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
    }

    /**
     * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy.hh.mm.ss
     * @param time Der Zeitpunkt
     * @return Ein String, den man intern speichern kann
     */
    /**
     * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy.hh.mm.ss
     * @return Ein String, den man intern speichern kann
     */
    @JvmStatic
	@JvmOverloads
    fun formatDateTimeSave(time: ZonedDateTime = currentDateTime): String {
        val builder = StringBuilder()

        val tag = time.dayOfMonth
        val monat = time.monthValue
        val jahr = time.year
        val h = time.hour
        val min = time.minute
        val sec = time.second
        val nano = time.nano


        //Tag
        builder.append(tag)
        builder.append('.')


        //Monat
        builder.append(monat)
        builder.append('.')


        //Jahr
        builder.append(jahr)
        builder.append(".")


        //Stunde
        builder.append(h)
        builder.append('.')


        //Minute
        builder.append(min)
        builder.append('.')


        //Sekunde
        builder.append(sec)
        builder.append('.')


        //Nanosekunde
        builder.append(nano)

        return builder.toString()
    }

    /**
     * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy.hh.mm.ss
     * @param time Der Zeitpunkt
     * @return Ein String, den man intern speichern kann
     */
    fun formatDateTimeSave(time: LocalDateTime) = formatDateTimeSave(ZonedDateTime.of(time, ZoneId.systemDefault()))

    /**
     * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy.hh.mm.ss
     * @param instant Der Zeitpunkt
     * @return Ein String, den man intern speichern kann
     */
    fun formatDateTimeSave(instant: Instant) = formatDateTimeSave(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))

    /**
     * Formatiert eine Location in einen speicherbaren String
     * @param loc Die Location
     * @return Der speicherbare String
     */
	@JvmStatic
	fun formatLocation(loc: Location): String {
        val builder = StringBuilder()


        //Welt
        if (loc.world == null) builder.append("null")
        else builder.append(loc.world!!.name)
        builder.append(',')


        //X
        builder.append(loc.x)
        builder.append(',')


        //Y
        builder.append(loc.y)
        builder.append(',')


        //Z
        builder.append(loc.z)
        builder.append(',')


        //Yaw
        builder.append(loc.yaw)
        builder.append(',')


        //Pitch
        builder.append(loc.pitch)

        return builder.toString()
    }

    /**
     * Formatiert eine Location in einen speicherbaren String
     * @param player Der Spieler, dessen Location formatiert werden soll
     * @return Der speicherbare String
     */
    fun formatLocation(player: Player) = formatLocation(player.location)

    /**
     * Formatiert eine Location in einen String, den man im Chat anzeigen kann
     * @param loc Die Location
     * @return Der speicherbare String
     */
	@JvmStatic
    @JvmOverloads
	fun formatChatLocation(loc: Location, showWorld: Boolean = true): String {
        val builder = StringBuilder()

        //Welt
        if (showWorld) {
            if (loc.world == null) builder.append("null")
            else builder.append(loc.world!!.name)
            builder.append(' ')
        }

        //X
        builder.append(String.format(formatThree, loc.x))
        builder.append(' ')

        //Y
        builder.append(String.format(formatThree, loc.y))
        builder.append(' ')

        //Z
        builder.append(String.format(formatThree, loc.z))
        builder.append(' ')

        //Yaw
        builder.append(String.format(formatOne, loc.yaw))
        builder.append(' ')

        //Pitch
        builder.append(String.format(formatOne, loc.pitch))

        return builder.toString()
    }

    /**
     * Formatiert einen Spielernamen mit Prefix und Suffix des gegebenen Ranges
     * @param player Der Spieler
     * @param rank Der zu nutzende Rang
     * @return Der formatierte String
     */
	@JvmStatic
	fun formatPlayerName(player: Player, rank: Rank): String {
        return rank.prefix + player.displayName + rank.suffix
    }

    /**
     * Formatiert einen Spielernamen mit Prefix und Suffix des gegebenen Ranges
     * @param uuid Die UUID des Spielers
     * @param rank Der zu nutzende Rang
     * @return Der formatierte String
     */
    @JvmStatic
    fun formatPlayerName(uuid: UUID, rank: Rank): String {
        return rank.prefix + Bukkit.getServer().getOfflinePlayer(uuid).name + rank.suffix
    }
}
