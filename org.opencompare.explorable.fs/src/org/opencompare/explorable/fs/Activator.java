package org.opencompare.explorable.fs;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import org.opencompare.explorable.ApplicationConfiguration;
import org.opencompare.explorable.OptionDefinition;
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

    private final Logger log = Logger.getLogger(Activator.class.getName());

    public void start(BundleContext bundleContext) throws Exception {
		if (log.isLoggable(Level.FINE)) log.fine("Initializing filesystem factories: START");
		
		FileFactory fileFactory = new FileFactory();
		ApplicationConfiguration appConfig = ApplicationConfiguration.getInstance();
		
		appConfig.registerExplorableFactory(SimpleFile.class.getSimpleName(), fileFactory);
		appConfig.registerExplorableFactory(Folder.class.getSimpleName(), fileFactory);
		appConfig.registerExplorableFactory(ZipFile.class.getSimpleName(), fileFactory);

		appConfig.registerExplorer(SimpleFile.class.getSimpleName(), new NoExplorer());
		appConfig.registerExplorer(Folder.class.getSimpleName(), new FolderExplorer());
		appConfig.registerExplorer(ZipFile.class.getSimpleName(), new ZipFileExplorer());
		
		appConfig.registerExplorer(Root.class.getSimpleName(), new FolderExplorer());

		appConfig.addOptionDefinition(
				OptionDefinition.newTextOption(
						FolderExplorer.OPTION_ROOT, 
						"Root folder", 
						"",
						true
					)
			);
		
		appConfig.addOptionDefinition(
				OptionDefinition.newYesNoOption(
						FileFactory.OPTION_ZIP, 
						"Explore ZIP files", 
						true,
						false
					)
			);

		if (log.isLoggable(Level.FINE)) log.fine("Initializing filesystem factories: FINISH");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		if (log.isLoggable(Level.FINE)) log.fine("Unloading filesystem factories");
		
		// TODO: Unregister
	}

}
