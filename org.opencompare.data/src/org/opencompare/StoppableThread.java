package org.opencompare;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class StoppableThread extends Thread {

    private final AtomicBoolean stopFlag = new AtomicBoolean(false);

    public void stopThread() {
        stopFlag.set(true);
    }

    protected boolean isStopped() {
        return stopFlag.get();
    }
    
}
