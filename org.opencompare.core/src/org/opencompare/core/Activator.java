package org.opencompare.core;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.opencompare.ExploreApplication;
import org.opencompare.explorable.ApplicationConfiguration;
import org.opencompare.explorable.Conflict;
import org.opencompare.explorable.Description;
import org.opencompare.explorable.OptionDefinition;
import org.opencompare.explorable.Property;
import org.opencompare.explorable.Root;
import org.opencompare.explorable.RootFactory;
import org.opencompare.explorable.ThreadControllExplorable;
import org.opencompare.explorers.NoExplorer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final String DEBUG_LOG_PROPERTY = "org.opencompare.debug";
	
	private final Logger log = Logger.getLogger(Activator.class.getName());
	
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		String loggingProp = bundleContext.getProperty(DEBUG_LOG_PROPERTY);
		LogManager.getLogManager().readConfiguration(
				this.getClass().getResourceAsStream(
						"true".equalsIgnoreCase(loggingProp) ? 
						"/org/opencompare/log-debug.properties" :
						"/org/opencompare/log-default.properties"
					)
			);
		
		Activator.context = bundleContext;
		if (log.isLoggable(Level.FINE)) log.fine("Initializing core factories: START");
		
		RootFactory rootFactory = new RootFactory();
		ApplicationConfiguration appConfig = ApplicationConfiguration.getInstance();
		
		appConfig.registerExplorableFactory(Root.class.getSimpleName(), rootFactory);
		appConfig.registerExplorableFactory(Property.class.getSimpleName(), rootFactory);
		appConfig.registerExplorableFactory(ThreadControllExplorable.class.getSimpleName(), rootFactory);
		appConfig.registerExplorableFactory(Conflict.class.getSimpleName(), rootFactory);
		appConfig.registerExplorableFactory(Description.class.getSimpleName(), rootFactory);

		NoExplorer noExplorer = new NoExplorer();
		
		appConfig.registerExplorer(Property.class.getSimpleName(), noExplorer);
		appConfig.registerExplorer(Conflict.class.getSimpleName(), noExplorer);
		appConfig.registerExplorer(Description.class.getSimpleName(), noExplorer);
		appConfig.registerExplorer(ThreadControllExplorable.class.getSimpleName(), noExplorer);

		appConfig.addOptionDefinition(
				OptionDefinition.newTextOption(
						ExploreApplication.OPTION_SNAPSHOT_NAME, 
						"Snapshot name", 
						"",
						true
					)
			);
		
		appConfig.addOptionDefinition(
				OptionDefinition.newIntOption(
						ExploreApplication.OPTION_EXPLORE_THREADS_COUNT, 
						"Explore threads count", 
						5,
						false
					)
			);
		
		if (log.isLoggable(Level.FINE)) log.fine("Initializing core factories: FINISH");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		if (log.isLoggable(Level.FINE)) log.fine("Unloading core factories");
	}

}
