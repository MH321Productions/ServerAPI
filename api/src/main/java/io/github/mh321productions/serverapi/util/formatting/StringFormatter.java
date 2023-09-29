package io.github.mh321productions.serverapi.util.formatting;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.mh321productions.serverapi.util.permission.Rank;

public final class StringFormatter {
	
	private static final String formatThree = "%.3f";
	private static final String formatOne = "%.1f";
	
	/**
	 * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy, hh:mm:ss
	 * @param time Der Zeitpunkt
	 * @return Ein String, den man dem User ausgeben kann (z.B im Chat)
	 */
	public static String formatDateTime(ZonedDateTime time) {
		StringBuilder builder = new StringBuilder();
		
		int tag = time.getDayOfMonth(), monat = time.getMonthValue(), jahr = time.getYear(), h = time.getHour(), min = time.getMinute(), sec = time.getSecond();
		
		//Tag
		if (tag < 10) builder.append(0);
		builder.append(tag);
		builder.append('.');
		
		//Monat
		if (monat < 10) builder.append(0);
		builder.append(monat);
		builder.append('.');
		
		//Jahr
		builder.append(jahr);
		builder.append(", ");
		
		//Stunde
		if (h < 10) builder.append(0);
		builder.append(h);
		builder.append(':');
		
		//Minute
		if (min < 10) builder.append(0);
		builder.append(min);
		builder.append(':');
		
		//Sekunde
		if (sec < 10) builder.append(0);
		builder.append(sec);
		
		return builder.toString();
	}
	
	/**
	 * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy, hh:mm:ss
	 * @param time Der Zeitpunkt
	 * @return Ein String, den man dem User ausgeben kann (z.B im Chat)
	 */
	public static String formatDateTime(LocalDateTime time) {
		return formatDateTime(ZonedDateTime.of(time, ZoneId.systemDefault()));
	}
	
