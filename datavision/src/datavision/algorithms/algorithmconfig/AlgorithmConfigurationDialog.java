package datavision.algorithms.algorithmconfig;

import datavision.api.DataAPI;
import datavision.utils.settings.AlgorithmConfigurationDialogSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vision.lib.exceptions.numericalexception.MustBeIntegerException;
import vision.lib.exceptions.numericalexception.MustBeNonNegativeException;
import vision.lib.exceptions.numericalexception.MustBeNonZeroException;
import vision.lib.exceptions.numericalexception.NotInRangeException;
import vision.lib.ui.dialog.ConfigurationDialog;
import vision.lib.ui.dialog.TextInputControlValidator;
import vision.lib.ui.dialog.ErrorDialog;
import vision.utils.propertymanager.PropertyManager;

public abstract class AlgorithmConfigurationDialog extends ConfigurationDialog {

    protected DataAPI dataAPI;

    protected VBox canvas;
    protected GridPane form;
    protected Button submit;
    protected boolean stillHasError;

    protected String maxIteration;
    protected String updateInterval;
    protected boolean runContinuously;

    public AlgorithmConfigurationDialog() {

    }

    public AlgorithmConfigurationDialog(Stage owner, DataAPI dataAPI, String title) {
        super(owner, title);
        this.dataAPI = dataAPI;
        maxIteration = "";
        updateInterval = "";
        runContinuously = false;
        init(owner);
    }

    @Override
    public void init(Stage owner) {
        super.init(owner);

        canvas = new VBox(8);
        canvas.setAlignment(Pos.CENTER);
        canvas.setPadding(new Insets(5));
        canvas.getChildren().add(title);

        renderAboveFormContent();
        renderFormContent();
        renderBelowFormContent();
        renderOkButton();

        addFormEventHandlers();
        addOkButtonEventHandler();

        this.setScene(new Scene(canvas));
        this.setWidth(800);
    }

    public void showDialog() {
        TextField maxIterationInput = (TextField)getChildAtColRow(1, 0, 3);
        TextField updateIntervalInput = (TextField)getChildAtColRow(1,1,3);
        CheckBox runContinuouslyInput = (CheckBox)getChildAtColRow(1,2,3);
        maxIterationInput.setText(maxIteration);
        updateIntervalInput.setText(updateInterval);
        runContinuouslyInput.setSelected(runContinuously);
        ((Label)form.getChildren().get(2)).setText("");
        ((Label)form.getChildren().get(5)).setText("");
        super.showAndWait();
    }

    public boolean isValid() {
        return !maxIteration.isEmpty() && !updateInterval.isEmpty();
    }

    protected void renderFormContent() {
        PropertyManager manager = PropertyManager.getManager();
        form = new GridPane();
        form.setHgap(10);
        form.setVgap(5);

        String MAX_ITERATION = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.MAX_ITERATION.name());
        Label maxIterationLabel = new Label(MAX_ITERATION);
        form.add(maxIterationLabel, 0, 0);
        TextInputControl maxIterationInput = new TextField();
        form.add(maxIterationInput, 1, 0);
        form.add(new Label(), 2, 0);

