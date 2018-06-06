package datavision.core;

import datavision.algorithms.DataSet;
import datavision.api.PlotAPI;
import datavision.lib.data.DataProcessor;
import datavision.utils.settings.DataVisionSettings;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import vision.core.ApplicationTemplate;
import vision.core.Data;
import vision.lib.utils.FileOperationSuccess;
import vision.utils.propertymanager.PropertyManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class AppData extends Data {

    private DataProcessor dataProcessor;

    final PropertyManager manager;
    final SimpleStringProperty textAreaText;

    public AppData(PropertyManager manager) {
        this.dataProcessor = new DataProcessor();

        this.manager = manager;
        this.textAreaText = new SimpleStringProperty();
    }

    @Override
    public void loadFromString(String dataString) {
        textAreaText.set(dataString);
    }

    @Override
    public FileOperationSuccess loadFromFile(Path filePath) throws IOException {
        if (filePath == null) {
            return new FileOperationSuccess(false);
        }
        String data = new String(Files.readAllBytes(filePath));
        textAreaText.set(data);
        return new FileOperationSuccess(true);
    }

    @Override
    public FileOperationSuccess saveToFile(Path filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(filePath))) {
            writer.write(textAreaText.get());
            writer.close();
            return new FileOperationSuccess(true);
        }
    }

    public FileOperationSuccess saveToFile(Path filePath, String dataString) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(filePath))) {
            writer.write(dataString);
            writer.close();
            return new FileOperationSuccess(true);
        }
    }

    @Override
    public void clear() {
        dataProcessor.clear();
    }

    public DataProcessor.DataSet getProcessedDataString(String dataString) throws Exception {
        dataProcessor.processString(dataString);
        return dataProcessor.getProcessedData();
    }

    public DataProcessor.DataSet getProcessedTextAreaData() throws Exception {
        dataProcessor.processString(textAreaText.get());
        return dataProcessor.getProcessedData();
    }

    @Override
    public DataValidity checkData(String dataString) {
        return checkData(dataString, false);
    }

    public DataValidity checkData(String dataString, boolean strict) {
        String linesNumWithInvalidData = ensureDataFormatIsValid(dataString, strict);
        String linesWithDuplicateNames = (linesNumWithInvalidData.isEmpty()) ? ensureNoDuplicateNames(dataString) : "";

        if (!linesNumWithInvalidData.isEmpty()) {
            StringBuilder errMsg = new StringBuilder();
            errMsg.append(manager.getPropertyValue(DataVisionSettings.INVALID_DATA_FORMAT_MSG.name()));
            errMsg.append(linesNumWithInvalidData);
            return new DataValidity(false, errMsg.toString());
        }
        if (!linesWithDuplicateNames.isEmpty()) {
            StringBuilder errMsg = new StringBuilder();
            errMsg.append(manager.getPropertyValue(DataVisionSettings.DUPLICATE_NAMES_FORMAT_MSG.name()));
            errMsg.append(linesWithDuplicateNames);
            return new DataValidity(false, errMsg.toString());
        }
        return new DataValidity(true, null);
    }

    public DataValidity strictCheckDataFromTextArea() {
        return checkData(textAreaText.getValue(), true);
    }

    public DataValidity checkDataFromTextArea() {
        return checkData(textAreaText.getValue(), false);
    }


    public DataSet generateDataSetFromDataString(String dataString) {
        DataSet dataSet = new DataSet();
        Arrays.asList(dataString.split("\n")).forEach(line -> {
            try {
                dataSet.addInstance(line);
            } catch (DataSet.InvalidDataNameException e) {
                e.printStackTrace();
            }
        });
        return dataSet;
    }

    public DataSet generateDataSetFromTextArea() {
        DataSet dataSet = new DataSet();
        Arrays.asList(textAreaText.get().split("\n")).forEach(line -> {
            try {
                dataSet.addInstance(line);
            } catch (DataSet.InvalidDataNameException e) {
                e.printStackTrace();
            }
        });
        return dataSet;
    }

    public String getMetaData() {
        StringBuilder metaData = new StringBuilder();
        String[] labels = getLabels();
        int numLabels = labels.length;
        int numLines = textAreaText.get().split("\n").length;
        String metaDataFormatString = manager.getPropertyValue(DataVisionSettings.META_DATA_NO_FILE_FORMAT_STRING.name());
        metaData.append(String.format(metaDataFormatString, numLines, numLabels));
        for(String label: labels) {
            metaData.append(String.format("\n - %s", label));
        }
        return metaData.toString();
    }

    public String getMetaData(String loadPath) {
        StringBuilder metaData = new StringBuilder();
        String[] labels = getLabels();
        int numLabels = labels.length;
        int numLines = textAreaText.get().split("\n").length;
        String metaDataFormatString = manager.getPropertyValue(DataVisionSettings.META_DATA_WITH_FILE_FORMAT_STRING.name());
        metaData.append(String.format(metaDataFormatString, numLines, numLabels, loadPath));
        for(String label: labels) {
            metaData.append(String.format("\n - %s", label));
        }
        return metaData.toString();
    }

    private String[] getLabels() {
        String GET_LABEL_REGEX = "(^@.+\\t(.+)\\t(0|([1-9][0-9]*)|((0|[1-9][0-9]*)?.[0-9]+)),(0|([1-9][0-9]*)|((0|[1-9][0-9]*)?.[0-9]+))$\r*\n*)";

        String data = textAreaText.get();
        Set<String> labels = new HashSet<>();
        Pattern pattern = Pattern.compile(GET_LABEL_REGEX, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(data);

        String match;
        while (matcher.find()) {
            if (!(match = matcher.group(2)).equals("null")) {
                labels.add(match);
            }
        }
        return labels.toArray(new String[labels.size()]);
    }

    public int getNumLabels() {
        return getLabels().length;
    }

    public int getTotalPoints() { return textAreaText.get().split("\n").length; }

    public double[] getXRange() {
        List<Double> xValues = new ArrayList<>();
        DataSet dataSet = generateDataSetFromTextArea();
        Map<String, Point2D> points = dataSet.getPoints();
        points.values().forEach(point2D -> xValues.add(point2D.getX()));
        return new double[]{Collections.min(xValues), Collections.max(xValues)};
    }

    private String ensureDataFormatIsValid(String dataString, boolean strict) {
        String[] linesOfData = dataString.split("\n");
        String VALID_DATA_REGEX;
        if (strict) {
            VALID_DATA_REGEX = "(^@.+\\t.+\\t.+$\r*\n*)";
        } else {
            VALID_DATA_REGEX = "(^@.+\\t.+\\t(0|([1-9][0-9]*)|((0|[1-9][0-9]*)?.[0-9]+)),(0|([1-9][0-9]*)|((0|[1-9][0-9]*)?.[0-9]+))$\r*\n*)+";
        }
        Pattern pattern = Pattern.compile(VALID_DATA_REGEX);
        StringBuilder linesNumWithInvalidData = new StringBuilder();
        for(int i = 1; i <= linesOfData.length; i++) {
            Matcher matcher = pattern.matcher(linesOfData[i - 1]);
            if (!matcher.matches()) {
                linesNumWithInvalidData = (linesNumWithInvalidData.length() == 0) ? linesNumWithInvalidData.append(i) : linesNumWithInvalidData.append(", ").append(i);
            }
        }
        return linesNumWithInvalidData.toString();
    }

    private String ensureNoDuplicateNames(String dataString) {
        ArrayList<String> listOfNames = new ArrayList<>();
        String VALID_NAME_REGEX = "(^(@.+)\\t.+\\t(0|([1-9][0-9]*)|((0|[1-9][0-9]*)?.[0-9]+)),(0|([1-9][0-9]*)|((0|[1-9][0-9]*)?.[0-9]+))$\r*\n*)";
        Pattern pattern = Pattern.compile(VALID_NAME_REGEX, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(dataString);
        while (matcher.find()) {
            listOfNames.add(matcher.group(2).substring(1));
        }
        Map<String, Long> counts = listOfNames.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        StringBuilder duplicateNames = new StringBuilder();
        for (Map.Entry<String, Long> entry: counts.entrySet()) {
            if (entry.getValue() > 1) {
                String key = entry.getKey();
                duplicateNames = (duplicateNames.length() == 0) ? duplicateNames.append(key): duplicateNames.append(", ").append(key);
            }
        }
        return duplicateNames.toString();
    }

}
