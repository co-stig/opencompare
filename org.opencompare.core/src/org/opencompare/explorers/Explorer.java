package org.opencompare.explorers;

import org.opencompare.database.Database;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;

public interface Explorer {
	
	/**
	 * Get contents of this object. This method will not be called often, so
	 * there is no need to cache its results in subclasses.
	 * 
	 * @throws ExplorationException
	 * @throws InterruptedException
	 */
	public abstract void explore(Database threadDatabase, Explorable what) throws ExplorationException, InterruptedException;

}
