package org.opencompare.explorable;


public class Conflict extends Explorable {

    private final Explorable reference;
    private final Explorable actual;
    private ConflictType type;
    private String comment;

    Conflict(int id, int parentId, Explorable reference, Explorable actual, ConflictType type, String comment) {
        super(id, parentId, reference == null ? (actual == null ? null : actual.getSha()) : reference.getSha());
        this.reference = reference;
        this.actual = actual;
        this.type = type;
        this.comment = comment;
    }

    @Override
    public String getValue() {
        return getType().toString();
    }

    @Override
    public String getRelativeId() {
        return getActualOrReference().getRelativeId();
    }

    public Explorable getReference() {
        return reference;
    }

    public Explorable getActual() {
        return actual;
    }

    public Explorable getActualOrReference() {
        return actual == null ? reference : actual;
    }

    public ConflictType getType() {
        return type;
    }

    public void setType(ConflictType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + " " + getActualOrReference().getClass().getSimpleName() + " \"" + getRelativeId() + "\"";
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

	@Override
	public long getValueHashCode() {
		return getType().ordinal();
	}

    @Override
    public String getUserFriendlyValue() {
        return "[Conflict]";
    }
        
}