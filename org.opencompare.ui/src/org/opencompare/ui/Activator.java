package org.opencompare.ui;

import org.opencompare.ExploreApplication;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting exploration...");
		ExploreApplication.main(new String[] {});
		System.out.println("Finished exploration");
	}
	
	public void stop(BundleContext context) throws Exception {
	}

}
