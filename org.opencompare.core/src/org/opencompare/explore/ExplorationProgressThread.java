package org.opencompare.explore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencompare.WithProgress;
import org.opencompare.database.Database;

public class ExplorationProgressThread extends Thread {

	private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss");
    
	private final Database database;
    private final int outputInterval;
    private final WithProgress progress;
    private final AtomicBoolean stopFlag = new AtomicBoolean(false);
    private final int estimatedSize;
    private final boolean filesOnly;
    
    private final Logger log = Logger.getLogger(ExplorationProgressThread.class.getName());

    public ExplorationProgressThread(Database database, int outputInterval, WithProgress progress, int estimatedSize, boolean filesOnly) {
        this.database = database;
        this.outputInterval = outputInterval;
        this.progress = progress;
        this.estimatedSize = estimatedSize;
        this.filesOnly = filesOnly;
    }

    public void run() {
        progress.setMaximum(estimatedSize);
        progress.start();
        
        while (!isInterrupted() && !stopFlag.get()) {
            try {
                int value = filesOnly ? database.sizeFilesOnly() : database.size();
                if (log.isLoggable(Level.FINEST)) log.finest(TIMESTAMP_FORMAT.format(new Date()) + ": " + value);
                progress.setValue(value);
                Thread.sleep(outputInterval);
            } catch (ExplorationException ex) {
                if (log.isLoggable(Level.SEVERE)) log.severe("Error while outputting database content: " + ex.getMessage());
                ex.printStackTrace();
            } catch (InterruptedException e) {
                break;
            }
        }
        
        if (log.isLoggable(Level.FINE)) log.fine("Exiting ExplorationProgressThread");
        // TODO: We can indicate that something went wrong here
        progress.complete(true);
    }

    public void stopThread() {
        stopFlag.set(true);
    }
}
