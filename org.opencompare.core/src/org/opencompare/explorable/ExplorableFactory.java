package org.opencompare.explorable;

import org.opencompare.explore.ExplorationException;

public interface ExplorableFactory {

	/**
	 * This method is called only when we read objects from the database to
	 * display it to the user, so we can't get full file paths for example
	 * (persisted snapshot is detached from the actual System instance).
	 * 
	 * Note: If the factory can't handle this particular type of the explorable,
	 * it can return null. In this case the control will be passed forward to
	 * the next (less specific) factory.
	 */
	Explorable parseExplorable(String type, int id, int parentId, String relativeId, String value, long hash, String sha) throws ExplorationException;

	/**
	 * This method is called only when we construct objects during the
	 * exploration phase, so that we may have access to some additional
	 * information.
	 * 
	 * Note: If the factory can't handle this particular type of the explorable,
	 * it can return null. In this case the control will be passed forward to
	 * the next (less specific) factory.
	 */
	Explorable createExplorable(Explorable origin, String type, Object... params) throws ExplorationException;
	
}
