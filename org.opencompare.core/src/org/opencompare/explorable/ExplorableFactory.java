package org.opencompare.explorable;

import org.opencompare.explore.ExplorationException;

public interface ExplorableFactory {

	/**
	 * This method is called only when we read objects from the database to
	 * display it to the user, so we can't get full file paths for example
	 * (persisted snapshot is detached from the actual System instance).
	 */
	Explorable newExplorable(String type, int id, int parentId, String relativeId, String value, long hash, String sha) throws ExplorationException;

	/**
	 * This method is called only when we construct objects during the
	 * exploration phase, so that we may have access to some additional
	 * information.
	 */
	Explorable newExplorable(Explorable origin, String type, Object... params) throws ExplorationException;
	
}
