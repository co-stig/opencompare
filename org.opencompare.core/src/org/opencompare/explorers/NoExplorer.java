package org.opencompare.explorers;

import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ProcessConfiguration;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExploringThread;

public class NoExplorer implements Explorer {

    @Override
	public void explore(ProcessConfiguration config, ExploringThread thread, Explorable what) throws ExplorationException {
    	// Do nothing
	}

}
