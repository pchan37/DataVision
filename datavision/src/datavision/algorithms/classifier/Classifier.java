package datavision.algorithms.classifier;

import datavision.algorithms.Algorithm;

import java.util.List;

public abstract class Classifier extends Algorithm {

    protected List<Integer> output;

    public List<Integer> getOutput() {
        return output;
    }

}
