package vision.lib.ui.hovernode;

import datavision.utils.settings.DataVisionSettings;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import vision.utils.propertymanager.PropertyManager;

/**
 *
 */
public class HoverNode extends StackPane {

    /** The label that pops up to show extra information when hovering over the hover node */
    private Label label;

    /**
     * Creates an instance of the HoverNode class that takes a {@link String} parameter for the data that
     * would be displayed when hovering over the node and a {@link PropertyManager} parameter for access
     * to string values that would otherwise be hardcoded.
     *
     * @param value The data to be displayed when the user hovers over the {@link HoverNode}
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     */
    public HoverNode(String value, PropertyManager manager) {
        createLabel(value, manager);
        setOnMouseEnteredEvent();
        setOnMouseExitedEvent();
    }

    /**
     * Creates the label that would pop up when the user hovers over the {@link HoverNode}
     *
     * @param value The data to be displayed when the user hovers over the {@link HoverNode}
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     */
    private void createLabel(String value, PropertyManager manager) {
        label = new Label(value);

        String chartLineSymbol = manager.getPropertyValue(DataVisionSettings.CHART_LINE_SYMBOL.name());
        String chartSeriesLine = manager.getPropertyValue(DataVisionSettings.CHART_SERIES_LINE.name());
        String chartSeriesColor = manager.getPropertyValue(DataVisionSettings.CHART_SERIES_COLOR.name());
        String chartFontStyle = manager.getPropertyValue(DataVisionSettings.CHART_FONT_STYLE.name());

        label.getStyleClass().addAll(chartLineSymbol, chartSeriesLine, chartSeriesColor);
        label.setStyle(chartFontStyle);
        label.setTranslateY(30);

        label.setTextFill(Color.FORESTGREEN);
        label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
    }

    /**
     * An API to change the behavior of what happens when the mouse enters the {@link HoverNode} area
     */
    protected void setOnMouseEnteredEvent() {
        setOnMouseEntered(mouseEvent -> {
            getChildren().setAll(label);
            setCursor(Cursor.NONE);
            toFront();
        });
    }

    /**
     * An API to change the behavior of what happens when the mouse exits the {@link HoverNode} area
     */
    protected void setOnMouseExitedEvent() {
        setOnMouseExited(mouseEvent -> {
            getChildren().clear();
            setCursor(Cursor.DEFAULT);
        });
    }

}