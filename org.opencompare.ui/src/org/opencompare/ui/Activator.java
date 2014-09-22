package org.opencompare.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting UI...");
		MainWindow.main(new String[] {});
		System.out.println("Finished UI");
	}
	
	public void stop(BundleContext context) throws Exception {
	}

}
