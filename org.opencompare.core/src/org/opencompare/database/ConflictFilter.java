package org.opencompare.database;

import org.opencompare.explorable.Conflict;

public interface ConflictFilter {
    
    boolean include(Conflict conflict);
    
}
