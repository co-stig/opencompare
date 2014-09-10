package org.opencompare.explorers;

import org.opencompare.database.Database;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ExplorableFactory;

public class NoExplorer implements Explorer {

    @Override
	public void explore(Database threadDatabase, Explorable what, ExplorableFactory factory) {
    	// Empty
	}

}
