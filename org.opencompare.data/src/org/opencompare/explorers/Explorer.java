package org.opencompare.explorers;

import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ExplorableFactory;
import org.opencompare.explore.ExplorationException;

public interface Explorer {
	
	// TODO: Because we use static methods in Configuration, we probably don't need factory parameter anymore. Remove one of those two.
	
	/**
	 * Get contents of this object. This method will not be called often, so
	 * there is no need to cache its results in subclasses.
	 * 
	 * @throws ExplorationException
	 * @throws InterruptedException 
	 */
	public abstract void explore(Explorable what, ExplorableFactory factory) throws ExplorationException, InterruptedException;

}
