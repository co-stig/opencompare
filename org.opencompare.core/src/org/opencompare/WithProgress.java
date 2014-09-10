package org.opencompare;

public interface WithProgress {
    
    void start();
    void complete(boolean success);
    void setMaximum(int max);
    void setValue(int value);
    
    public final static String TEXT_FINISH = "Finish";
    public final static String TEXT_ABORT = "Abort";
    public final static String TEXT_START = "Start";
    
}
