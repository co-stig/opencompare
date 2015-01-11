package org.opencompare.explorable;

import java.util.Arrays;

import org.opencompare.explore.ExplorationException;

@Creates({Root.class, Property.class, ThreadControllExplorable.class, Conflict.class, Description.class})
public class RootFactory implements ExplorableFactory {

	public static final String TYPE_ROOT = Root.class.getSimpleName();
	public static final String TYPE_PROPERTY = Property.class.getSimpleName();
	public static final String TYPE_CONFLICT = Conflict.class.getSimpleName();
	
	/**
	 * This method is called only when we read objects from the database to
	 * display it to the user, so we can't get full file paths for example
	 * (persisted snapshot is detached from the actual System instance).
	 */
	@Override
	public Explorable parseExplorable(String type, int id, int parentId, String relativeId, String value, long hash, String sha) throws ExplorationException {
		System.out.println("org.opencompare.explorable.RootFactory.newExplorable1(" + type + ", " + id + ", " + parentId + ", " + relativeId + ", " + value + ", " + hash + ", " + sha + ")");
		if (type.equals(TYPE_PROPERTY)) {
			return new Property(
					id, 
					parentId, 
					relativeId == null ? "" : relativeId, 
					value == null ? "" : value, 
					hash, 
					sha
				);
		} else if (type.equals(TYPE_ROOT)) {
			return new Root();
		} else {
			throw new ExplorationException("Unsupported type: " + type);
		}
	}

	@Override
	public Explorable createExplorable(Explorable origin, String type, Object... params) throws ExplorationException {
		System.out.println("org.opencompare.explorable.RootFactory.newExplorable2(" + origin + ", " + type + ", " + Arrays.toString(params) + ")");
		if (type.equals(TYPE_PROPERTY)) {
			return new Property(
					Configuration.getSharedDatabase().nextId(),				// int id 
					origin.getId(), 										// int parentId
					(String) params[0] == null ? "" : (String) params[0], 	// String name
					(String) params[1] == null ? "" : (String) params[1]	// String value
				);
		} else if (type.equals(TYPE_CONFLICT)) {
			Integer optionalId = (Integer) params[0];
			return new Conflict(
					optionalId > 0 ? optionalId : Configuration.getSharedDatabase().nextId(),	// int id 
					(Integer) params[1], 														// int parentId
					(Explorable) params[2], 													// Explorable reference
					(Explorable) params[3], 													// Explorable actual
					(ConflictType) params[4], 													// ConflictType type
					(String) params[5]															// String comment
				);
		} else if (type.equals(TYPE_ROOT)) {
			return new Root();
		} else {
			throw new ExplorationException("Unsupported type: " + type);
		}
	}
	
}
