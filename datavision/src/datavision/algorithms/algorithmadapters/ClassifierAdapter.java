package datavision.algorithms.algorithmadapters;

import datavision.algorithms.DataSet;
import datavision.algorithms.classifier.Classifier;
import datavision.api.AlgorithmAPI;
import datavision.algorithms.algorithmconfig.AlgorithmConfigurationDialog;
import datavision.algorithms.algorithmconfig.ClassifierConfigurationDialog;
import datavision.api.DataAPI;
import datavision.api.PlotAPI;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ClassifierAdapter extends AlgorithmAdapter {

    private List<Integer> output;

    public ClassifierAdapter(PlotAPI plotAPI, DataAPI dataAPI) {
        super(plotAPI, dataAPI);
        output = null;
    }

    public void createAlgorithm(String algorithmType, String algorithmName, DataSet dataSet, AlgorithmConfigurationDialog algorithmConfigurationDialog) {
        ClassifierConfigurationDialog classifierConfigurationDialog = (ClassifierConfigurationDialog)algorithmConfigurationDialog;
        int maxIterations = classifierConfigurationDialog.getMaxIterations();
        int updateInterval = classifierConfigurationDialog.getUpdateInterval();

        try {
            Class<?> klass = AlgorithmAPI.getAlgorithmRawClass(algorithmType, algorithmName);
            Constructor<?> constructor = klass.getConstructor(DataSet.class, int.class, int.class);
            algorithm = (Classifier)constructor.newInstance(dataSet, maxIterations, updateInterval);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            // FIXME
        }
    }

    public void plot() {
        int a = output.get(0);
        int b = output.get(1);
        int c = output.get(2);
        plotAPI.drawLine(a, b, c);
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
        output = ((Classifier)algorithm).getOutput();
    }

    @Override
    public boolean shouldPlotTextAreaData() {
        return true;
    }

    @Override
    public boolean isValidForDataSet() {
        return (dataAPI.getNumLabels() == 2);
    }

    public boolean toContinue() {
        return algorithm.canContinue();
    }

}
