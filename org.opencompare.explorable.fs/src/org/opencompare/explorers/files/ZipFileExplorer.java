package org.opencompare.explorers.files;

import java.io.File;
import java.util.Random;

import org.opencompare.core.FolderDisposer;
import org.opencompare.core.ZipUtility;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ProcessConfiguration;
import org.opencompare.explorable.files.Folder;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explorable.files.ZipFile;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExploringThread;
import org.opencompare.explorers.Explorer;
import org.opencompare.explorers.Explores;

@Explores(ZipFile.class)
public class ZipFileExplorer implements Explorer {

	private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
	private static final String TEMP_DIR_FORMAT = "opencompare." + "%s" + File.separator + "%s.%d.%d";
	private static final Random RAND = new Random();
	
    @Override
	public void explore(ProcessConfiguration config, ExploringThread thread, Explorable parent) throws ExplorationException {
		try {
			ZipFile zip = (ZipFile) parent;
			
			File zipFile = zip.getPath();
			File tempFolder = getTempFolder(zipFile.getName());
			tempFolder.mkdirs();
			
			System.out.print("Unzipping " + zipFile + " to " + tempFolder + "... ");
			ZipUtility.unzip(zipFile, tempFolder, null);
			config.closeOnFinish(new FolderDisposer(tempFolder));
			System.out.println("Done.");
			
			File[] files = tempFolder.listFiles();
			if (files != null && files.length > 0) { // Yes, it can be null under Windows for virtual folders like "My Music"
				for (File child : files) {
					thread.enqueue(
							zip, 
							child.isDirectory() ? Folder.class.getSimpleName() : SimpleFile.class.getSimpleName(), 
							child
						);
				}
			}
		} catch (Throwable t) {
			throw new ExplorationException("Unable to explore the content of the folder '" + parent + "'", t);
		}
    }

    // Already synchronized
	private File getTempFolder(String zipFilename) {
		return new File(
				String.format(
						TEMP_DIR_FORMAT, 
						TEMP_DIR, 
						zipFilename, 
						System.nanoTime(), 
						RAND.nextInt()
					)
			);
	}

}
