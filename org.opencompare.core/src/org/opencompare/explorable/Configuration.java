package org.opencompare.explorable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.opencompare.database.Database;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExplorationQueue;
import org.opencompare.explorers.ExplorerFactory;

public abstract class Configuration {

	private static ExplorationQueue explorationQueue;
	private static Database sharedDatabase;
	private static final Map<String, LinkedList<ExplorableFactory>> EXPLORABLE_FACTORIES = new HashMap<String, LinkedList<ExplorableFactory>>();
	private static final Map<String, LinkedList<ExplorerFactory>> EXPLORER_FACTORIES = new HashMap<String, LinkedList<ExplorerFactory>>();
	
	public static void registerExplorableFactory(String type, ExplorableFactory factory) {
		synchronized(EXPLORABLE_FACTORIES) {
			LinkedList<ExplorableFactory> factories = EXPLORABLE_FACTORIES.get(type);
			if (factories == null) {
				factories = new LinkedList<ExplorableFactory>();
			}
			factories.push(factory);
			EXPLORABLE_FACTORIES.put(type, factories);
		}
	}
	
	public static void unregisterExplorableFactory(String type, ExplorableFactory factory) {
		synchronized(EXPLORABLE_FACTORIES) {
			LinkedList<ExplorableFactory> factories = EXPLORABLE_FACTORIES.get(type);
			if (factories != null) {
				while (factories.remove(factory)) {
					// Remove all
				}
				if (factories.isEmpty()) {
					EXPLORABLE_FACTORIES.remove(factories);
				}
			}
		}
	}
	
	public static void registerExplorerFactory(String type, ExplorerFactory factory) {
		synchronized(EXPLORER_FACTORIES) {
			LinkedList<ExplorerFactory> factories = EXPLORER_FACTORIES.get(type);
			if (factories == null) {
				factories = new LinkedList<ExplorerFactory>();
			}
			factories.push(factory);
			EXPLORER_FACTORIES.put(type, factories);
		}
	}
	
	public static void unregisterExplorerFactory(String type, ExplorerFactory factory) {
		synchronized(EXPLORER_FACTORIES) {
			LinkedList<ExplorerFactory> factories = EXPLORER_FACTORIES.get(type);
			if (factories != null) {
				while (factories.remove(factory)) {
					// Remove all
				}
				if (factories.isEmpty()) {
					EXPLORER_FACTORIES.remove(factories);
				}
			}
		}
	}
	
	public static ExplorableFactory getExplorableFactory(String type) throws ExplorationException {
		synchronized(EXPLORABLE_FACTORIES) {
			LinkedList<ExplorableFactory> factories = EXPLORABLE_FACTORIES.get(type);
			if (factories != null && !factories.isEmpty()) {
				return factories.peek();
			}
			throw new ExplorationException("No explorable factory is registered for type '" + type + "'. Registered factories: " + EXPLORABLE_FACTORIES);
		}
	}

	public static ExplorerFactory getExplorerFactory(String type) throws ExplorationException {
		synchronized(EXPLORER_FACTORIES) {
			LinkedList<ExplorerFactory> factories = EXPLORER_FACTORIES.get(type);
			if (factories != null && !factories.isEmpty()) {
				return factories.peek();
			}
			throw new ExplorationException("No explorer factory is registered for type '" + type + "'. Registered factories: " + EXPLORER_FACTORIES);
		}
	}
	
	// TODO: Do something with this ugly "threadDatabase"
	public static void enqueue(Database threadDatabase, Explorable origin, String type, Object... params) throws InterruptedException, ExplorationException {
		// 0. Find a suitable factory for this object type
		ExplorableFactory factory = getExplorableFactory(type);
		
		// 1. Actually create this new Explorable, based on type and params. Use subfactories.
		Explorable e = factory.newExplorable(origin, type, params);
		
		// 1.b. Calculate SHA
		e.calculateSha(origin.getTempFullId());
		
		// 2. Store it in database, use thread connection
		// TODO: This is wrong! We should have 1 DB connection per thread! NOT STATIC HERE (or one Configuration object per thread)
		threadDatabase.add(e);
		
		// 3. Enqueue
		explorationQueue.add(e);
	}

	public static ExplorationQueue getQueue() {
		return explorationQueue;
	}

	// So far this one is only used for generating unique IDs
	public static Database getSharedDatabase() {
		return sharedDatabase;
	}
	
	// TODO: Extract a normal singleton instead
	public static void initialize(ExplorationQueue q, Database d) {
		explorationQueue = q;
		sharedDatabase = d;
	}
}
