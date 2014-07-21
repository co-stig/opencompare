package org.opencompare.explorers;

import org.opencompare.explore.ExplorationException;

public interface ExplorerFactory {

	Explorer getExplorer(String parent) throws ExplorationException;

}
