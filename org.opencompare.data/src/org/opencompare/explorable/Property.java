package org.opencompare.explorable;

import org.opencompare.explore.ExplorationException;

public class Property extends Explorable {

	private final String name;
	private final String value;
	private final long valueHashCode;

	Property(int id, int parentId, String name, String value, long valueHashCode, String sha) throws ExplorationException {
		super(id, parentId, sha);
		this.name = name;
		this.value = value;
		this.valueHashCode = valueHashCode;
	}

	Property(int id, int parentId, String name, String value) throws ExplorationException {
		this(id, parentId, name, value, crc32(value), null);
	}

	@Override
	public String getRelativeId() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Property [name=" + name + ", value=" + value + "]";
	}

	@Override
	public long getValueHashCode() {
		return valueHashCode;
	}

	@Override
	public String getUserFriendlyValue() {
		return getValue();
	}
}
