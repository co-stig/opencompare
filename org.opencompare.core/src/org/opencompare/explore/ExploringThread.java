package org.opencompare.explore;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.opencompare.database.Database;
import org.opencompare.explorable.ApplicationConfiguration;
import org.opencompare.explorable.ProcessConfiguration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ThreadControllExplorable;
import org.opencompare.explorers.Explorer;

public class ExploringThread extends Thread {

    public static final AtomicInteger exploringCount = new AtomicInteger(0);
    private final ProcessConfiguration config;
    private final Database threadDatabase;

    public ExploringThread(ProcessConfiguration config, Database threadDatabase) {
    	this.config = config;
        this.threadDatabase = threadDatabase;
    }

    public void run() {
        try {
            while (!isInterrupted()) {

                Explorable parent = null;
                try {
                    parent = ExplorationQueue.getInstance().next();
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

                	List<Explorer> explorers = ApplicationConfiguration.getInstance().getExplorers(clazz);
                	for (Explorer explorer: explorers) {
                		explorer.explore(config, this, parent);
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
            if (threadDatabase != null) {
                try {
                    threadDatabase.close();
                } catch (IOException ex) {
                    System.out.println("ERROR while closing the database for an exploration thread: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            System.out.println("Exiting ExploringThread");
        }
    }
    
	public Explorable enqueue(Explorable origin, String type, Object... params) throws InterruptedException, ExplorationException {
		Explorable e = ApplicationConfiguration.getInstance().createExplorable(config, threadDatabase, origin, type, params);
		
		// 1. Calculate SHA
		e.calculateSha(origin.getTempFullId());
		
		// 2. Store it in database, use thread connection
		threadDatabase.add(e);
		
		// 3. Enqueue
		ExplorationQueue.getInstance().add(e);
		
		return e;
	}

}
