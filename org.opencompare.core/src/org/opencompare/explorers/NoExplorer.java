package org.opencompare.explorers;

import java.util.Collection;
import java.util.Collections;

import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExploringThread;

public class NoExplorer implements Explorer {

    @Override
	public void explore(ExploringThread thread, Explorable what) throws ExplorationException {
    	// Do nothing
	}
    
	@Override
	@SuppressWarnings("unchecked")
	public Collection<ExplorerProperty> getProperties() {
		return Collections.EMPTY_LIST;
	}

}
