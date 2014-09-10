package org.opencompare;

import java.io.File;
import java.io.IOException;

import org.opencompare.compare.CompareThread;
import org.opencompare.database.Database;
import org.opencompare.database.DatabaseManager;
import org.opencompare.database.DatabaseManagerFactory;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Root;
import org.opencompare.explorable.RootFactory;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExplorationProgressThread;
import org.opencompare.explore.ExplorationQueue;
import org.opencompare.explore.ExploringThread;
import org.opencompare.explore.TerminatingThread;

public class ExploreApplication {

    private static final int EXPLORING_THREADS_COUNT = 5;
    
    public static String TOOL_VERSION = "0.1";
    
    public static boolean ENABLE_EXPERIMENTAL_FUNCTIONALITY = false;

    public static String getUser() {
    	return "username";
    }
    
    public static Snapshot explore(String rootFolder, String snapshotName, WithProgress progress) throws InterruptedException, ExplorationException, IOException {
    	DatabaseManager dbm = DatabaseManagerFactory.get();

    	Snapshot newDatabase = dbm.createExplorablesDatabase(snapshotName);
    	
        Database rootConnection = dbm.newExplorablesConnection(newDatabase);

        System.out.print("Estimating size: ");
        int estimatedSize = 1000;	// TODO: Create good estimation (add to Explorer interface?)
        System.out.println(estimatedSize);

        ExplorationQueue queue = new ExplorationQueue();
        
        Configuration.initialize(queue, rootConnection);

        newDatabase.setState(Snapshot.State.InProgress);
        
        for (int i = 0; i < EXPLORING_THREADS_COUNT; ++i) {
            // Apache Derby recommends using one connection per thread, its
            // multithreading semantics is not intuitive.
            Database database = dbm.newExplorablesConnection(newDatabase);
            new ExploringThread(queue, database, null).start();	// TODO: Supply real factory here (not null)
        }

        Database progressDb = dbm.newExplorablesConnection(newDatabase);
        try {
            ExplorationProgressThread progressThread = new ExplorationProgressThread(progressDb, 1000, progress, estimatedSize, true);
            progressThread.start();

            // Now everything is ready -- enqueue a Root
            Configuration.enqueue(
            		rootConnection, 
            		new Root(null, ""),		// We use this one only to calculate SHA later, thus nulls, etc. 
            		RootFactory.TYPE_ROOT, 
            		new File(rootFolder), 
            		null
        		);
            
            Thread terminator = new TerminatingThread(EXPLORING_THREADS_COUNT, queue, 1000, progressThread);
            terminator.start();
            terminator.join();
        } finally {
            try {
                progressDb.close();
                rootConnection.close();
            } catch (IOException ex) {
                throw new ExplorationException("Unable to close the progress database", ex);
            }
        }

        newDatabase.setState(Snapshot.State.Finished);
        newDatabase.recalculateSize();

        return newDatabase;
    }

    public static Snapshot compare(Snapshot referenceSnapshot, Snapshot actualSnapshot, String conflictSnapshotName, WithProgress progress) throws InterruptedException, ExplorationException, IOException {
    	DatabaseManager dbm = DatabaseManagerFactory.get();
    	
        Database referenceDb = dbm.newExplorablesConnection(referenceSnapshot);
        Database actualDb = dbm.newExplorablesConnection(actualSnapshot);

        System.out.print("Estimating size: ");
        int estimatedSize = Math.max(referenceDb.size(), actualDb.size());
        System.out.println(estimatedSize);

        Snapshot newDatabase = dbm.createConflictsDatabase(conflictSnapshotName);
        Database conflictDb = dbm.newConflictsConnection(newDatabase);
        Database progressDb = dbm.newConflictsConnection(newDatabase);
        
        newDatabase.setState(Snapshot.State.InProgress);
        
        try {
            ExplorationProgressThread progressThread = new ExplorationProgressThread(progressDb, 1000, progress, estimatedSize, false);
            
            Thread compareThread = new CompareThread(actualDb, referenceDb, conflictDb, progressThread);
            compareThread.start();

            progressThread.start();
            progressThread.join();
        } finally {
            try {
            	conflictDb.close();
                progressDb.close();
            } catch (IOException ex) {
                throw new ExplorationException("Unable to close the progress database", ex);
            }
            progress.complete(true);
        }

        newDatabase.setState(Snapshot.State.Finished);
        newDatabase.recalculateSize();
        
        return newDatabase;
    }

    public static void main(String[] args) throws InterruptedException, ExplorationException, IOException {
    	WithProgress dummyProgress = new WithProgress() {
			public void start() { System.out.println("Started"); }
			public void setValue(int value) { }
			public void setMaximum(int max) { }
			public void complete(boolean success) { System.out.println("Completed successfully: " + success); }
		};
    	
    	Snapshot ref = explore("reffolder", "ref", dummyProgress);
    	Snapshot actual = explore("actualfolder", "actual", dummyProgress);
		compare(ref, actual, "conf", dummyProgress);
	}
    
}
