package io.github.mh321productions.serverapi.util;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class StringReader {
	
	/**
	 * Dekodiert einen Zeit-String in ein Zeitobjekt
	 * @param time Der formatierte String (siehe {@link StringFormatter#formatDateTimeSave(ZonedDateTime)})
	 * @return Der Zeitpunkt oder null bei einem Fehler
	 */
	public static ZonedDateTime readDateTime(String time) {
		String[] comp = time.split("\\.");
		
		if (comp.length != 7) return null;
		
		
		try {
			return ZonedDateTime.of(
					Integer.parseInt(comp[2]), 
					Integer.parseInt(comp[1]), 
					Integer.parseInt(comp[0]), 
					Integer.parseInt(comp[3]), 
					Integer.parseInt(comp[4]), 
					Integer.parseInt(comp[5]), 
					Integer.parseInt(comp[6]),
					ZoneId.systemDefault()
			);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} catch (DateTimeException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Dekodiert einen Location-String in eine {@link Location}.
	 * @param loc Der formatierte String (siehe {@link StringFormatter#formatLocation(Location)})
	 * @return Die Location oder null bei einem Fehler
	 */
	public static Location readLocation(String loc) {
		String[] comp = loc.split(",");
		if (comp.length == 6) {
			try {
				return new Location(
						Bukkit.getWorld(comp[0]), 
						Double.parseDouble(comp[1]), 
						Double.parseDouble(comp[2]), 
						Double.parseDouble(comp[3]), 
						Float.parseFloat(comp[4]), 
						Float.parseFloat(comp[5])
				);
			} catch (NumberFormatException e) {
				return null;
			}
		} else if (comp.length == 4) {
			try {
				return new Location(
						Bukkit.getWorld(comp[0]), 
						Double.parseDouble(comp[1]), 
						Double.parseDouble(comp[2]), 
						Double.parseDouble(comp[3]), 
						0,
						0
				);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		
		return null;
	}
}
