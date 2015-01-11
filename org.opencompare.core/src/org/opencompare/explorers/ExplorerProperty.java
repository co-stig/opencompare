package org.opencompare.explorers;

import java.util.Arrays;

public final class ExplorerProperty {

	public enum PropertyType { text, list, bool, number }
	
	public static final int UNLIMITED = -1;

	private final String name;
	private final String displayName;	// TODO: I18N
	private final PropertyType type;
	private final Object min;		// For number -- min. value, type Number; for text -- min. length (inclusive), type Integer
	private final Object max;   	// For number -- max. value, type Number; for text -- max. length (inclusive), type Integer
	private final Object def;		// Default value, for number of type Number; for text and list -- of type String; for bool -- of type Boolean
	private final String[] values;	// Possible values for type list
	
	private ExplorerProperty(String name, String displayName, PropertyType type, Object min, Object max, Object def, String[] values) {
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.min = min;
		this.max = max;
		this.def = def;
		this.values = values;
	}
	
	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public PropertyType getType() {
		return type;
	}

	public Number getMinValue() {
		return (Number) min;
	}

	public Number getMaxValue() {
		return (Number) max;
	}
	
	public int getMinLength() {
		return (Integer) min;
	}
	
	public int getMaxLength() {
		return (Integer) max;
	}

	public Object getDefaultValue() {
		return def;
	}

	public String[] getPossibleValues() {
		return values;
	}

	public static ExplorerProperty newTextProperty(String name, String displayName, String defValue, int minLength, int maxLength) {
		return new ExplorerProperty(name, displayName, PropertyType.text, minLength, maxLength, defValue, null);
	}
	
	public static ExplorerProperty newTextProperty(String name, String displayName, String defValue) {
		return newTextProperty(name, displayName, defValue, UNLIMITED, UNLIMITED);
	}
	
	public static ExplorerProperty newTextProperty(String name, String displayName) {
		return newTextProperty(name, displayName, "");
	}
	
	public static ExplorerProperty newNumberProperty(String name, String displayName, Number defValue, Number minValue, Number maxValue) {
		return new ExplorerProperty(name, displayName, PropertyType.number, minValue, maxValue, defValue, null);
	}
	
	public static ExplorerProperty newNumberProperty(String name, String displayName, Number defValue) {
		return newNumberProperty(name, displayName, defValue, null, null);
	}
	
	public static ExplorerProperty newNumberProperty(String name, String displayName) {
		return newNumberProperty(name, displayName, 0);
	}
	
	public static ExplorerProperty newBooleanProperty(String name, String displayName, Boolean defValue) {
		return new ExplorerProperty(name, displayName, PropertyType.bool, null, null, defValue, null);
	}
	
	public static ExplorerProperty newBooleanProperty(String name, String displayName) {
		return newBooleanProperty(name, displayName, false);
	}
	
	public static ExplorerProperty newListProperty(String name, String displayName, String defValue, String[] values) {
		return new ExplorerProperty(name, displayName, PropertyType.text, null, null, defValue, values);
	}
	
	public static ExplorerProperty newListProperty(String name, String displayName, String[] values) {
		return newListProperty(name, displayName, values[0], values);
	}

	@Override
	public String toString() {
		return "ExplorerProperty [name=" + name + 
				", displayName=" + displayName + 
				", type=" + type + 
				", min=" + min + 
				", max=" + max + 
				", def=" + def + 
				", values=" + Arrays.toString(values) + 
			"]";
	}

}
