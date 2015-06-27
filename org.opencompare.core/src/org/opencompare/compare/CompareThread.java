package org.opencompare.compare;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.opencompare.StoppableThread;
import org.opencompare.database.Database;
import org.opencompare.explorable.ApplicationConfiguration;
import org.opencompare.explorable.Conflict;
import org.opencompare.explorable.ConflictType;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ProcessConfiguration;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExplorationProgressThread;

/**
 * TODO: Describe comparison algorithm and (in)equality semantics.
 * TODO: Compare now runs in a single thread -- parallelize!
 */
public class CompareThread extends StoppableThread {

	private final static String CONFLICT = Conflict.class.getSimpleName();
	
	private final ProcessConfiguration config;
    private final Database actualDatabase;
    private final Database referenceDatabase;
    private final Database conflictsDatabase;
    private final ExplorationProgressThread progress;

    public CompareThread(ProcessConfiguration config, Database actual, Database reference, Database conflicts, ExplorationProgressThread progress) {
    	this.config = config;
    	this.actualDatabase = actual;
        this.referenceDatabase = reference;
        this.conflictsDatabase = conflicts;
        this.progress = progress;
    }

    private Conflict compareRecursive(Explorable reference, Explorable actual, Conflict parent) throws ExplorationException {
    	System.out.println("compareRecursive(" + reference + ", " + actual + ", " + parent + ")");
        if (isStopped()) {
            throw new ExplorationException("The thread is stopped (comparison cancelled)");
        }
        
        Conflict res;
        int parentId = parent == null ? 0 : parent.getId();
        int id = parent == null ? 1 : 0;

        if (actual == null && reference != null) {
            // Add reference children recursively
            res = (Conflict) ApplicationConfiguration.getInstance().createExplorable(config, conflictsDatabase, parent, CONFLICT, id, parentId, reference, null, ConflictType.Missing, null);
            System.out.println(" * ANR created conflict: " + res);
            for (Explorable referenceChild : referenceDatabase.getChildren(reference)) {
                compareRecursive(referenceChild, null, res);
            }
        } else if (actual != null && reference == null) {
            // Add actual children recursively 
            res = (Conflict) ApplicationConfiguration.getInstance().createExplorable(config, conflictsDatabase, parent, CONFLICT, id, parentId, null, actual, ConflictType.New, null);
            System.out.println(" * ARN created conflict: " + res);
            for (Explorable actualChild : actualDatabase.getChildren(actual)) {
                compareRecursive(null, actualChild, res);
            }
        } else if (actual != null && reference != null) {
        	// Add actual children recursively 
       		res = (Conflict) ApplicationConfiguration.getInstance().createExplorable(config, conflictsDatabase, parent, CONFLICT, id, parentId, reference, actual, null, null);
       		System.out.println(" * AR created conflict: " + res);
            ConflictType childrenConflictType = compareBothRecursive(reference, actual, res);
            res.setType(childrenConflictType);
        } else {
            // Both actual and reference are null -- impossible, we should never get here
            throw new ExplorationException(
                    "While comparing actual and reference objects both were null. This should never happen, thus fatal exception.");
        }

        if (reference != null) {
        	conflictsDatabase.addReference(reference);
        }
        if (actual != null) {
        	conflictsDatabase.addActual(actual);
        }
        conflictsDatabase.add(res);

        return res;
    }

    /**
     * Here both reference and actual are not null, so we need to check for
     * modifications.
     */
    private ConflictType compareBothRecursive(Explorable reference, Explorable actual, Conflict parent) throws ExplorationException {
        ConflictType res = ConflictType.Identical;

        Map<String, Explorable> referenceChildren = referenceDatabase.getChildrenAsMap(reference);
        Map<String, Explorable> actualChildren = actualDatabase.getChildrenAsMap(actual);

        Set<String> allKeys = new TreeSet<String>(referenceChildren.keySet());
        allKeys.addAll(actualChildren.keySet());

        for (String id : allKeys) {
            Explorable referenceChild = referenceChildren.get(id);
            Explorable actualChild = actualChildren.get(id);

            Conflict c = compareRecursive(referenceChild, actualChild, parent);
            if (!c.getType().equals(ConflictType.Identical)) {
                // Children are not identical
                res = ConflictType.Modified;
            }
        }

        if (res.equals(ConflictType.Identical)) {
			/*
			 * Children were identical, now let's check the values and their
			 * hashes. We should check both, because value can be truncated
			 * somewhere in the persistence layer. Also (theoretically) two
			 * different values may produce identical hashes. See
			 * Explorable.equals for further details.
			 */
            if (!reference.equals(actual)) {
                res = ConflictType.Modified;
            }
        }

        return res;
    }

    @Override
    public void run() {
        try {
            compareRecursive(referenceDatabase.getRoot(), actualDatabase.getRoot(), null);
        } catch (Exception ex) {
            System.out.println("Unable to compare snapshots, or the thread is stopped");
            ex.printStackTrace();
        } finally {
            progress.stopThread();
            System.out.println("Exiting CompareThread");
        }
    }
}
