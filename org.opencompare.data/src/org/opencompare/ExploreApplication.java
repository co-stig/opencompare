package org.opencompare;

import java.io.File;
import java.io.IOException;

import org.opencompare.WithProgress;
import org.opencompare.compare.CompareThread;
import org.opencompare.database.Database;
import org.opencompare.database.DatabaseManager;
import org.opencompare.database.JdbcExplorablesDatabase;
import org.opencompare.explorable.ExplorableFactory;
import org.opencompare.explorable.Root;
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
    
    public static Database explore(String rootFolder, String snapshotName, WithProgress progress) throws InterruptedException, ExplorationException, IOException {
    	Snapshot newDatabase = DatabaseManager.createExplorablesDatabase(snapshotName);
    	
        Database rootConnection = DatabaseManager.newExplorablesConnection(newDatabase);
        Root root = new Root(new File(rootFolder), "1");

        System.out.print("Estimating size: ");
        int estimatedSize = 1000;	// TODO: Create good estimation (add to Explorer interface?)
        System.out.println(estimatedSize);

        ExplorationQueue queue = new ExplorationQueue(root);

        newDatabase.setState(Snapshot.State.InProgress);
        
        for (int i = 0; i < EXPLORING_THREADS_COUNT; ++i) {
            // Apache Derby recommends using one connection per thread, its
            // multithreading semantics is not intuitive.
            JdbcExplorablesDatabase database = DatabaseManager.newExplorablesConnection(newDatabase);
            new ExploringThread(queue, database, database.getFactory()).start();
        }

        Database progressDb = DatabaseManager.newExplorablesConnection(newDatabase);
        try {
            ExplorationProgressThread progressThread = new ExplorationProgressThread(progressDb, 1000, progress, estimatedSize, true);
            progressThread.start();

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

        return DatabaseManager.newExplorablesConnection(newDatabase);
    }

    public static Database compare(Snapshot referenceSnapshot, Snapshot actualSnapshot, String conflictSnapshotName, WithProgress progress) throws InterruptedException, ExplorationException, IOException {
        Database referenceDb = DatabaseManager.newExplorablesConnection(referenceSnapshot);
        Database actualDb = DatabaseManager.newExplorablesConnection(actualSnapshot);

        System.out.print("Estimating size: ");
        int estimatedSize = Math.max(referenceDb.size(), actualDb.size());
        System.out.println(estimatedSize);

        Snapshot newDatabase = DatabaseManager.createConflictsDatabase(conflictSnapshotName);
        Database conflictDb = DatabaseManager.newConflictsConnection(newDatabase);
        Database progressDb = DatabaseManager.newConflictsConnection(newDatabase);
        
        newDatabase.setState(Snapshot.State.InProgress);
        
        try {
            ExplorationProgressThread progressThread = new ExplorationProgressThread(progressDb, 1000, progress, estimatedSize, false);
            
            Thread compareThread = new CompareThread(actualDb, referenceDb, conflictDb, progressThread);
            compareThread.start();

            progressThread.start();
            progressThread.join();
        } finally {
            try {
                progressDb.close();
            } catch (IOException ex) {
                throw new ExplorationException("Unable to close the progress database", ex);
            }
            progress.complete(true);
        }

        newDatabase.setState(Snapshot.State.Finished);
        newDatabase.recalculateSize();
        
        return conflictDb;
    }

    public static void main(String[] args) throws InterruptedException, ExplorationException, IOException {
    	WithProgress dummyProgress = new WithProgress() {
			public void start() { System.out.println("Started"); }
			public void setValue(int value) { }
			public void setMaximum(int max) { }
			public void complete(boolean success) { System.out.println("Completed successfully: " + success); }
		};
    	
    	Snapshot ref = explore("d:\\Users\\ckulak\\Desktop\\ref\\system", "ref", dummyProgress).getSnapshot();
    	Snapshot actual = explore("d:\\Users\\ckulak\\Desktop\\actual\\system", "actual", dummyProgress).getSnapshot();
    	
		compare(ref, actual, "conf", dummyProgress);
	}
    
}
