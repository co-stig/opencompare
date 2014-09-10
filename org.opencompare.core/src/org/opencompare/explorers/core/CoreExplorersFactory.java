package org.opencompare.explorers.core;

import java.util.HashMap;
import java.util.Map;

import org.opencompare.explorable.Conflict;
import org.opencompare.explorable.Description;
import org.opencompare.explorable.Property;
import org.opencompare.explorable.Root;
import org.opencompare.explorable.ThreadControllExplorable;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;
import org.opencompare.explorers.ExplorerFactory;
import org.opencompare.explorers.NoExplorer;

public class CoreExplorersFactory implements ExplorerFactory {

	private final Map<String, Explorer> explorers = new HashMap<String, Explorer>();
	
	public CoreExplorersFactory() {
		NoExplorer noExplorer = new NoExplorer();
		
		explorers.put(Root.class.getSimpleName(), new RootExplorer());
		explorers.put(Property.class.getSimpleName(), noExplorer);
		explorers.put(Conflict.class.getSimpleName(), noExplorer);
		explorers.put(Description.class.getSimpleName(), noExplorer);
		explorers.put(ThreadControllExplorable.class.getSimpleName(), noExplorer);
	}
	
	@Override
	public Explorer getExplorer(String parent) throws ExplorationException {
		return explorers.get(parent);
	}
	
}
