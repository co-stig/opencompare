package org.opencompare.database;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import org.opencompare.Snapshot;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;

// TODO: Create separate interfaces for different types of Database, e.g. DescriptionsDatabase
/**
 * All methods should be synchronized.
 */
public interface Database extends Closeable, IdGenerator {

    Explorable getRoot() throws ExplorationException;

    Explorable getById(int id) throws ExplorationException;

    String getFullId(Explorable e) throws ExplorationException;
    
	/**
	 * Returns list of all child Explorables for the given parent. Two
	 * subsequent calls to this method should return the lists with the same
	 * order. This is important to display it correctly in the tree. Also this
	 * list should have Set semantics, i.e. all elements should be equal.
	 * 
	 * @param parent Not null
	 * @return
	 * @throws ExplorationException
	 */
    List<Explorable> getChildren(Explorable parent) throws ExplorationException;
    List<Explorable> getChildren(Explorable parent, ConflictFilter filter) throws ExplorationException;
    
    Map<String, Explorable> getChildrenAsMap(Explorable parent) throws ExplorationException;

    void add(Explorable e) throws ExplorationException;
    void addActual(Explorable e) throws ExplorationException;
    void addReference(Explorable e) throws ExplorationException;

    int size() throws ExplorationException;
    int sizeFilesOnly() throws ExplorationException;

    Snapshot getSnapshot();
}
