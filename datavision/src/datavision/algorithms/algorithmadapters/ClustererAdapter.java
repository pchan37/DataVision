package datavision.algorithms.algorithmadapters;

import datavision.algorithms.DataSet;
import datavision.algorithms.algorithmconfig.ClustererConfigurationDialog;
import datavision.algorithms.clusterer.Clusterer;
import datavision.api.AlgorithmAPI;
import datavision.algorithms.algorithmconfig.AlgorithmConfigurationDialog;
import datavision.api.DataAPI;
import datavision.api.PlotAPI;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClustererAdapter extends AlgorithmAdapter {

    private DataSet output;

    public ClustererAdapter(PlotAPI plotAPI, DataAPI dataAPI) {
        super(plotAPI, dataAPI);
        output = null;
    }

    public void createAlgorithm(String algorithmType, String algorithmName, DataSet dataSet, AlgorithmConfigurationDialog algorithmConfigurationDialog) {
        ClustererConfigurationDialog clustererConfigurationDialog = (ClustererConfigurationDialog)algorithmConfigurationDialog;
        int maxIterations = clustererConfigurationDialog.getMaxIterations();
        int updateInterval = clustererConfigurationDialog.getUpdateInterval();
        int numberOfClusters = clustererConfigurationDialog.getNumberOfClusters();

        try {
            Class<?> klass = AlgorithmAPI.getAlgorithmRawClass(algorithmType, algorithmName);
            Constructor<?> constructor = klass.getConstructor(DataSet.class, int.class, int.class, int.class);
            algorithm = (Clusterer)constructor.newInstance(dataSet, maxIterations, updateInterval, numberOfClusters);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            // FIXME
        }
    }

    public void plot() {
        plotAPI.plotDataSet(output);
        output = null;
    }

    @Override
    protected boolean dataNotReady() {
        return output == null;
    }

    @Override
    protected boolean dataNotTaken() {
        return output != null;
    }

    @Override
    protected void setData() {
        output = ((Clusterer)algorithm).getOutput();
    }

    @Override
    public boolean shouldPlotTextAreaData() {
        return true;
    }

    @Override
    public boolean isValidForDataSet() {
        return dataAPI.getTotalPoints() > 1;
    }

    public boolean toContinue() {
        return algorithm.canContinue();
    }

}
