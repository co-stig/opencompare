package org.opencompare.explorable;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.opencompare.database.Database;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;

public abstract class Configuration {

	private static Database sharedDatabase;
	
	private static final Map<String, LinkedList<ExplorableFactory>> EXPLORABLE_FACTORIES = new HashMap<String, LinkedList<ExplorableFactory>>();
	private static final Map<String, LinkedList<Explorer>> EXPLORERS = new HashMap<String, LinkedList<Explorer>>();
	private static final Map<String, String> PROPERTIES = Collections.synchronizedMap(new HashMap<String, String>());
	private static final List<Closeable> TO_BE_CLOSED = Collections.synchronizedList(new ArrayList<Closeable>());
	
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
	
	// So far this one is only used for generating unique IDs
	public static Database getSharedDatabase() {
		return sharedDatabase;
	}
	
	// TODO: Extract a normal singleton instead
	public static void initialize(Database d) {
		sharedDatabase = d;
		TO_BE_CLOSED.clear(); 
	}
	
	public static void saveConfiguration(Properties to) {
		saveExplorableFactories(to);
		saveExplorers(to);
	}

	private static void saveExplorableFactories(Properties to) {
		for (Entry<String, LinkedList<ExplorableFactory>> entry: EXPLORABLE_FACTORIES.entrySet()) {
			String type = entry.getKey();
			int counter = 0;
			for (ExplorableFactory factory: entry.getValue()) {
				to.setProperty("explorablefactory-" + type + "-" + ++counter, factory.getClass().getName());
			}			
		}
	}
	
	private static void saveExplorers(Properties to) {
		for (Entry<String, LinkedList<Explorer>> entry: EXPLORERS.entrySet()) {
			String type = entry.getKey();
			int counter = 0;
			for (Explorer explorer: entry.getValue()) {
				to.setProperty("explorer-" + type + "-" + ++counter, explorer.getClass().getName());
			}			
		}
	}

	public static void setProperty(String name, String value) {
		PROPERTIES.put(name, value);
	}

	public static String getProperty(String name) {
		return PROPERTIES.get(name);
	}
	
	public static void closeOnFinish(Closeable closeable) {
		TO_BE_CLOSED.add(closeable);
	}
	
	public static Map<Closeable, IOException> close() {
		Map<Closeable, IOException> res = new HashMap<Closeable, IOException>();
		
		for (Closeable c: TO_BE_CLOSED) {
			try {
				System.out.println("Closing " + c);
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
				res.put(c, e);
			}
		}
		
		return res;
	}
	
	
}
