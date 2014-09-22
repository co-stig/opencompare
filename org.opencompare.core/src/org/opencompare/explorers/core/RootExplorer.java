package org.opencompare.explorers.core;

import org.opencompare.database.Database;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.Root;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;
import org.opencompare.explorers.Explores;

@Explores(Root.class)
public class RootExplorer implements Explorer {

    @Override
	public void explore(Database threadDatabase, Explorable what) throws ExplorationException {
    	Root root = (Root) what;
    	
    	try {
			Configuration.enqueue(
					threadDatabase,
					root, 
					"Folder", 
					root.getPath()
				);
		} catch (Throwable t) {
			throw new ExplorationException("Unable to explore the root: " + root.getPath(), t);
		}
	}

}
