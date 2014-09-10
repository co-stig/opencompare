package org.opencompare.explorable.fs;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.files.FileFactory;
import org.opencompare.explorable.files.Folder;
import org.opencompare.explorable.files.PropertiesFile;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explorable.files.XConfFile;
import org.opencompare.explorers.files.FileExplorersFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Initializing filesystem factories: START");
		
		FileFactory fileFactory = new FileFactory();
		
		Configuration.registerExplorableFactory(SimpleFile.class.getSimpleName(), fileFactory);
		Configuration.registerExplorableFactory(Folder.class.getSimpleName(), fileFactory);
		Configuration.registerExplorableFactory(PropertiesFile.class.getSimpleName(), fileFactory);
		Configuration.registerExplorableFactory(XConfFile.class.getSimpleName(), fileFactory);
		
		FileExplorersFactory fileExplorersFactory = new FileExplorersFactory();
		
		Configuration.registerExplorerFactory(SimpleFile.class.getSimpleName(), fileExplorersFactory);
		Configuration.registerExplorerFactory(PropertiesFile.class.getSimpleName(), fileExplorersFactory);
		Configuration.registerExplorerFactory(XConfFile.class.getSimpleName(), fileExplorersFactory);
		Configuration.registerExplorerFactory(Folder.class.getSimpleName(), fileExplorersFactory);
		
		System.out.println("Initializing filesystem factories: FINISH");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Unloading filesystem factories");
	}

}
