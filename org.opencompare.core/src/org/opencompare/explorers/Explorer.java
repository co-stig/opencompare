package org.opencompare.explorers;

import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ProcessConfiguration;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExploringThread;

public interface Explorer {
	
	/**
	 * Get contents of this object. This method will not be called often, so
	 * there is no need to cache its results in subclasses.
	 * 
	 * @throws ExplorationException
	 * @throws InterruptedException
	 */
	void explore(ProcessConfiguration config, ExploringThread thread, Explorable what) throws ExplorationException, InterruptedException;
}
