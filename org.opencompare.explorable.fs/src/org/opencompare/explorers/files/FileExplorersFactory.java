package org.opencompare.explorers.files;

import java.util.HashMap;
import java.util.Map;

import org.opencompare.explorable.files.Folder;
import org.opencompare.explorable.files.PropertiesFile;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explorable.files.XConfFile;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;
import org.opencompare.explorers.ExplorerFactory;
import org.opencompare.explorers.NoExplorer;

public class FileExplorersFactory implements ExplorerFactory {

	private final Map<String, Explorer> explorers = new HashMap<String, Explorer>();
	
	public FileExplorersFactory() {
		explorers.put(XConfFile.class.getSimpleName(), new XConfFileExplorer());
		explorers.put(Folder.class.getSimpleName(), new FolderExplorer());
		explorers.put(PropertiesFile.class.getSimpleName(), new PropertiesFileExplorer());
		explorers.put(SimpleFile.class.getSimpleName(), new NoExplorer());
	}
	
	@Override
	public Explorer getExplorer(String parent) throws ExplorationException {
		return explorers.get(parent);
	}
	
}
