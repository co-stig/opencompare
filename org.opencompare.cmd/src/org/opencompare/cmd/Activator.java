package org.opencompare.cmd;

import org.opencompare.ExploreApplication;
import org.opencompare.Snapshot;
import org.opencompare.WithProgress;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting exploration...");

    	WithProgress dummyProgress = new WithProgress() {
			public void start() { System.out.println("Started"); }
			public void setValue(int value) { }
			public void setMaximum(int max) { }
			public void complete(boolean success) { System.out.println("Completed successfully: " + success); }
		};
    	
    	Snapshot ref = ExploreApplication.explore("d:\\Users\\ckulak\\Desktop\\ref", "ref", dummyProgress);
    	Snapshot actual = ExploreApplication.explore("d:\\Users\\ckulak\\Desktop\\actual\\ref", "actual", dummyProgress);
    	ExploreApplication.compare(ref, actual, "conf", dummyProgress);

		System.out.println("Finished exploration");
	}
	
	public void stop(BundleContext context) throws Exception {
	}

}
