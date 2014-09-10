package org.opencompare.database;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		DatabaseManagerFactory.register(new JdbcDatabaseManager());
		System.out.println("Registered JDBC database manager: " + JdbcDatabaseManager.getDbFolder());
	}
	
	public void stop(BundleContext context) throws Exception {
		DatabaseManagerFactory.unregister();
		System.out.println("Unregistered database manager");
	}

}
