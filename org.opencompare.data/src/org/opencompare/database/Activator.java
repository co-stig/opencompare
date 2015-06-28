package org.opencompare.database;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private final Logger log = Logger.getLogger(Activator.class.getName());

    public void start(BundleContext context) throws Exception {
		DatabaseManagerFactory.register(new JdbcDatabaseManager());
		if (log.isLoggable(Level.FINE)) log.fine("Registered JDBC database manager: " + JdbcDatabaseManager.getDbFolder());
	}
	
	public void stop(BundleContext context) throws Exception {
		DatabaseManagerFactory.unregister();
		if (log.isLoggable(Level.FINE)) log.fine("Unregistered database manager");
	}

}
