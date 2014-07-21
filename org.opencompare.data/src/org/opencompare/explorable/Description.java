package org.opencompare.explorable;


public class Description extends Explorable {

	private final String value;
	private final String relativeId;
	
	Description(int id, int parentId, String relativeId, String value, String sha) {
		super(id, parentId, sha);
		this.value = value;
		this.relativeId = relativeId;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public long getValueHashCode() {
		return crc32(value);
	}

	@Override
	public String getRelativeId() {
		return relativeId;
	}

	@Override
	public String getUserFriendlyValue() {
		return value;
	}

}
