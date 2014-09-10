package org.opencompare;


public class WithProgressAdapter implements WithProgress {
    
    private final WithProgress wrapped;
    private final int notifyEach;
    
    private int value = 0;
    private int lastNotificationValue = 0;

    public WithProgressAdapter(WithProgress wrapped, int notifyEach) {
        this.wrapped = wrapped;
        this.notifyEach = notifyEach;
    }

    public WithProgress getWrapped() {
        return wrapped;
    }

    public void start() {
        wrapped.start();
        value = 0;
        lastNotificationValue = 0;
    }

    public void complete(boolean success) {
        wrapped.complete(success);
    }

    public void setMaximum(int max) {
        wrapped.setMaximum(max);
    }

    public void setValue(int value) {
        wrapped.setValue(value);
        this.value = value;
        this.lastNotificationValue = value;
    }

    public void increment(int inc) {
        value += inc;
        if (value - lastNotificationValue >= notifyEach) {
            lastNotificationValue = value;
            wrapped.setValue(value);
        }
    }
    
    public int getValue() {
        return value;
    }

    public int getNotifyEach() {
        return notifyEach;
    }

}