        String UPDATE_INTERVAL = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.UPDATE_INTERVAL.name());
        Label updateIntervalLabel = new Label(UPDATE_INTERVAL);
        form.add(updateIntervalLabel, 0, 1);
        TextInputControl updateIntervalInput = new TextField();
        form.add(updateIntervalInput, 1, 1);
        form.add(new Label(), 2, 1);

        String CONTINUOUS_RUN = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.CONTINUOUS_RUN.name());
        Label continuousRunLabel = new Label(CONTINUOUS_RUN);
        form.add(continuousRunLabel, 0, 2);
        CheckBox runContinuouslyInput = new CheckBox();
        form.add(runContinuouslyInput, 1, 2);
        form.add(new Label(), 2, 2);
        canvas.getChildren().add(form);
    }

    protected void renderAboveFormContent() {

    }

    protected void renderBelowFormContent() {

    }

    protected void renderOkButton() {
        PropertyManager manager = PropertyManager.getManager();
        String ALGORITHM_CONFIGURATION_DIALOG_OK = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.ALGORITHM_CONFIGURATION_DIALOG_OK.name());
        submit = new Button(ALGORITHM_CONFIGURATION_DIALOG_OK);
        canvas.getChildren().add(submit);
    }

    protected void addFormEventHandlers() {
        TextField maxIterationInput = (TextField)getChildAtColRow(1, 0, 3);
        TextField updateIntervalInput = (TextField)getChildAtColRow(1,1,3);
        CheckBox  runContinuouslyInput = (CheckBox)getChildAtColRow(1, 2, 3);
        maxIterationInput.focusedProperty().addListener((observable, previousFocusedValue, currentFocusedValue) -> {
            if (!currentFocusedValue) {
                validateInputIsPositiveIntegerAndHandle(maxIterationInput, 1, 2, 0, 3);
            } else {
                stillHasError = false;
            }
        });
        updateIntervalInput.focusedProperty().addListener((observable, previousFocusedValue, currentFocusedValue) -> {
            if (!currentFocusedValue) {
                validateInputIsPositiveIntegerAndHandle(updateIntervalInput, 1, 2, 1, 3);
            } else {
                stillHasError = false;
            }
        });
        runContinuouslyInput.focusedProperty().addListener(((observable, previousFocusedValue, currentFocusedValue) -> {
            stillHasError = false;
        }));
    }

    protected void addOkButtonEventHandler() {
        PropertyManager manager = PropertyManager.getManager();
        submit.setOnAction(e -> {
            TextField maxIterationInput = (TextField)getChildAtColRow(1, 0, 3);
            TextField updateIntervalInput = (TextField)getChildAtColRow(1,1,3);
            CheckBox runContinuouslyInput = (CheckBox)getChildAtColRow(1,2,3);
            maxIteration = maxIterationInput.getText();
            updateInterval = updateIntervalInput.getText();
            runContinuously = runContinuouslyInput.isSelected();
            if (maxIteration.isEmpty()) {
                maxIteration = "1";
                stillHasError = true;
            }
            if (updateInterval.isEmpty()) {
                updateInterval = "1";
                stillHasError = true;
            }
            if (stillHasError) {
                String INVALID_CONFIGURATION_TITLE = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.INVALID_CONFIGURATION_TITLE.name());
                String INVALID_CONFIGURATION_MESSAGE = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.INVALID_CONFIGURATION_MESSAGE.name());
                ErrorDialog dialog = new ErrorDialog(this, INVALID_CONFIGURATION_TITLE, INVALID_CONFIGURATION_MESSAGE);
                dialog.showAndWait();
                stillHasError = false;
            }
            this.close();
        });
    }

    protected Node getChildAtColRow(int col, int row, @SuppressWarnings("SameParameterValue") int numOfNodesPerRow) {
        return form.getChildren().get(row * numOfNodesPerRow + col);
    }

    // FIXME: have a general API for getting data from configuration dialogs instead of writing a method for each
    public int getMaxIterations() {
        return Integer.parseInt(maxIteration);
    }

    public int getUpdateInterval() {
        return Integer.parseInt(updateInterval);
    }

    public boolean getContinuousRun() {
        return runContinuously;
    }

    protected void validateInputIsPositiveIntegerAndHandle(TextInputControl input, int defaultValue, int col, int row, int numOfNodesPerRow) {
        PropertyManager manager = PropertyManager.getManager();
        Label errorLabel = (Label)getChildAtColRow(col, row, numOfNodesPerRow);
        try {
            TextInputControlValidator.validateInputIsPositiveInteger(input);
        } catch (MustBeIntegerException e) {
            String MUST_BE_INTEGER_FORMAT_STRING = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.MUST_BE_INTEGER_FORMAT_STRING.name());
            errorLabel.setText(String.format(MUST_BE_INTEGER_FORMAT_STRING, defaultValue));
            input.setText(Integer.toString(defaultValue));
            stillHasError = true;
        } catch (MustBeNonNegativeException e) {
            String MUST_BE_NON_NEGATIVE_FORMAT_STRING = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.MUST_BE_NON_NEGATIVE_FORMAT_STRING.name());
            errorLabel.setText(String.format(MUST_BE_NON_NEGATIVE_FORMAT_STRING, defaultValue));
            input.setText(Integer.toString(defaultValue));
            stillHasError = true;
        } catch (MustBeNonZeroException e) {
            String MUST_BE_NON_ZERO_FORMAT_STRING = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.MUST_BE_NON_ZERO_FORMAT_STRING.name());
            errorLabel.setText(String.format(MUST_BE_NON_ZERO_FORMAT_STRING, defaultValue));
            input.setText(Integer.toString(defaultValue));
            stillHasError = true;
        }
    }

    protected void validateInputIsPositiveIntegerAndWithinRangeAndHandle(TextInputControl input, int minValue, int maxValue, int col, int row, int numOfNodesPerRow) {
        PropertyManager manager = PropertyManager.getManager();
        Label errorLabel = (Label)getChildAtColRow(col, row, numOfNodesPerRow);
        try {
            TextInputControlValidator.validateInputIsPositiveIntegerAndWithinRange(input, minValue, maxValue);
        } catch (MustBeIntegerException e) {
            String MUST_BE_INTEGER_FORMAT_STRING = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.MUST_BE_INTEGER_FORMAT_STRING.name());
            errorLabel.setText(String.format(MUST_BE_INTEGER_FORMAT_STRING, minValue));
            input.setText(Integer.toString(minValue));
            stillHasError = true;
        } catch (MustBeNonNegativeException e) {
            String MUST_BE_NON_NEGATIVE_FORMAT_STRING = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.MUST_BE_NON_NEGATIVE_FORMAT_STRING.name());
            errorLabel.setText(String.format(MUST_BE_NON_NEGATIVE_FORMAT_STRING, minValue));
            input.setText(Integer.toString(minValue));
            stillHasError = true;
        } catch (MustBeNonZeroException e) {
            String MUST_BE_NON_ZERO_FORMAT_STRING = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.MUST_BE_NON_ZERO_FORMAT_STRING.name());
            errorLabel.setText(String.format(MUST_BE_NON_ZERO_FORMAT_STRING, minValue));
            input.setText(Integer.toString(minValue));
            stillHasError = true;
        } catch (NotInRangeException e) {
            String MUST_BE_WITHIN_RANGE_FORMAT_STRING = manager.getPropertyValue(AlgorithmConfigurationDialogSettings.MUST_BE_WITHIN_RANGE_FORMAT_STRING.name());
            if (e.getErrorValue().equals(NotInRangeException.ErrorValue.ABOVE_MAXIMUM_VALUE)) {
                errorLabel.setText(String.format(MUST_BE_WITHIN_RANGE_FORMAT_STRING, minValue, maxValue, maxValue));
                input.setText(Integer.toString(maxValue));
            } else if (e.getErrorValue().equals(NotInRangeException.ErrorValue.BELOW_MINIMUM_VALUE)) {
                errorLabel.setText(String.format(MUST_BE_WITHIN_RANGE_FORMAT_STRING, minValue, maxValue, minValue));
                input.setText(Integer.toString(minValue));
            }
            stillHasError = true;
        }
    }

}
