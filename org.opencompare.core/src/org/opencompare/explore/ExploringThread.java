package org.opencompare.explore;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.opencompare.database.Database;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ExplorableFactory;
import org.opencompare.explorable.ThreadControllExplorable;

public class ExploringThread extends Thread {

    public static final AtomicInteger exploringCount = new AtomicInteger(0);
    private final ExplorationQueue queue;
    private final Database database;
    private final ExplorableFactory factory; 

    public ExploringThread(ExplorationQueue queue, Database database, ExplorableFactory factory) {
        this.queue = queue;
        this.database = database;
        this.factory = factory;
    }

    public void run() {
        try {
            while (!isInterrupted()) {

                Explorable parent = null;
                try {
                    parent = queue.next();
                    if (parent instanceof ThreadControllExplorable) {
                        // It is time to terminate the thread
                        break;
                    }
                } catch (InterruptedException e) {
                    break;
                }

                // Theoretically here we may encounter a situation when the
                // exploration job has been claimed by queue.next() above, but
                // the thread got interrupted for long time right here. Then the
                // count won't get incremented, and it will look like the thread
                // is idle. In order to avoid such situations, wait for few seconds
                // in the terminating thread, when checking exploringCount variable.

                exploringCount.incrementAndGet();
                try {
                    
                	String clazz = parent.getClass().getSimpleName();
                	Configuration.getExplorerFactory(clazz).getExplorer(clazz).explore(database, parent, factory);
                    
                } catch (InterruptedException e) {
                    break;
                } catch (ExplorationException e) {
                    System.out.println("ERROR while exploring: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    exploringCount.decrementAndGet();
                }
            }
        } finally {
            if (database != null) {
                try {
                    database.close();
                } catch (IOException ex) {
                    System.out.println("ERROR while closing the database for an exploration thread: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            System.out.println("Exiting ExploringThread");
        }
    }
}
