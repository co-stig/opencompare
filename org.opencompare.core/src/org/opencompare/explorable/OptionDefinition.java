package org.opencompare.explorable;

import java.util.Arrays;

/**
 * Immutable option definition. For value see {@link OptionValue}
 * 
 */
public class OptionDefinition implements Comparable<OptionDefinition> {

	public enum OptionType {
		Text, Int, YesNo, List
	}

	private final OptionType type;
	private final String name;
	private final String description;
	private final String[] allowedValues;
	private final Object defaultValue;
	private final boolean required;
	
	private OptionDefinition(OptionType type, String name, String description, String[] allowedValues, Object defaultValue, boolean required) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.allowedValues = allowedValues;
		this.defaultValue = defaultValue;
		this.required = required;
	}

	public static OptionDefinition newTextOption(String name, String description, String defaultValue, boolean required) {
		return new OptionDefinition(OptionType.Text, name, description, null, defaultValue, required);
	}
	
	public static OptionDefinition newIntOption(String name, String description, Integer defaultValue, boolean required) {
		return new OptionDefinition(OptionType.Int, name, description, null, defaultValue, required);
	}
	
	public static OptionDefinition newYesNoOption(String name, String description, Boolean defaultValue, boolean required) {
		return new OptionDefinition(OptionType.YesNo, name, description, null, defaultValue, required);
	}
	
	public static OptionDefinition newListOption(String name, String description, String[] values, int defaultIndex, boolean required) {
		return new OptionDefinition(OptionType.List, name, description, values, values[defaultIndex], required);
	}
	
	public OptionType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String[] getAllowedValues() {
		return allowedValues;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public boolean isRequired() {
		return required;
	}

	@Override
	public String toString() {
		return "OptionDefinition [type=" + type + ", name=" + name + ", description=" + description + ", allowedValues=" + Arrays.toString(allowedValues) + ", defaultValue=" + defaultValue + ", required=" + required + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(allowedValues);
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OptionDefinition other = (OptionDefinition) obj;
		if (!Arrays.equals(allowedValues, other.allowedValues))
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (required != other.required)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	/**
	 * User-friendly sorting conditions, applied in the following order:
	 * <ol>
	 * <li>Required options go first</li>
	 * <li>Grouping by package's internal name</li>
	 * <li>Alphabetic ordering by display name</li>
	 * </ol>
	 */
	@Override
	public int compareTo(OptionDefinition o) {
		// 1. Required options go first
		if (this.isRequired() && !o.isRequired()) {
			return -1;
		} else if (!this.isRequired() && o.isRequired()) {
			return 1;
		}
		
		// 2. Grouping by package's internal name
		int packageCompare = getPackage().compareTo(o.getPackage());
		if (packageCompare != 0) {
			return packageCompare;
		}
		
		// 3. Alphabetic ordering by display name
		return this.description.compareTo(o.description);
	}
	
	// TODO: Handle corner cases, or better introduce a separate class instead of Strings here
	public String getPackage() {
		return name.split("\\/")[0];
	}
}
