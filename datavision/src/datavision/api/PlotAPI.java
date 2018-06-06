package datavision.api;

import com.sun.javafx.charts.Legend;
import datavision.algorithms.DataSet;
import datavision.core.AppData;
import datavision.core.AppUI;
import datavision.lib.data.DataProcessor;
import datavision.utils.settings.DataVisionSettings;
import datavision.utils.settings.PlotAPISettings;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import vision.core.ApplicationTemplate;
import vision.core.Data;
import vision.core.UITemplate;
import vision.lib.ui.dialog.ErrorDialog;
import vision.lib.ui.hovernode.HoverNode;
import vision.utils.propertymanager.PropertyManager;
import vision.utils.settings.AppSettings;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PlotAPI {

    private UITemplate ui;
    private Data data;
    private Stage owner;
    private PropertyManager manager;

    public PlotAPI(UITemplate ui, Data data, Stage owner, PropertyManager manager) {
        this.ui = ui;
        this.data = data;
        this.owner = owner;
        this.manager = manager;
    }

    public void plotDataString(String dataString) {
        AppData datacomponent = (AppData)data;
        AppUI uicomponent = (AppUI)ui;
        datacomponent.clear();
        uicomponent.clearChart();

        DataProcessor.DataSet dataSet;
        try {
            dataSet = datacomponent.getProcessedDataString(dataString);
        } catch (Exception e) {
            handleInvalidData();
            return;
        }

        Map<String, String> dataLabels = dataSet.getLabels();
        Map<String, Point2D> dataPoints = dataSet.getPoints();
        plot(dataLabels, dataPoints);
    }

    public void drawLine(int a, int b, int c) {
        AppUI uicomponent = (AppUI)ui;
        LineChart<Number, Number> chart = uicomponent.getChart();
        PropertyManager manager = PropertyManager.getManager();

        double[] ranges = ((AppData)data).getXRange();

        double pointOneX = ranges[0];
        double pointOneY = (-c - (a * pointOneX)) / b;
        double pointTwoX = ranges[1];
        double pointTwoY = (-c - (a * pointTwoX)) / b;

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        String INTERNAL_SERIES_NAME = manager.getPropertyValue(PlotAPISettings.INTERNAL_SERIES_NAME.name());
        series.setName(INTERNAL_SERIES_NAME);
        series.getData().add(new XYChart.Data<>(pointOneX, pointOneY));
        series.getData().add(new XYChart.Data<>(pointTwoX, pointTwoY));

        chart.getData().add(series);
        String INTERNAL_SERIES_LINE_CSS_NAME = manager.getPropertyValue(PlotAPISettings.INTERNAL_SERIES_LINE_CSS_NAME.name());
        chart.getData().get(chart.getData().size() - 1).getNode().getStyleClass().add(INTERNAL_SERIES_LINE_CSS_NAME);

        for(XYChart.Data data: chart.getData().get(chart.getData().size() - 1).getData()){
            data.getNode().setVisible(false);
        }

        Legend legend = (Legend)chart.lookup(manager.getPropertyValue(DataVisionSettings.CHART_LEGEND.name()));
        legend.getItems().remove(legend.getItems().size() - 1);
    }

    public void plotTextAreaData() {
        AppData datacomponent = (AppData)data;
        AppUI uicomponent = (AppUI)ui;
        datacomponent.clear();
        uicomponent.clearChart();

        DataProcessor.DataSet dataSet;
        try {
            dataSet = datacomponent.getProcessedTextAreaData();
        } catch (Exception e) {
            handleInvalidData();
            return;
        }

        Map<String, String> dataLabels = dataSet.getLabels();
        Map<String, Point2D> dataPoints = dataSet.getPoints();
        plot(dataLabels, dataPoints);
    }

    public void plotDataSet(DataSet dataSet) {
        AppData datacomponent = (AppData)data;
        AppUI uicomponent = (AppUI)ui;
        datacomponent.clear();
        uicomponent.clearChart();

        Map<String, String> dataLabels = dataSet.getLabels();
        Map<String, Point2D> dataPoints = dataSet.getPoints();
        plot(dataLabels, dataPoints);
    }

    private void plot(Map<String, String> dataLabels, Map<String, Point2D> dataPoints) {
        if (dataLabels.isEmpty() || dataPoints.isEmpty()){
            return;
        }

        AtomicInteger numOfPoints = new AtomicInteger(0);
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(point.getX(), point.getY());
                series.getData().add(dataPoint);
                dataPoint.setNode(new HoverNode(entry.getKey().substring(1), manager));
                numOfPoints.getAndIncrement();
            });
            ((AppUI)ui).getChart().getData().add(series);
        }
    }

    private void handleInvalidData() {
        AppUI uicomponent = (AppUI)ui;
        uicomponent.clear();

        String          errTitle = manager.getPropertyValue(AppSettings.LOAD_ERROR_TITLE.name());
        String          errMsg   = manager.getPropertyValue(AppSettings.LOAD_ERROR_MSG.name());
        ErrorDialog dialog   = new ErrorDialog(owner, errTitle, errMsg);
        dialog.showAndWait();
    }

}
