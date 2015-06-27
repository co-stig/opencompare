package org.opencompare.explorable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.opencompare.database.IdGenerator;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;

public class ApplicationConfiguration {

	private final Map<String, LinkedList<ExplorableFactory>> explorableFactories = new HashMap<String, LinkedList<ExplorableFactory>>();
	private final Map<String, LinkedList<Explorer>> explorers = new HashMap<String, LinkedList<Explorer>>();
	private final Set<OptionDefinition> optionDefinitions = new TreeSet<OptionDefinition>();

	private static ApplicationConfiguration inst = new ApplicationConfiguration();
	
	public static ApplicationConfiguration getInstance() {
		return inst;
	}
	
	public void registerExplorableFactory(String type, ExplorableFactory factory) {
		synchronized (explorableFactories) {
			LinkedList<ExplorableFactory> factories = explorableFactories.get(type);
			if (factories == null) {
				factories = new LinkedList<ExplorableFactory>();
			}
			factories.push(factory);
			explorableFactories.put(type, factories);
		}
	}

	public void unregisterExplorableFactory(String type, ExplorableFactory factory) {
		synchronized (explorableFactories) {
			LinkedList<ExplorableFactory> factories = explorableFactories.get(type);
			if (factories != null) {
				while (factories.remove(factory)) {
					// Remove all
				}
				if (factories.isEmpty()) {
					explorableFactories.remove(factories);
				}
			}
		}
	}

	public void registerExplorer(String type, Explorer explorer) {
		synchronized (explorers) {
			LinkedList<Explorer> explorersForType = explorers.get(type);
			if (explorersForType == null) {
				explorersForType = new LinkedList<Explorer>();
			}
			explorersForType.push(explorer);
			explorers.put(type, explorersForType);
		}
	}

	public void unregisterExplorer(String type, Explorer explorer) {
		synchronized (explorers) {
			LinkedList<Explorer> explorersForType = explorers.get(type);
			if (explorersForType != null) {
				while (explorersForType.remove(explorer)) {
					// Remove all
				}
				if (explorersForType.isEmpty()) {
					explorers.remove(explorersForType);
				}
			}
		}
	}

	public List<Explorer> getExplorers(String type) throws ExplorationException {
		synchronized (explorers) {
			LinkedList<Explorer> explorersForType = explorers.get(type);
			if (explorersForType != null && !explorersForType.isEmpty()) {
				if (explorersForType.size() == 1) {
					return Collections.singletonList(explorersForType.peek());
				} else {
					return new ArrayList<Explorer>(explorersForType);
				}
			}
			throw new ExplorationException("No explorers are registered for type '" + type + "'. Registered explorers: " + explorersForType);
		}
	}

	// Used during exploration
	public Explorable createExplorable(ProcessConfiguration config, IdGenerator idGenerator, Explorable origin, String type, Object... params) throws ExplorationException {
		// Iterate through the available factories and try to create new
		// explorable. Return the first suitable one.
		synchronized (explorableFactories) {
			LinkedList<ExplorableFactory> factories = explorableFactories.get(type);
			if (factories != null && !factories.isEmpty()) {
				for (ExplorableFactory factory : factories) {
					System.out.println("For type " + type + ": checking factory " + factory + " out of " + factories);
					Explorable e = factory.createExplorable(config, idGenerator, origin, type, params);
					if (e != null) {
						return e;
					}
				}
			}
			throw new ExplorationException("No explorable factory is registered for type '" + type + "'. Registered factories: " + explorableFactories);
		}
	}

	// Used for UI
	public Explorable createExplorable(String type, int id, int parentId, String relativeId, String value, long hash, String sha) throws ExplorationException {
		// Iterate through the available factories and try to create new
		// explorable. Return the first suitable one.
		synchronized (explorableFactories) {
			LinkedList<ExplorableFactory> factories = explorableFactories.get(type);
			if (factories != null && !factories.isEmpty()) {
				for (ExplorableFactory factory : factories) {
					System.out.println("For type " + type + ": checking factory " + factory + " out of " + factories);
					Explorable e = factory.parseExplorable(type, id, parentId, relativeId, value, hash, sha);
					if (e != null) {
						return e;
					}
				}
			}
			throw new ExplorationException("No explorable factory is registered for type '" + type + "'. Registered factories: " + explorableFactories);
		}
	}

	public void appendConfiguration(Properties to) {
		appendExplorableFactories(to);
		appendExplorers(to);
	}

	private void appendExplorableFactories(Properties to) {
		for (Entry<String, LinkedList<ExplorableFactory>> entry : explorableFactories.entrySet()) {
			String type = entry.getKey();
			int counter = 0;
			for (ExplorableFactory factory : entry.getValue()) {
				to.setProperty("explorablefactory-" + type + "-" + ++counter, factory.getClass().getName());
				// TODO: Serialize options
			}
		}
	}

	private void appendExplorers(Properties to) {
		for (Entry<String, LinkedList<Explorer>> entry : explorers.entrySet()) {
			String type = entry.getKey();
			int counter = 0;
			for (Explorer explorer : entry.getValue()) {
				to.setProperty("explorer-" + type + "-" + ++counter, explorer.getClass().getName());
			}
		}
	}

	public void addOptionDefinition(OptionDefinition option) throws AlreadyRegisteredException {
		synchronized (optionDefinitions) {
			OptionDefinition existing = getOptionDefinition(option.getName());
			if (existing != null) {
				throw new AlreadyRegisteredException("Option '" + option.getName() + "' already registered: {" + existing + "}, while adding {" + option + "}");
			}
			optionDefinitions.add(option);
		}
	}
	
	public void removeOptionDefinition(String name) throws AlreadyRegisteredException {
		synchronized (optionDefinitions) {
			Iterator<OptionDefinition> it = optionDefinitions.iterator();
			while (it.hasNext()) {
				OptionDefinition next = it.next();
				if (next.getName().equals(name)) {
					it.remove();
					return;
				}
			}
		}
	}
	
	public OptionDefinition getOptionDefinition(String name) {
		synchronized (optionDefinitions) {
			for (OptionDefinition o: optionDefinitions) {
				if (o.getName().equals(name)) {
					return o;
				}
			}
		}
		return null;
	}
	
	// TODO: Probably it's not the most elegant way to do it
	public List<OptionValue> initializeAllOptions() {
		synchronized (optionDefinitions) {
			List<OptionValue> res = new ArrayList<OptionValue>(optionDefinitions.size());
			for (OptionDefinition od: optionDefinitions) {
				res.add(new OptionValue(od));
			}
			return res;
		}
	}
	
}
