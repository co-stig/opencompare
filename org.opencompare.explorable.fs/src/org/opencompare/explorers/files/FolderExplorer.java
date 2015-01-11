package org.opencompare.explorers.files;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.Root;
import org.opencompare.explorable.files.Folder;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExploringThread;
import org.opencompare.explorers.Explorer;
import org.opencompare.explorers.ExplorerProperty;
import org.opencompare.explorers.Explores;

@Explores(Folder.class)
public class FolderExplorer implements Explorer {

    @Override
	public void explore(ExploringThread thread, Explorable parent) throws ExplorationException {
		if (parent instanceof Root) {
			try {
				thread.enqueue(
						parent, 
						"Folder", 
						new File(Configuration.getProperty("root.folder"))
					);
			} catch (InterruptedException e) {
				throw new ExplorationException("Unable to enqueue root folder", e);
			}
		} else {
			try {
    			Folder folder = (Folder) parent;
				File[] files = folder.getPath().listFiles();
				if (files != null && files.length > 0) { // Yes, it can be null under Windows for virtual folders like "My Music"
					for (File child : files) {
						thread.enqueue(
								folder, 
								child.isDirectory() ? Folder.class.getSimpleName() : SimpleFile.class.getSimpleName(), 
								child
							);
					}
				}
    		} catch (Throwable t) {
    			throw new ExplorationException("Unable to explore the content of the folder '" + parent + "'", t);
    		}
		}
    }

	@Override
	@SuppressWarnings("unchecked")
	public Collection<ExplorerProperty> getProperties() {
		return Collections.EMPTY_LIST;
	}

}
