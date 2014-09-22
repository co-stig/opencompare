package org.opencompare.explorable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opencompare.database.Database;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExplorationQueue;
import org.opencompare.explorers.Explorer;

public abstract class Configuration {

	private static ExplorationQueue explorationQueue;
	private static Database sharedDatabase;
	
	private static final Map<String, LinkedList<ExplorableFactory>> EXPLORABLE_FACTORIES = new HashMap<String, LinkedList<ExplorableFactory>>();
	private static final Map<String, LinkedList<Explorer>> EXPLORERS = new HashMap<String, LinkedList<Explorer>>();
	
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
	
	public static void registerExplorer(String type, Explorer explorer) {
		synchronized(EXPLORERS) {
			LinkedList<Explorer> explorers = EXPLORERS.get(type);
			if (explorers == null) {
				explorers = new LinkedList<Explorer>();
			}
			explorers.push(explorer);
			EXPLORERS.put(type, explorers);
		}
	}
	
	public static void unregisterExplorer(String type, Explorer explorer) {
		synchronized(EXPLORERS) {
			LinkedList<Explorer> explorers = EXPLORERS.get(type);
			if (explorers != null) {
				while (explorers.remove(explorer)) {
					// Remove all
				}
				if (explorers.isEmpty()) {
					EXPLORERS.remove(explorers);
				}
			}
		}
	}
	
	public static List<Explorer> getExplorers(String type) throws ExplorationException {
		synchronized(EXPLORERS) {
			LinkedList<Explorer> explorers = EXPLORERS.get(type);
			if (explorers != null && !explorers.isEmpty()) {
				if (explorers.size() == 1) {
					return Collections.singletonList(explorers.peek());
				} else {
					return new ArrayList<Explorer>(explorers);
				}
			}
			throw new ExplorationException("No explorers are registered for type '" + type + "'. Registered explorers: " + EXPLORERS);
		}
	}

	
	// TODO: Do something with this ugly "threadDatabase"
	// TODO: This method (together with getExplorableFactory) probably doesn't belong to the Configuration class
	public static Explorable enqueue(Database threadDatabase, Explorable origin, String type, Object... params) throws InterruptedException, ExplorationException {
		Explorable e = createExplorable(origin, type, params);
		
		// 1.b. Calculate SHA
		e.calculateSha(origin.getTempFullId());
		
		// 2. Store it in database, use thread connection
		// TODO: This is wrong! We should have 1 DB connection per thread! NOT STATIC HERE (or one Configuration object per thread)
		threadDatabase.add(e);
		
		// 3. Enqueue
		explorationQueue.add(e);
		
		return e;
	}

	// Used during exploration
	public static Explorable createExplorable(Explorable origin, String type, Object... params) throws ExplorationException {
		// Iterate through the available factories and try to create new explorable. Return the first suitable one.
		synchronized(EXPLORABLE_FACTORIES) {
			LinkedList<ExplorableFactory> factories = EXPLORABLE_FACTORIES.get(type);
			if (factories != null && !factories.isEmpty()) {
				for (ExplorableFactory factory: factories) {
					System.out.println("For type " + type + ": checking factory " + factory + " out of " + factories);
					Explorable e = factory.createExplorable(origin, type, params);
					if (e != null) {
						return e;
					}
				}
			}
			throw new ExplorationException("No explorable factory is registered for type '" + type + "'. Registered factories: " + EXPLORABLE_FACTORIES);
		}
	}

	// Used for UI
	public static Explorable createExplorable(String type, int id, int parentId, String relativeId, String value, long hash, String sha) throws ExplorationException {
		// Iterate through the available factories and try to create new explorable. Return the first suitable one.
		synchronized(EXPLORABLE_FACTORIES) {
			LinkedList<ExplorableFactory> factories = EXPLORABLE_FACTORIES.get(type);
			if (factories != null && !factories.isEmpty()) {
				for (ExplorableFactory factory: factories) {
					System.out.println("For type " + type + ": checking factory " + factory + " out of " + factories);
					Explorable e = factory.parseExplorable(type, id, parentId, relativeId, value, hash, sha);
					if (e != null) {
						return e;
					}
				}
			}
			throw new ExplorationException("No explorable factory is registered for type '" + type + "'. Registered factories: " + EXPLORABLE_FACTORIES);
		}
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
