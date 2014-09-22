package org.opencompare.explore;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.opencompare.database.Database;
import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ThreadControllExplorable;
import org.opencompare.explorers.Explorer;

public class ExploringThread extends Thread {

    public static final AtomicInteger exploringCount = new AtomicInteger(0);
    private final ExplorationQueue queue;
    private final Database database;

    public ExploringThread(ExplorationQueue queue, Database database) {
        this.queue = queue;
        this.database = database;
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

                	List<Explorer> explorers = Configuration.getExplorers(clazz);
                	for (Explorer explorer: explorers) {
                		explorer.explore(database, parent);
                	}
                	
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
