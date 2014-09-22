package org.opencompare.explorable.fs.java;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.files.SimpleFile;
import org.opencompare.explorable.files.java.FileFactory;
import org.opencompare.explorable.files.java.PropertiesFile;
import org.opencompare.explorable.files.java.XConfFile;
import org.opencompare.explorers.files.java.ClassFileExplorer;
import org.opencompare.explorers.files.java.PropertiesFileExplorer;
import org.opencompare.explorers.files.java.XConfFileExplorer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Initializing Java filesystem factories: START");
		
		FileFactory fileFactory = new FileFactory();
		
		Configuration.registerExplorableFactory(SimpleFile.class.getSimpleName(), fileFactory);
		Configuration.registerExplorableFactory(PropertiesFile.class.getSimpleName(), fileFactory);
		Configuration.registerExplorableFactory(XConfFile.class.getSimpleName(), fileFactory);
		
		Configuration.registerExplorer(PropertiesFile.class.getSimpleName(), new PropertiesFileExplorer());
		Configuration.registerExplorer(XConfFile.class.getSimpleName(), new XConfFileExplorer());
		Configuration.registerExplorer(SimpleFile.class.getSimpleName(), new ClassFileExplorer());
		
		System.out.println("Initializing Java filesystem factories: FINISH");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Unloading Java filesystem factories");
	}

}
