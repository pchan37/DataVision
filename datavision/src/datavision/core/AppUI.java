package datavision.core;

import datavision.api.DataAPI;
import datavision.api.PlotAPI;
import datavision.lib.ui.algorithmselection.AlgorithmSelectionSystem;
import datavision.utils.settings.AppUISettings;
import datavision.utils.settings.DataVisionSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vision.core.UITemplate;
import vision.lib.exceptions.InvalidDataFormatException;
import vision.lib.exceptions.LoadErrorException;
import vision.lib.exceptions.SaveErrorException;
import vision.lib.ui.dialog.ConfirmationDialog;
import vision.lib.ui.dialog.ErrorDialog;
import vision.utils.propertymanager.PropertyManager;
import vision.utils.settings.AppSettings;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public final class AppUI extends UITemplate {

    private final Stage owner;
    private LineChart<Number, Number> chart;
    private TextArea textArea;
    private Label metaDataLabel;
    private AlgorithmSelectionSystem algorithmSelectionSystem;
    private PlotAPI plotAPI;
    private final PropertyManager manager;

    AppUI(Stage primaryStage, PropertyManager manager) {
        super(primaryStage, manager);
        this.owner = primaryStage;
        this.data = new AppData(manager);
        this.action = new AppAction(this, data, manager);
        this.plotAPI = new PlotAPI(this, data, owner, manager);
        this.manager = manager;

        setToolbar(manager);
    }

    @Override
    protected void setToolbar(PropertyManager manager) {
        super.setToolbar(manager);
        Button screenshotButton = makeButton(manager,
                DataVisionSettings.SCREENSHOT_ICON.name(),
                DataVisionSettings.SCREENSHOT_TOOLTIP.name(), false);
        toolbar.getItems().remove(buttonStorage.get(AppSettings.PRINT_ICON.name()));
        toolbar.getItems().add(screenshotButton);
    }

    @Override
    protected void setToolbarHandlers(PropertyManager manager) {
        Button newButton = buttonStorage.get(AppSettings.NEW_ICON.name());
        newButton.setOnAction(e -> action.handleNewRequest());
        Button loadButton = buttonStorage.get(AppSettings.LOAD_ICON.name());
        loadButton.setOnAction(e -> {
            try {
                action.handleLoadRequest();
            } catch (InvalidDataFormatException ex) {
                handleInvalidDataFormat(ex);
            } catch (LoadErrorException ex) {
                handleLoadError(ex);
            }
        });
        Button saveButton = buttonStorage.get(AppSettings.SAVE_ICON.name());
        saveButton.setOnAction(e -> {
            try {
                action.handleSaveRequest();
            } catch (InvalidDataFormatException ex) {
                handleInvalidDataFormat(ex);
            } catch (SaveErrorException ex) {
                handleSaveError(ex);
            }
        });
        Button exitButton = buttonStorage.get(AppSettings.EXIT_ICON.name());
        exitButton.setOnAction(e -> {
            try {
                action.handleExitRequest();
            } catch (InvalidDataFormatException ex) {
                handleInvalidDataFormat(ex);
            } catch (SaveErrorException ex) {
                handleSaveError(ex);
            }
        });
        Button screenshotButton = buttonStorage.get(DataVisionSettings.SCREENSHOT_ICON.name());
        screenshotButton.setOnAction(e -> {
            try {
                ((AppAction) action).handleScreenshotRequest();
            } catch (SaveErrorException ex) {
                handleSaveError(ex);
            }
        });
    }

    @Override
    public void initialize() {
        setCSS(manager);
        layout();
    }

    @Override
    public void setCSS(PropertyManager manager) {
        super.setCSS(manager);
        addCSSSheet(String.join(manager.getPropertyValue(AppSettings.SEPARATOR.name()),
                manager.getPropertyValue(DataVisionSettings.MAIN_CSS_RESOURCE_PATH.name())));
    }

    @Override
    public void clear() {
        textArea.clear();
        textArea.setDisable(false);
        String edit = manager.getPropertyValue(DataVisionSettings.EDIT_BUTTON_TEXT.name());
        buttonStorage.get(DataVisionSettings.EDIT_DONE_TOGGLE_BUTTON.name()).setText(edit);
        clearChart();
    }

    public void clearChart() {
        chart.getData().clear();
        ((AppAction)action).chartNotHasData.set(true);
    }

    private void layout() {
        workspace = new HBox();
        HBox.setHgrow(workspace, Priority.ALWAYS);

        renderLeftPanel();
        renderRightPanel();

        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);
    }

    private void renderLeftPanel() {
        VBox leftPanel = new VBox(8);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));
        VBox.setVgrow(leftPanel, Priority.ALWAYS);
        int windowWidth = manager.getPropertyValueAsInt(AppSettings.WINDOW_WIDTH.name());
        int windowHeight = manager.getPropertyValueAsInt(AppSettings.WINDOW_HEIGHT.name());
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight);
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight);

        algorithmSelectionSystem = new AlgorithmSelectionSystem(owner, this, action, new DataAPI(data), leftPanel, manager);

        renderTextArea(leftPanel);
        renderEditDoneToggleButton(leftPanel);
        renderMetaDataLabel(leftPanel);

        setTextAreaVisible(false);
        setEditDoneToggleButtonVisible(false);
        setMetaDataLabelVisible(false);

        workspace.getChildren().add(leftPanel);
    }

    private void renderTextArea(Pane pane) {
        textArea = new TextArea();
        textArea.managedProperty().bindBidirectional(textArea.visibleProperty());
        textArea.setPrefRowCount(10);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            textArea.setPrefRowCount(10);

            if (!oldValue.equals(newValue) && !newValue.isEmpty()) {
                ((AppAction)action).isUnsaved.set(true);
            } else {
                ((AppAction)action).isUnsaved.set(false);
            }
        });
        ((AppAction)action).setUpDataSync(textArea);

        pane.getChildren().add(textArea);
    }

    private void renderEditDoneToggleButton(Pane pane) {
        Button editDoneToggleButton = new Button();
        editDoneToggleButton.setOnAction(e -> {
            try {
                ((AppAction) action).handleEditDoneRequest(e);
            } catch (InvalidDataFormatException ex) {
                handleInvalidDataFormat(ex);
            }
        });
        editDoneToggleButton.managedProperty().bindBidirectional(editDoneToggleButton.visibleProperty());
        buttonStorage.put(DataVisionSettings.EDIT_DONE_TOGGLE_BUTTON.name(), editDoneToggleButton);

        pane.getChildren().add(editDoneToggleButton);
    }

    private void renderMetaDataLabel(Pane pane) {
        VBox labelBox = new VBox(8);
        labelBox.setAlignment(Pos.TOP_LEFT);
        metaDataLabel = new Label();
        metaDataLabel.setWrapText(true);
        metaDataLabel.setMinHeight(Region.USE_PREF_SIZE);
        metaDataLabel.managedProperty().bindBidirectional(metaDataLabel.visibleProperty());
        labelBox.getChildren().add(metaDataLabel);
        labelBox.managedProperty().bindBidirectional(labelBox.visibleProperty());
        labelBox.managedProperty().bindBidirectional(metaDataLabel.visibleProperty());

        pane.getChildren().add(labelBox);
    }

    private void renderRightPanel() {
        Label chartTitle = new Label(manager.getPropertyValue(DataVisionSettings.CHART_TITLE.name()));
        chartTitle.managedProperty().bindBidirectional(chartTitle.visibleProperty());

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
        xAxis.setForceZeroInRange(false);
        yAxis.setForceZeroInRange(false);
        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setTitle(manager.getPropertyValue(DataVisionSettings.CHART_TITLE.name()));
        chart.managedProperty().bindBidirectional(chart.visibleProperty());
        chartTitle.visibleProperty().bind(chart.visibleProperty().not());
        setChartVisible(false);

        VBox rightPanel = new VBox(8);
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.getChildren().addAll(chartTitle, chart);

        int windowWidth = manager.getPropertyValueAsInt(AppSettings.WINDOW_WIDTH.name());
        int windowHeight = manager.getPropertyValueAsInt(AppSettings.WINDOW_HEIGHT.name());
        rightPanel.setMaxSize(windowWidth * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth * 0.69, windowHeight * 0.69);

        workspace.getChildren().add(rightPanel);
    }

    private void handleLoadError(LoadErrorException e) {
        String path = e.getPath() == null ? "" : e.getPath().toString();
        String loadErrorTitle = manager.getPropertyValue(AppSettings.LOAD_ERROR_TITLE.name());
        String loadErrorMsg = manager.getPropertyValue(AppSettings.LOAD_ERROR_MSG.name()) + path;
        if (!(e.getReason() == null) && !e.getReason().isEmpty()) {
            String loadErrorMsgFormat = manager.getPropertyValue(AppUISettings.LOAD_ERROR_MSG_FORMAT.name());
            loadErrorMsg = String.format(loadErrorMsgFormat, loadErrorMsg, e.getReason());
        }
        ErrorDialog dialog = new ErrorDialog(owner, loadErrorTitle, loadErrorMsg);
        dialog.showAndWait();
    }

    private void handleSaveError(SaveErrorException e) {
        String path = e.getPath() == null ? "" : e.getPath().toString();
        String saveErrorTitle = manager.getPropertyValue(AppSettings.SAVE_ERROR_TITLE.name());
        String saveErrorMsg = manager.getPropertyValue(AppSettings.SAVE_ERROR_MSG.name()) + path;
        if (!(e.getReason() == null) && !e.getReason().isEmpty()) {
            String saveErrorMsgFormat = manager.getPropertyValue(AppUISettings.SAVE_ERROR_MSG_FORMAT.name());
            saveErrorMsg = String.format(saveErrorMsgFormat, saveErrorMsg, e.getReason());
        }
        ErrorDialog dialog = new ErrorDialog(owner, saveErrorTitle, saveErrorMsg);
        dialog.showAndWait();
    }

    private void handleInvalidDataFormat(InvalidDataFormatException e) {
        String invalidDataFormatErrorTitle = manager.getPropertyValue(DataVisionSettings.INVALID_DATA_TITLE.name());
        String invalidDataFormatErrorMsg = e.getReason();
        ErrorDialog dialog = new ErrorDialog(owner, invalidDataFormatErrorTitle, invalidDataFormatErrorMsg);
        dialog.showAndWait();
    }

    public void handleInterruptedException() {
        clear();

        String errTitle = manager.getPropertyValue(AppUISettings.ALGORITHM_INTERRUPTED_TITLE.name());
        String errMsg = manager.getPropertyValue(AppUISettings.ALGORITHM_INTERRUPTED_MESSAGE.name());
        ErrorDialog errorDialog = new ErrorDialog(owner, errTitle, errMsg);
        errorDialog.showAndWait();
    }

    public ConfirmationDialog.Option getUserChoice(String title, String message) {

        ConfirmationDialog confirmationDialog = new ConfirmationDialog(owner, title, message);
        confirmationDialog.showAndWait();
        return confirmationDialog.getSelectedOption();
    }

    public Path getLoadFile(String description, String extension) {
        FileChooser fileChooser = new FileChooser();
        String dataDirectory = manager.getPropertyValue(AppSettings.SEPARATOR.name()) +
                manager.getPropertyValue(DataVisionSettings.DATA_RESOURCE_PATH.name());
        URL dataDirectoryURL = getClass().getResource(dataDirectory);
        File dataDirectoryFile;
        if (dataDirectoryURL == null) {
            dataDirectoryFile = new File(System.getProperty("user.home"));
        } else {
            dataDirectoryFile = new File(dataDirectoryURL.getFile());
        }

        fileChooser.setInitialDirectory(dataDirectoryFile);
        fileChooser.setTitle(manager.getPropertyValue(AppSettings.LOAD_WORK_TITLE.name()));

        String fileExtDescFormat = manager.getPropertyValue(DataVisionSettings.FILE_EXT_AND_DESC_FORMAT.name());
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(String.format(fileExtDescFormat, description, extension),
                String.format(manager.getPropertyValue(DataVisionSettings.FILE_EXT_FORMAT.name()), extension));

        fileChooser.getExtensionFilters().add(extensionFilter);
        File selectedFile = fileChooser.showOpenDialog(owner);
        return selectedFile == null ? null : selectedFile.toPath();
    }

    public Path getSaveFile(String description, String extension) {
        FileChooser fileChooser = new FileChooser();
        String dataDirectory = manager.getPropertyValue(AppSettings.SEPARATOR.name()) +
                manager.getPropertyValue(DataVisionSettings.DATA_RESOURCE_PATH.name());
        URL dataDirectoryURL = getClass().getResource(dataDirectory);
        File dataDirectoryFile;
        if (dataDirectoryURL == null) {
            dataDirectoryFile = new File(System.getProperty("user.home"));
        } else {
            dataDirectoryFile = new File(dataDirectoryURL.getFile());
        }

        fileChooser.setInitialDirectory(dataDirectoryFile);
        fileChooser.setTitle(manager.getPropertyValue(AppSettings.SAVE_WORK_TITLE.name()));

        String fileExtDescFormat = manager.getPropertyValue(DataVisionSettings.FILE_EXT_AND_DESC_FORMAT.name());
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(String.format(fileExtDescFormat, description, extension),
                String.format(manager.getPropertyValue(DataVisionSettings.FILE_EXT_FORMAT.name()), extension));

        fileChooser.getExtensionFilters().add(extensionFilter);
        File selectedFile = fileChooser.showSaveDialog(owner);
        if (selectedFile == null) {
            return null;
        } else if (selectedFile.toString().endsWith(extension)) {
            return selectedFile.toPath();
        } else {
            return new File(selectedFile.toString() + extension).toPath();
        }
    }

    public void setChartVisible(boolean isVisible) {
        chart.setVisible(isVisible);
    }

    public void setTextAreaVisible(boolean isVisible) {
        textArea.setVisible(isVisible);
    }

    public void setEditDoneToggleButtonVisible(boolean isVisible) {
        Button editDoneToggleButton = buttonStorage.get(DataVisionSettings.EDIT_DONE_TOGGLE_BUTTON.name());
        editDoneToggleButton.setVisible(isVisible);
    }

    public void setMetaDataLabelVisible(boolean isVisible) {
        metaDataLabel.setVisible(isVisible);
    }

    public void setTextAreaDisabledState(boolean isDisabled){
        textArea.setDisable(isDisabled);
    }

    public void updateMetaDataLabel(String newValue) {
        metaDataLabel.setText(newValue);
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    public AlgorithmSelectionSystem getAlgorithmSelectionSystem() {
        return algorithmSelectionSystem;
    }

    public Map<String, Button> getButtonStorage() {
        return buttonStorage;
    }

    public PlotAPI getPlotAPI() {
        return plotAPI;
    }

}