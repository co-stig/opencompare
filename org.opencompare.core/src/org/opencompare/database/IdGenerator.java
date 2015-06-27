package org.opencompare.database;

import org.opencompare.explore.ExplorationException;

public interface IdGenerator {

    int nextId() throws ExplorationException;
    
}
