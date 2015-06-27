package org.opencompare;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import org.opencompare.compare.CompareThread;
import org.opencompare.database.Database;
import org.opencompare.database.DatabaseManager;
import org.opencompare.database.DatabaseManagerFactory;
import org.opencompare.explorable.ProcessConfiguration;
import org.opencompare.explorable.Root;
import org.opencompare.explorable.RootFactory;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explore.ExplorationProgressThread;
import org.opencompare.explore.ExploringThread;
import org.opencompare.explore.TerminatingThread;

public class ExploreApplication {

	public final static String OPTION_SNAPSHOT_NAME = "core/snapshot.name";
	public final static String OPTION_EXPLORE_THREADS_COUNT = "core/explore.threads.count";
	
    public static String TOOL_VERSION = "0.2";
    
    public static boolean ENABLE_EXPERIMENTAL_FUNCTIONALITY = false;

    public static String getUser() {
    	return "username";
    }

    // TODO: Split it into several smaller methods
    public static Snapshot explore(ProcessConfiguration config, WithProgress progress) throws InterruptedException, ExplorationException, IOException {
        System.out.println("Exploring with configuration: " + config);
    	
    	DatabaseManager dbm = DatabaseManagerFactory.get();

    	String snapshotName = config.getOption(OPTION_SNAPSHOT_NAME).getStringValue();
    	int threadsCount = config.getOption(OPTION_EXPLORE_THREADS_COUNT).getIntValue();
    	
    	Snapshot newDatabase = dbm.createExplorablesDatabase(snapshotName);
    	
        Database rootConnection = dbm.newExplorablesConnection(newDatabase);	// TODO: This is only used to enqueue the Root

        System.out.print("Estimating size: ");
        int estimatedSize = 1000;	// TODO: Create good estimation (add to Explorer interface?)
        System.out.println(estimatedSize);

        newDatabase.setState(Snapshot.State.InProgress);
        ExploringThread anyThread = null;
        
        for (int i = 0; i < threadsCount; ++i) {
            // Apache Derby recommends using one connection per thread, its
            // multithreading semantics is not intuitive.
            Database database = dbm.newExplorablesConnection(newDatabase);
            anyThread = new ExploringThread(config, database);
            anyThread.start();
        }

        Database progressDb = dbm.newExplorablesConnection(newDatabase);
        try {
            ExplorationProgressThread progressThread = new ExplorationProgressThread(progressDb, 1000, progress, estimatedSize, true);
            progressThread.start();

            // Now everything is ready -- enqueue a Root
            anyThread.enqueue(
            		new Root(), 
            		RootFactory.TYPE_ROOT
        		);
            
            Thread terminator = new TerminatingThread(threadsCount, 1000, progressThread);
            terminator.start();
            terminator.join();
        } finally {
            try {
                progressDb.close();
                rootConnection.close();
                Map<Closeable, IOException> exceptions = config.close();
                System.out.println(exceptions);
                // TODO: Handle exceptions better somehow?
            } catch (IOException ex) {
                throw new ExplorationException("Unable to close the progress database", ex);
            }
        }

        newDatabase.setState(Snapshot.State.Finished);
        newDatabase.recalculateSize();
        newDatabase.save(config);

        return newDatabase;
    }

    public static Snapshot compare(ProcessConfiguration config, Snapshot referenceSnapshot, Snapshot actualSnapshot, WithProgress progress) throws InterruptedException, ExplorationException, IOException {
        System.out.println("Comparing with configuration: " + config);
        
    	DatabaseManager dbm = DatabaseManagerFactory.get();
    	
        Database referenceDb = dbm.newExplorablesConnection(referenceSnapshot);
        Database actualDb = dbm.newExplorablesConnection(actualSnapshot);
        
        System.out.print("Estimating size: ");
        int estimatedSize = Math.max(referenceDb.size(), actualDb.size());
        System.out.println(estimatedSize);

    	String conflictSnapshotName = config.getOption(OPTION_SNAPSHOT_NAME).getStringValue();
        Snapshot newDatabase = dbm.createConflictsDatabase(conflictSnapshotName);
        Database conflictDb = dbm.newConflictsConnection(newDatabase);

        Database progressDb = dbm.newConflictsConnection(newDatabase);
        
        newDatabase.setState(Snapshot.State.InProgress);
        
        try {
            ExplorationProgressThread progressThread = new ExplorationProgressThread(progressDb, 1000, progress, estimatedSize, false);
            
            Thread compareThread = new CompareThread(config, actualDb, referenceDb, conflictDb, progressThread);
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
        newDatabase.save(config);
        
        return newDatabase;
    }

}
