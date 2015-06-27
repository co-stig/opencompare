package org.opencompare.explorers.files;

import java.io.File;

import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ProcessConfiguration;
import org.opencompare.explorable.Root;
import org.opencompare.explorable.files.Folder;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExploringThread;
import org.opencompare.explorers.Explorer;
import org.opencompare.explorers.Explores;

@Explores(Folder.class)
public class FolderExplorer implements Explorer {

	public final static String OPTION_ROOT = FolderExplorer.class.getName() + "/root.folder";
	
    @Override
	public void explore(ProcessConfiguration config, ExploringThread thread, Explorable parent) throws ExplorationException {
		if (parent instanceof Root) {
			try {
				thread.enqueue(
						parent, 
						"Folder", 
						new File(config.getOption(OPTION_ROOT).getStringValue())
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

}
