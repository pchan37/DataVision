package datavision.algorithms.algorithmconfig;

import datavision.api.DataAPI;
import datavision.utils.settings.AlgorithmConfigurationDialogSettings;
import datavision.utils.settings.ClustererConfigurationDialogSettings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import vision.lib.ui.dialog.ErrorDialog;
import vision.utils.propertymanager.PropertyManager;

public class ClustererConfigurationDialog extends AlgorithmConfigurationDialog {

    private String numOfClusters;

    public ClustererConfigurationDialog(Stage owner, DataAPI dataAPI, String title) {
        super(owner, dataAPI, title);
        numOfClusters = "";
    }

    @Override
    protected void renderFormContent() {
        super.renderFormContent();
        PropertyManager manager = PropertyManager.getManager();

        String NUMBER_OF_CLUSTERS = manager.getPropertyValue(ClustererConfigurationDialogSettings.NUMBER_OF_CLUSTERS.name());
        Label numOfLabelsLabel = new Label(NUMBER_OF_CLUSTERS);
        form.add(numOfLabelsLabel, 0, 3);
        TextInputControl numOfLabelsInput = new TextField();
        form.add(numOfLabelsInput, 1, 3);
        form.add(new Label(), 2, 3);
    }

    @Override
    protected void addFormEventHandlers() {
        super.addFormEventHandlers();
        TextField numOfClustersInput = (TextField)getChildAtColRow(1,3,3);
        numOfClustersInput.focusedProperty().addListener((observable, previousFocusedValue, focused) -> {
            if (!focused) {
                validateInputIsPositiveIntegerAndWithinRangeAndHandle(numOfClustersInput, 2, Math.min(4, dataAPI.getTotalPoints()), 2, 3, 3);
            } else {
                stillHasError = false;
            }
        });
    }

    @Override
    public void showDialog() {
        TextField maxIterationInput = (TextField)getChildAtColRow(1, 0, 3);
        TextField updateIntervalInput = (TextField)getChildAtColRow(1,1,3);
        CheckBox runContinuouslyInput = (CheckBox)getChildAtColRow(1,2,3);
        TextField numOfClustersInput = (TextField)getChildAtColRow(1,3,3);
        maxIterationInput.setText(maxIteration);
        updateIntervalInput.setText(updateInterval);
        runContinuouslyInput.setSelected(runContinuously);
        numOfClustersInput.setText(numOfClusters);
        ((Label)form.getChildren().get(2)).setText("");
        ((Label)form.getChildren().get(5)).setText("");
        ((Label)form.getChildren().get(11)).setText("");
        this.showAndWait();
    }

    @Override
    public boolean isValid() {
        return !maxIteration.isEmpty() && !updateInterval.isEmpty() && !numOfClusters.isEmpty();
    }

    @Override
    protected void addOkButtonEventHandler() {
        PropertyManager manager = PropertyManager.getManager();
        submit.setOnAction(e -> {
            TextField maxIterationInput = (TextField)getChildAtColRow(1, 0, 3);
            TextField updateIntervalInput = (TextField)getChildAtColRow(1,1,3);
            CheckBox runContinuouslyInput = (CheckBox)getChildAtColRow(1,2,3);
            TextField numOfClustersInput = (TextField)getChildAtColRow(1,3,3);
            maxIteration = maxIterationInput.getText();
            updateInterval = updateIntervalInput.getText();
            runContinuously = runContinuouslyInput.isSelected();
            numOfClusters = numOfClustersInput.getText();

            if (maxIteration.isEmpty()) {
                maxIteration = "1";
                stillHasError = true;
            }
            if (updateInterval.isEmpty()) {
                updateInterval = "1";
                stillHasError = true;
            }
            if (numOfClusters.isEmpty()) {
                numOfClusters = "2";
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

    public int getNumberOfClusters() {
        return Integer.parseInt(numOfClusters);
    }

}
