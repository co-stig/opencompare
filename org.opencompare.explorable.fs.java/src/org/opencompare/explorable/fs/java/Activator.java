package org.opencompare.explorable.fs.java;

import org.opencompare.explorable.ApplicationConfiguration;
import org.opencompare.explorable.OptionDefinition;
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
		ApplicationConfiguration appConfig = ApplicationConfiguration.getInstance();
		
		appConfig.registerExplorableFactory(PropertiesFile.class.getSimpleName(), fileFactory);
		appConfig.registerExplorableFactory(XConfFile.class.getSimpleName(), fileFactory);
		appConfig.registerExplorableFactory(SimpleFile.class.getSimpleName(), fileFactory);
		
		appConfig.registerExplorer(PropertiesFile.class.getSimpleName(), new PropertiesFileExplorer());
		appConfig.registerExplorer(XConfFile.class.getSimpleName(), new XConfFileExplorer());
		appConfig.registerExplorer(SimpleFile.class.getSimpleName(), new ClassFileExplorer());

		appConfig.addOptionDefinition(
				OptionDefinition.newYesNoOption(
						ClassFileExplorer.OPTION_EXPLORE_FIELDS, 
						"Explore fields", 
						true,
						false
					)
			);

		appConfig.addOptionDefinition(
				OptionDefinition.newYesNoOption(
						ClassFileExplorer.OPTION_EXPLORE_METHODS, 
						"Explore methods", 
						true,
						false
					)
			);

		System.out.println("Initializing Java filesystem factories: FINISH");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Unloading Java filesystem factories");
		
		// TODO: Unregister
	}

}
