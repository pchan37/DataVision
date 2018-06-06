package datavision.algorithms.clusterer;

import datavision.algorithms.DataSet;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends Clusterer {

    private List<Point2D> centroids;
    private AtomicBoolean hasInitialized;

    public KMeansClusterer(DataSet dataSet, int maxIterations, int updateInterval, int numberOfClusters) {
        super(numberOfClusters);
        this.dataSet = dataSet;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.toContinue = new AtomicBoolean(true);

        this.currentIteration = 0;
        this.hasInitialized = new AtomicBoolean(false);
    }

    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return toContinue.get(); }

    @Override
    public void runOnce() {
        if (!this.hasInitialized.get()) {
            initializeCentroids();
        }
        if (currentIteration < maxIterations && toContinue.get() && this.hasInitialized.get()) {
            assignLabels();
            recomputeCentroids();
            currentIteration++;

            if (currentIteration >= maxIterations) {
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
        while (currentIteration++ < maxIterations && toContinue.get()) {
            runOnce();
        }
    }

    private void initializeCentroids() {
        Set<String>  chosen        = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataSet.getLabels().keySet());
        Random       r             = new Random();
        while (chosen.size() < numberOfClusters) {
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i)))
                i = (++i % instanceNames.size());
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataSet.getPoints().get(name)).collect(Collectors.toList());
        hasInitialized.set(true);
    }

    private void assignLabels() {
        dataSet.getPoints().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataSet.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {
        toContinue.set(false);
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataSet.getLabels()
                    .entrySet()
                    .stream()
                    .filter(entry -> i == Integer.parseInt(entry.getValue()))
                    .map(entry -> dataSet.getPoints().get(entry.getKey()))
                    .reduce(new Point2D(0, 0), (p, q) -> {
                        clusterSize.incrementAndGet();
                        return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                    });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                toContinue.set(true);
            }
        });
    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

    @Override
    public DataSet getOutput() {
        return dataSet;
    }
}