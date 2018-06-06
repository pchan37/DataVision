package datavision.algorithms.clusterer;

import datavision.algorithms.DataSet;

import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer {

    public RandomClusterer(DataSet dataSet, int maxIterations, int updateInterval, int numberOfClusters) {
        super(numberOfClusters);
        this.dataSet = dataSet;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.toContinue = new AtomicBoolean(true);

        this.currentIteration = 0;
    }

    @Override
    public void runOnce() {
        if (currentIteration < maxIterations && toContinue.get()) {
            currentIteration++;
            assignLabels();

            if (currentIteration == maxIterations) {
                toContinue.set(false);
            }
        }
    }

    @Override
    public void runUpdateInterval() {
        for(int i = 0; i < updateInterval && currentIteration < maxIterations && toContinue.get(); i++) {
            runOnce();
        }
    }

    @Override
    public void run() {
        currentIteration = 0;
        while (currentIteration < maxIterations && toContinue.get()) {
            runOnce();
        }
    }

    private void assignLabels() {
        dataSet.getPoints().forEach((instanceName, location) -> {
            dataSet.getLabels().put(instanceName, getRandomLabel());
        });
    }

    private String getRandomLabel() {
        int label = (int)(numberOfClusters * Math.random());
        return Integer.toString(label);
    }

}
