package org.opencompare.explore;

import org.opencompare.explorable.ThreadControllExplorable;

public class TerminatingThread extends Thread {

    private final int threadsCount;
    private final int checkInterval;
    private final ExplorationProgressThread progress;

    private int lastExploringCount = 0;

    public TerminatingThread(int threadsCount, int checkInterval, ExplorationProgressThread progress) {
        this.threadsCount = threadsCount;
        this.checkInterval = checkInterval;
        this.progress = progress;
    }

    public void run() {
        while (!isInterrupted()) {
            try {
                Thread.sleep(checkInterval);
                int ec = ExploringThread.exploringCount.get();
                if (ec == lastExploringCount) {
                    if (ec == 0) {
                        // Seems nobody explores anything at the moment --
                        // terminate the threads by queuing
                        // ThreadControllExplorables. Then terminate itself.
                        progress.stopThread();
                        for (int i = 0; i < threadsCount; ++i) {
                            ExplorationQueue.getInstance().add(new ThreadControllExplorable());
                        }
                        break;
                    }
                }
                lastExploringCount = ec;
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("Exiting TerminatingThread");
    }
}
