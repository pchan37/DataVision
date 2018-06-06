package datavision.algorithms.classifier;

import datavision.algorithms.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.currentIteration = 0;
        this.toContinue = new AtomicBoolean(true);
    }

    @Override
    public void runOnce() {
        if (currentIteration < maxIterations && toContinue.get()) {
            currentIteration++;

            int xCoefficient = new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
            int yCoefficient = 10;
            int constant = RAND.nextInt(11);

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            if (currentIteration > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                toContinue.set(false);
            }

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

}
