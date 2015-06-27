package org.opencompare.explorable;



public class OptionValue {

	private static final String UNDEFINED_LIST_VALUE = " # Undefined #";
	private final OptionDefinition definition;
	private Object value;

	public OptionValue(OptionDefinition definition, Object value) {
		this.definition = definition;
		this.value = value;
	}

	public OptionValue(OptionDefinition definition) {
		this.definition = definition;
		this.value = definition.getDefaultValue();
	}
	
	public void reset() {
		this.value = definition.getDefaultValue();
	}

	public Object getValue() {
		return value;
	}

	public boolean getBooleanValue() {
		return (Boolean) value;
	}
	
	public int getIntValue() {
		return (Integer) value;
	}
	
	public String getStringValue() {
		return (String) value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public OptionDefinition getDefinition() {
		return definition;
	}

	@Override
	public String toString() {
		return "OptionValue [definition=" + definition + ", value=" + value + "]";
	}
	
	public boolean isFilled() {
		switch (definition.getType()) {
			case List: 
				return value != null && !((String) value).isEmpty() && !((String) value).equals(UNDEFINED_LIST_VALUE);
			case Text: 
				return value != null && !((String) value).isEmpty();
			default: 
				return value != null;
		}
	}
}
