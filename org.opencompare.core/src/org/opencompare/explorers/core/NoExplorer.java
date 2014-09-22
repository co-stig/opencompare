package org.opencompare.explorers.core;

import org.opencompare.database.Database;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;

public class NoExplorer implements Explorer {

    @Override
	public void explore(Database threadDatabase, Explorable what) throws ExplorationException {
    	// Do nothing
	}

}
