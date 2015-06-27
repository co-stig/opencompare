package org.opencompare.cmd;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencompare.ExploreApplication;
import org.opencompare.Snapshot;
import org.opencompare.WithProgress;
import org.opencompare.explorable.ProcessConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private final Logger log = Logger.getLogger(Activator.class.getName());
	
	// TODO: Implement a proper command-line interface
	public void start(BundleContext context) throws Exception {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Starting exploration...");
		}

    	WithProgress dummyProgress = new WithProgress() {
			public void start() { System.out.println("Started"); }
			public void setValue(int value) { }
			public void setMaximum(int max) { }
			public void complete(boolean success) { System.out.println("Completed successfully: " + success); }
		};
    	 
		ProcessConfiguration confRef = new ProcessConfiguration();
		confRef.getOption(ExploreApplication.OPTION_SNAPSHOT_NAME).setValue("ref");
		confRef.getOption("org.opencompare.explorers.files.FolderExplorer/root.folder").setValue("d:\\Users\\ckulak\\Desktop\\ref");
    	Snapshot ref = ExploreApplication.explore(confRef, dummyProgress);

		ProcessConfiguration confActual = new ProcessConfiguration();
		confActual.getOption(ExploreApplication.OPTION_SNAPSHOT_NAME).setValue("actual");
		confActual.getOption("org.opencompare.explorers.files.FolderExplorer/root.folder").setValue("d:\\Users\\ckulak\\Desktop\\actual");
    	Snapshot actual = ExploreApplication.explore(confActual, dummyProgress);
    	
		ProcessConfiguration confDiff = new ProcessConfiguration();
		confDiff.getOption(ExploreApplication.OPTION_SNAPSHOT_NAME).setValue("diff");
    	ExploreApplication.compare(confDiff, ref, actual, dummyProgress);

		System.out.println("Finished exploration");
	}
	
	public void stop(BundleContext context) throws Exception {
	}

}
