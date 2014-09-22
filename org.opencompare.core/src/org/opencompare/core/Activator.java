package org.opencompare.core;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Conflict;
import org.opencompare.explorable.Description;
import org.opencompare.explorable.Property;
import org.opencompare.explorable.Root;
import org.opencompare.explorable.RootFactory;
import org.opencompare.explorable.ThreadControllExplorable;
import org.opencompare.explorers.core.NoExplorer;
import org.opencompare.explorers.core.RootExplorer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		System.out.println("Initializing core factories: START");
		
		RootFactory rootFactory = new RootFactory();
		
		Configuration.registerExplorableFactory(Root.class.getSimpleName(), rootFactory);
		Configuration.registerExplorableFactory(Property.class.getSimpleName(), rootFactory);
		Configuration.registerExplorableFactory(ThreadControllExplorable.class.getSimpleName(), rootFactory);
		Configuration.registerExplorableFactory(Conflict.class.getSimpleName(), rootFactory);
		Configuration.registerExplorableFactory(Description.class.getSimpleName(), rootFactory);

		NoExplorer noExplorer = new NoExplorer();
		
		Configuration.registerExplorer(Root.class.getSimpleName(), new RootExplorer());
		Configuration.registerExplorer(Property.class.getSimpleName(), noExplorer);
		Configuration.registerExplorer(Conflict.class.getSimpleName(), noExplorer);
		Configuration.registerExplorer(Description.class.getSimpleName(), noExplorer);
		Configuration.registerExplorer(ThreadControllExplorable.class.getSimpleName(), noExplorer);
		
		System.out.println("Initializing core factories: FINISH");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		System.out.println("Unloading core factories");
	}

}
