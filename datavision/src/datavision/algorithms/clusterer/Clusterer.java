package datavision.algorithms.clusterer;

import datavision.algorithms.Algorithm;
import datavision.algorithms.DataSet;

public abstract class Clusterer extends Algorithm {

    protected DataSet dataSet;
    protected int numberOfClusters;

    public Clusterer(int numberOfClusters) {
        setNumOfClusters(numberOfClusters);
    }

    private void setNumOfClusters(int numberOfClusters) {
        if (numberOfClusters < 2)
            numberOfClusters = 2;
        else if (numberOfClusters > 4)
            numberOfClusters = 4;
        this.numberOfClusters = numberOfClusters;
    }

    public DataSet getOutput() {
        return dataSet;
    }

}
