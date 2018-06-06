package datavision.lib.data;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import vision.utils.propertymanager.PropertyManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

// Todo: update the comment
/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class DataProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    public class DataSet {

        private Map<String, String> labels;
        private Map<String, Point2D> points;

        DataSet(Map<String, String> labels, Map<String, Point2D> points) {
            this.labels = labels;
            this.points = points;
        }

        public Map<String, String> getLabels() {
            return labels;
        }

        public Map<String, Point2D> getPoints() {
            return points;
        }

    }

    private final Map<String, String>  dataLabels;
    private final Map<String, Point2D> dataPoints;

    public DataProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {
                        String   name  = checkedname(list.get(0));
                        String   label = list.get(1);
                        String[] pair  = list.get(2).split(",");
                        Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                        dataLabels.put(name, label);
                        dataPoints.put(name, point);
                    } catch (Exception e) {
                        errorMessage.setLength(0);
                        errorMessage.append(e.getClass().getSimpleName()).append(", ").append(e.getMessage());
                        hadAnError.set(true);
                    }
                });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    /*
     * Returns the data labels and data points
     */
    public DataSet getProcessedData() {
        return new DataSet(dataLabels, dataPoints);
    }

    public void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }
}