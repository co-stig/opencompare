package org.opencompare.explore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

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
                System.out.println(TIMESTAMP_FORMAT.format(new Date()) + ": " + value);
                progress.setValue(value);
                Thread.sleep(outputInterval);
            } catch (ExplorationException ex) {
                System.out.println("Error while outputting database content: " + ex.getMessage());
                ex.printStackTrace();
            } catch (InterruptedException e) {
                break;
            }
        }
        
        System.out.println("Exiting ExplorationProgressThread");
        // TODO: We can indicate that something went wrong here
        progress.complete(true);
    }

    public void stopThread() {
        stopFlag.set(true);
    }
}
