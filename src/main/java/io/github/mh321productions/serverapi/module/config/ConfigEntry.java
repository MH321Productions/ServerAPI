package io.github.mh321productions.serverapi.module.config;

import org.bukkit.Material;

/**
 * Wrapper-Klasse für einen Config-Eintrag. Dieser besteht aus einem <br/>
 * <b>einzigartigem</b> Namen und einem Wert, der einen der folgenden <br/>
 * {@link EntryType Typen} hat.
 * 
 * @author 321Productions
 *
 */
public class ConfigEntry {

	public enum EntryType {
		Long(0),
		Double(1),
		Text(2),
		Material(3);
		
		int index;
		private EntryType(int index) {
			this.index = index;
		}
	}
	
	private String name;
	private EntryType type;
	private Object value;
	
	/**
	 * Initialisiert den Eintrag mit einem long Wert
	 * @param name Der Name des Eintrags
	 * @param value Der Wert
	 */
	public ConfigEntry(String name, long value) {
		this.name = name;
		type = EntryType.Long;
		this.value = value;
	}
	
	/**
	 * Initialisiert den Eintrag mit einem long Wert
	 * @param name Der Name des Eintrags
	 * @param value Der Wert
	 */
	public ConfigEntry(String name, double value) {
		this.name = name;
		type = EntryType.Double;
		this.value = value;
	}
	
	/**
	 * Initialisiert den Eintrag mit einem long Wert
	 * @param name Der Name des Eintrags
	 * @param value Der Wert
	 */
	public ConfigEntry(String name, String value) {
		this.name = name;
		type = EntryType.Text;
		this.value = value;
	}
	
	/**
	 * Initialisiert den Eintrag mit einem long Wert
	 * @param name Der Name des Eintrags
	 * @param value Der Wert
	 */
	public ConfigEntry(String name, Material value) {
		this.name = name;
		type = EntryType.Long;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public EntryType getType() {
		return type;
	}
	
	/**
	 * Fragt den gespeicherten Wert als Long ab
	 * @return Der gespeicherte Wert
	 * @throws IllegalStateException Wenn der Eintrag nicht vom Typ {@link EntryType#Long} ist
	 */
	public long getValueAsLong() throws IllegalStateException {
		if (type == EntryType.Long) return (long) value;
		
		throw new IllegalStateException("The entry type isn't Long (it is " + type + ")");
	}
	
	/**
	 * Fragt den gespeicherten Wert als Double ab
	 * @return Der gespeicherte Wert
	 * @throws IllegalStateException Wenn der Eintrag nicht vom Typ {@link EntryType#Double} ist
	 */
	public double getValueAsDouble() throws IllegalStateException {
		if (type == EntryType.Double) return (double) value;
		
		throw new IllegalStateException("The entry type isn't Double (it is " + type + ")");
	}
	
	/**
	 * Fragt den gespeicherten Wert als String ab
	 * @return Der gespeicherte Wert
	 * @throws IllegalStateException Wenn der Eintrag nicht vom Typ {@link EntryType#Text} ist
	 */
	public String getValueAsString() throws IllegalStateException {
		if (type == EntryType.Text) return (String) value;
		
		throw new IllegalStateException("The entry type isn't Text (it is " + type + ")");
	}
	
	/**
	 * Fragt den gespeicherten Wert als {@link Material} ab
	 * @return Der gespeicherte Wert
	 * @throws IllegalStateException Wenn der Eintrag nicht vom Typ {@link EntryType#Material} ist
	 */
	public Material getValueAsMaterial() throws IllegalStateException {
		if (type == EntryType.Material) return (Material) value;
		
		throw new IllegalStateException("The entry type isn't Material (it is " + type + ")");
	}
	
	/**
	 * Überschreibt den gespeicherten Wert
	 * @param value Der neue long-Wert
	 * @throws IllegalStateException
	 */
	public void setLongValue(long value) throws IllegalStateException {
		if (type == EntryType.Long) this.value = value;
		else throw new IllegalStateException("The entry type isn't Long (it is " + type + ")");
	}
	
	/**
	 * Überschreibt den gespeicherten Wert
	 * @param value Der neue double-Wert
	 * @throws IllegalStateException
	 */
	public void setDoubleValue(double value) throws IllegalStateException {
		if (type == EntryType.Double) this.value = value;
		else throw new IllegalStateException("The entry type isn't Double (it is " + type + ")");
	}
	
	/**
	 * Überschreibt den gespeicherten Wert
	 * @param value Der neue String-Wert
	 * @throws IllegalStateException
	 */
	public void setStringValue(String value) throws IllegalStateException {
		if (type == EntryType.Text) this.value = value;
		else throw new IllegalStateException("The entry type isn't Text (it is " + type + ")");
	}
	
	/**
	 * Überschreibt den gespeicherten Wert
	 * @param value Der neue {@link Material}-Wert
	 * @throws IllegalStateException
	 */
	public void setMaterialValue(Material value) throws IllegalStateException {
		if (type == EntryType.Material) this.value = value;
		else throw new IllegalStateException("The entry type isn't Material (it is " + type + ")");
	}
}