	/**
	 * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy, hh:mm:ss
	 * @param instant Der Zeitpunkt
	 * @return Ein String, den man dem User ausgeben kann (z.B im Chat)
	 */
	public static String formatDateTime(Instant instant) {
		return formatDateTime(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
	}
	
	/**
	 * Formatiert den jetzigen Zeitpunkt folgendermaßen: tt.mm.yyyy, hh:mm:ss
	 * @return Ein String, den man dem User ausgeben kann (z.B im Chat)
	 */
	public static String formatDateTime() {
		return formatDateTime(getCurrentDateTime());
	}
	
	/**
	 * Gibt den aktuellen Zeitpunkt zurück
	 * @return Der aktuelle Zeitpunkt
	 */
	public static ZonedDateTime getCurrentDateTime() {
		return ZonedDateTime.now(ZoneId.systemDefault());
	}
	
	/**
	 * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy.hh.mm.ss
	 * @param time Der Zeitpunkt
	 * @return Ein String, den man intern speichern kann
	 */
	public static String formatDateTimeSave(ZonedDateTime time) {
		StringBuilder builder = new StringBuilder();
		
		int tag = time.getDayOfMonth(), monat = time.getMonthValue(), jahr = time.getYear(), h = time.getHour(), min = time.getMinute(), sec = time.getSecond(), nano = time.getNano();
		
		//Tag
		builder.append(tag);
		builder.append('.');
		
		//Monat
		builder.append(monat);
		builder.append('.');
		
		//Jahr
		builder.append(jahr);
		builder.append(".");
		
		//Stunde
		builder.append(h);
		builder.append('.');
		
		//Minute
		builder.append(min);
		builder.append('.');
		
		//Sekunde
		builder.append(sec);
		builder.append('.');
		
		//Nanosekunde
		builder.append(nano);
		
		return builder.toString();
	}
	
	/**
	 * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy.hh.mm.ss
	 * @param time Der Zeitpunkt
	 * @return Ein String, den man intern speichern kann
	 */
	public static String formatDateTimeSave(LocalDateTime time) {
		return formatDateTimeSave(ZonedDateTime.of(time, ZoneId.systemDefault()));
	}
	
	/**
	 * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy.hh.mm.ss
	 * @param instant Der Zeitpunkt
	 * @return Ein String, den man intern speichern kann
	 */
	public static String formatDateTimeSave(Instant instant) {
		return formatDateTimeSave(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
	}
	
	/**
	 * Formatiert einen Zeitpunkt folgendermaßen: tt.mm.yyyy.hh.mm.ss
	 * @return Ein String, den man intern speichern kann
	 */
	public static String formatDateTimeSave() {
		return formatDateTimeSave(getCurrentDateTime());
	}
	
	/**
	 * Formatiert eine Location in einen speicherbaren String
	 * @param loc Die Location
	 * @return Der speicherbare String
	 */
	public static String formatLocation(Location loc) {
		StringBuilder builder = new StringBuilder();
		
		//Welt
		if (loc.getWorld() == null) builder.append("null");
		else builder.append(loc.getWorld().getName());
		builder.append(',');
		
		//X
		builder.append(loc.getX());
		builder.append(',');
		
		//Y
		builder.append(loc.getY());
		builder.append(',');
		
		//Z
		builder.append(loc.getZ());
		builder.append(',');
		
		//Yaw
		builder.append(loc.getYaw());
		builder.append(',');
		
		//Pitch
		builder.append(loc.getPitch());
		
		return builder.toString();
	}
	
	/**
	 * Formatiert eine Location in einen speicherbaren String
	 * @param player Der Spieler, dessen Location formatiert werden soll
	 * @return Der speicherbare String
	 */
	public static String formatLocation(Player player) {
		return formatLocation(player.getLocation());
	}
	
	/**
	 * Formatiert eine Location in einen String, den man im Chat anzeigen kann
	 * @param loc Die Location
	 * @return Der speicherbare String
	 */
	public static String formatChatLocation(Location loc, boolean showWorld) {
		StringBuilder builder = new StringBuilder();
		
		//Welt
		if (showWorld) {
			if (loc.getWorld() == null) builder.append("null");
			else builder.append(loc.getWorld().getName());
			builder.append(' ');
		}
		
		//X
		builder.append(String.format(formatThree, loc.getX()));
		builder.append(' ');
		
		//Y
		builder.append(String.format(formatThree, loc.getY()));
		builder.append(' ');
		
		//Z
		builder.append(String.format(formatThree, loc.getZ()));
		builder.append(' ');
		
		//Yaw
		builder.append(String.format(formatOne, loc.getYaw()));
		builder.append(' ');
		
		//Pitch
		builder.append(String.format(formatOne, loc.getPitch()));
		
		return builder.toString();
	}
	
	/**
	 * Formatiert eine Location in einen String, den man im Chat anzeigen kann
	 * @param player Der Spieler, dessen Location formatiert werden soll
	 * @return Der speicherbare String
	 */
	public static String formatChatLocation(Player player) {
		return formatChatLocation(player.getLocation(), true);
	}
	
	/**
	 * Formatiert einen Spielernamen mit Prefix und Suffix des gegebenen Ranges
	 * @param player Der Spieler
	 * @param rank Der zu nutzende Rang
	 * @return Der formatierte String
	 */
	public static String formatPlayerName(Player player, Rank rank) {
		return rank.getPrefix() + player.getDisplayName() + rank.getSuffix();
	}
	
	/**
	 * Formatiert einen Spielernamen mit Prefix und Suffix des gegebenen Ranges
	 * @param uuid Die UUID des Spielers
	 * @param rank Der zu nutzende Rang
	 * @return Der formatierte String
	 */
	public static String formatPlayerName(UUID uuid, Rank rank) {
		return rank.getPrefix() + Bukkit.getServer().getOfflinePlayer(uuid).getName() + rank.getSuffix();
	}
}
