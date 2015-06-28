package org.opencompare.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private final Logger log = Logger.getLogger(Activator.class.getName());

    public void start(BundleContext context) throws Exception {
		if (log.isLoggable(Level.FINE)) log.fine("Starting UI...");
		MainWindow.main(new String[] {});
		if (log.isLoggable(Level.FINE)) log.fine("Finished UI");
	}
	
	public void stop(BundleContext context) throws Exception {
	}

}
