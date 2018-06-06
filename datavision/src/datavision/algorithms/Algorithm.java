package datavision.algorithms;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Algorithm implements Runnable {

    protected int maxIterations;
    protected int updateInterval;
    protected boolean shouldRunContinuously;
    protected AtomicBoolean toContinue;

    protected int currentIteration;

    public int getMaxIterations() {
        return maxIterations;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public boolean getShouldRunContinuously() {
        return shouldRunContinuously;
    }

    public boolean tocontinue() { return toContinue.get(); }

    public boolean canContinue() {
        return toContinue.get();
    }

    public abstract void runOnce();

    public abstract void runUpdateInterval();

    public abstract void run();

}
