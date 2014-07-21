package org.opencompare.explorable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.opencompare.database.Database;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExplorationQueue;
import org.opencompare.explorers.ExplorerFactory;

public abstract class Configuration {

	// TODO: Initialize all those!
	private static ExplorationQueue queue;
	private static Database database;
	private static final Map<String, LinkedList<ExplorableFactory>> EXPLORABLE_FACTORIES = new HashMap<String, LinkedList<ExplorableFactory>>();
	private static final Map<String, LinkedList<ExplorerFactory>> EXPLORER_FACTORIES = new HashMap<String, LinkedList<ExplorerFactory>>();
	
	public static void registerFactory(String type, ExplorableFactory factory) {
		synchronized(EXPLORABLE_FACTORIES) {
			LinkedList<ExplorableFactory> factories = EXPLORABLE_FACTORIES.get(type);
			if (factories == null) {
				factories = new LinkedList<ExplorableFactory>();
			}
			factories.push(factory);
			EXPLORABLE_FACTORIES.put(type, factories);
		}
	}
	
	public static void unregisterFactory(String type, ExplorableFactory factory) {
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
	
	public static void registerFactory(String type, ExplorerFactory factory) {
		synchronized(EXPLORER_FACTORIES) {
			LinkedList<ExplorerFactory> factories = EXPLORER_FACTORIES.get(type);
			if (factories == null) {
				factories = new LinkedList<ExplorerFactory>();
			}
			factories.push(factory);
			EXPLORER_FACTORIES.put(type, factories);
		}
	}
	
	public static void unregisterFactory(String type, ExplorerFactory factory) {
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
		}
		throw new ExplorationException("No explorable factory is registered for type '" + type + "'");
	}

	public static ExplorerFactory getExplorerFactory(String type) throws ExplorationException {
		synchronized(EXPLORER_FACTORIES) {
			LinkedList<ExplorerFactory> factories = EXPLORER_FACTORIES.get(type);
			if (factories != null && !factories.isEmpty()) {
				return factories.peek();
			}
		}
		throw new ExplorationException("No explorer factory is registered for type '" + type + "'");
	}
	
	public static void enqueue(Explorable origin, String type, Object... params) throws InterruptedException, ExplorationException {
		// 0. Find a suitable factory for this object type
		ExplorableFactory factory = getExplorableFactory(type);
		
		// 1. Actually create this new Explorable, based on type and params. Use subfactories.
		Explorable e = factory.newExplorable(origin, type, params);
		
		// 2. Store it in database
		database.add(e);
		
		// 3. Enqueue
		queue.add(e);
	}

	public static ExplorationQueue getQueue() {
		return queue;
	}

	public static Database getDatabase() {
		return database;
	}
}
