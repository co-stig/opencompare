package org.opencompare.explorable;

import java.io.File;
import java.util.Arrays;

import org.opencompare.explore.ExplorationException;

public class RootFactory implements ExplorableFactory {

	public static final String TYPE_ROOT = Root.class.getSimpleName();
	public static final String TYPE_PROPERTY = Property.class.getSimpleName();
	public static final String TYPE_CONFLICT = Conflict.class.getSimpleName();
	
	private Root newRoot(File path, String sha) throws ExplorationException {
		return new Root(path, sha);
	}

	/**
	 * Creates new property object with generated ID and calculated checksum.
	 * Used in the exploration phase.
	 */
	private Property newProperty(int parentId, String name, String value) throws ExplorationException {
		return new Property(Configuration.getSharedDatabase().nextId(), parentId, name == null ? "" : name, value == null ? "" : value);
	}

	/**
	 * Creates new file object with given ID and checksum (value hash code).
	 * Used for parsing the database in comparison and display phases.
	 * 
	 * @param name
	 *            Can be null, then treated as empty string
	 * @param value
	 *            Can be null, then treated as empty string
	 */
	private Property newProperty(int id, int parentId, String name, String value, long valueHashCode, String sha) throws ExplorationException {
		return new Property(id, parentId, name == null ? "" : name, value == null ? "" : value, valueHashCode, sha);
	}

	/**
	 * Creates new conflict object with generated ID. Used in the comparison
	 * phase.
	 */
	private Conflict newConflict(int parentId, Explorable reference, Explorable actual, ConflictType type, String comment) throws ExplorationException {
		return newConflict(Configuration.getSharedDatabase().nextId(), parentId, reference, actual, type, comment);
	}

	/**
	 * Creates new conflict object with given ID. Used in the display phase.
	 * 
	 * @param parent
	 *            Can be null
	 * @param reference
	 *            Can be null (then actual is not null)
	 * @param actual
	 *            Can be null (then reference is not null)
	 * @param type
	 *            Not null
	 * @param comment
	 *            Can be null
	 */
	private Conflict newConflict(int id, int parentId, Explorable reference, Explorable actual, ConflictType type, String comment) throws ExplorationException {
		return new Conflict(id, parentId, reference, actual, type, comment);
	}


	/**
	 * This method is called only when we read objects from the database to
	 * display it to the user, so we can't get full file paths for example
	 * (persisted snapshot is detached from the actual System instance).
	 */
	@Override
	public Explorable newExplorable(String type, int id, int parentId, String relativeId, String value, long hash, String sha) throws ExplorationException {
		System.out.println("org.opencompare.explorable.RootFactory.newExplorable1(" + type + ", " + id + ", " + parentId + ", " + relativeId + ", " + value + ", " + hash + ", " + sha + ")");
		if (type.equals(TYPE_PROPERTY)) {
			return newProperty(id, parentId, relativeId, value, hash, sha);
		} else if (type.equals(TYPE_ROOT)) {
			return newRoot(null, sha);
		} else {
			throw new ExplorationException("Unsupported type: " + type);
		}
	}

	@Override
	public Explorable newExplorable(Explorable origin, String type, Object... params) throws ExplorationException {
		System.out.println("org.opencompare.explorable.RootFactory.newExplorable2(" + origin + ", " + type + ", " + Arrays.toString(params) + ")");
		if (type.equals(TYPE_PROPERTY)) {
			return newProperty(origin.getId(), (String) params[0], (String) params[1]);
		} else if (type.equals(TYPE_ROOT)) {
			return newRoot((File) params[0], (String) params[1]);
		} else if (type.equals(TYPE_CONFLICT)) {
			return newConflict(origin == null ? 1 : origin.getId(), (Explorable) params[0], (Explorable) params[1], (ConflictType) params[2], (String) params[3]);
		} else {
			throw new ExplorationException("Unsupported type: " + type);
		}
	}
	
}
