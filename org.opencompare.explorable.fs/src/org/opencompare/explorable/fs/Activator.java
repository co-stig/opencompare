package org.opencompare.explorable.fs;

import java.util.zip.ZipFile;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Root;
import org.opencompare.explorable.files.FileFactory;
import org.opencompare.explorable.files.Folder;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explorers.NoExplorer;
import org.opencompare.explorers.files.FolderExplorer;
import org.opencompare.explorers.files.ZipFileExplorer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Initializing filesystem factories: START");
		
		FileFactory fileFactory = new FileFactory();
		
		Configuration.registerExplorableFactory(SimpleFile.class.getSimpleName(), fileFactory);
		Configuration.registerExplorableFactory(Folder.class.getSimpleName(), fileFactory);
		Configuration.registerExplorableFactory(ZipFile.class.getSimpleName(), fileFactory);

		Configuration.registerExplorer(SimpleFile.class.getSimpleName(), new NoExplorer());
		Configuration.registerExplorer(Folder.class.getSimpleName(), new FolderExplorer());
		Configuration.registerExplorer(ZipFile.class.getSimpleName(), new ZipFileExplorer());
		
		Configuration.registerExplorer(Root.class.getSimpleName(), new FolderExplorer());
		
		System.out.println("Initializing filesystem factories: FINISH");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Unloading filesystem factories");
		
		// TODO: Unregister
	}

}
