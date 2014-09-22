package org.opencompare.explorers.files;

import java.io.File;

import org.opencompare.database.Database;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.files.Folder;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;
import org.opencompare.explorers.Explores;

@Explores(Folder.class)
public class FolderExplorer implements Explorer {

    @Override
	public void explore(Database threadDatabase, Explorable folder) throws ExplorationException {
    	try {
			File[] files = ((Folder) folder).getPath().listFiles();
			if (files != null && files.length > 0) { // Yes, it can be null under Windows for virtual folders like "My Music"
				for (File child : files) {
					Configuration.enqueue(
							threadDatabase,
							folder, 
							child.isDirectory() ? Folder.class.getSimpleName() : SimpleFile.class.getSimpleName(), 
							child
						);
				}
			}
		} catch (Throwable t) {
			throw new ExplorationException("Unable to explore the content of the folder '" + folder + "'", t);
		}
    }

}
