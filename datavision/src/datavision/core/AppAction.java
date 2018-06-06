package datavision.core;

import datavision.algorithms.DataSet;
import datavision.algorithms.algorithmadapters.AlgorithmAdapter;
import datavision.api.AlgorithmAPI;
import datavision.algorithms.algorithmconfig.AlgorithmConfigurationDialog;
import datavision.api.DataAPI;
import datavision.utils.settings.AppActionSettings;
import vision.lib.exceptions.InvalidDataFormatException;
import vision.lib.exceptions.LoadErrorException;
import javafx.application.Platform;
import datavision.utils.settings.DataVisionSettings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import vision.core.Action;
import vision.core.ApplicationTemplate;
import vision.core.Data;
import vision.core.UITemplate;
import vision.lib.exceptions.SaveErrorException;
import vision.lib.ui.dialog.ConfirmationDialog;
import vision.utils.propertymanager.PropertyManager;
import vision.utils.settings.AppSettings;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

public final class AppAction extends Action {

    private final PropertyManager manager;

    /** The boolean property marking whether or not there are any unsaved changes. */
    final SimpleBooleanProperty isUnsaved;
    private SimpleBooleanProperty algorithmRunning;
    public SimpleBooleanProperty chartNotHasData;
    private AlgorithmAdapter algorithmAdapter;

    private Path saveFilePath;

    AppAction(UITemplate ui, Data data, PropertyManager manager) {
        this.ui = ui;
        this.data = data;
        this.manager = manager;
        this.isUnsaved = new SimpleBooleanProperty(false);
        this.algorithmRunning = new SimpleBooleanProperty(false);
        this.chartNotHasData = new SimpleBooleanProperty(true);

        AppUI uicomponent = (AppUI)ui;
        uicomponent.getButtonStorage().get(AppSettings.SAVE_ICON.name()).disableProperty().bind(this.isUnsaved.not());
        uicomponent.getButtonStorage().get(DataVisionSettings.SCREENSHOT_ICON.name()).disableProperty().bindBidirectional(this.chartNotHasData);
    }

    @Override
    public void handleNewRequest() {
        AppUI uicomponent = ((AppUI)ui);

        if (algorithmRunning.get()) {
            String algorithmStillRunningTitle = manager.getPropertyValue(AppActionSettings.ALGORITHM_IS_STILL_RUNNING_TITLE.name());
            String algorithmStillRunningMsg = manager.getPropertyValue(AppActionSettings.ALGORITHM_IS_STILL_RUNNING_STOP_ALGORITHM_MESSAGE.name());
            ConfirmationDialog.Option userExitChoice = uicomponent.getUserChoice(algorithmStillRunningTitle, algorithmStillRunningMsg);
            if (!userExitChoice.equals(ConfirmationDialog.Option.YES)) {
                return;
            }
        }
        algorithmRunning.set(false);

        uicomponent.setTextAreaVisible(true);
        uicomponent.setEditDoneToggleButtonVisible(true);
        uicomponent.setMetaDataLabelVisible(false);
        uicomponent.getAlgorithmSelectionSystem().clear();
        uicomponent.setChartVisible(false);
        uicomponent.clear();
        saveFilePath = null;

        String editDoneToggleButtonKey = DataVisionSettings.EDIT_DONE_TOGGLE_BUTTON.name();
        Button editDoneToggleButton = uicomponent.getButtonStorage().get(editDoneToggleButtonKey);
        String done = manager.getPropertyValue(DataVisionSettings.DONE_BUTTON_TEXT.name());
        editDoneToggleButton.setText(done);
    }

    @Override
    public void handleLoadRequest() throws InvalidDataFormatException, LoadErrorException {
        AppUI uicomponent = ((AppUI)ui);
        if (algorithmRunning.get()) {
            String algorithmStillRunningTitle = manager.getPropertyValue(AppActionSettings.ALGORITHM_IS_STILL_RUNNING_TITLE.name());
            String algorithmStillRunningMsg = manager.getPropertyValue(AppActionSettings.ALGORITHM_IS_STILL_RUNNING_STOP_ALGORITHM_MESSAGE.name());
            ConfirmationDialog.Option userExitChoice = uicomponent.getUserChoice(algorithmStillRunningTitle, algorithmStillRunningMsg);
            if (!userExitChoice.equals(ConfirmationDialog.Option.YES)) {
                return;
            }
        }
        algorithmRunning.set(false);

        proceedToLoad();
    }

    @Override
    public void handleSaveRequest() throws InvalidDataFormatException, SaveErrorException {
        AppUI uicomponent = (AppUI)ui;
        String unsavedTitle = manager.getPropertyValue(DataVisionSettings.SAVE_UNSAVED_WORK_TITLE.name());
        String unsavedMessage = manager.getPropertyValue(DataVisionSettings.SAVE_UNSAVED_WORK.name());
        if ((saveFilePath == null && uicomponent.getUserChoice(unsavedTitle, unsavedMessage).equals(ConfirmationDialog.Option.YES)) || saveFilePath != null){
            proceedToSave();
        }
    }

    @Override
    public void handlePrintRequest() {
        // this method would not do anything in this project
    }

    @Override
    public void handleExitRequest() throws InvalidDataFormatException, SaveErrorException {
        AppUI uicomponent = (AppUI)ui;

        if (algorithmRunning.get()) {
            String algorithmStillRunningTitle = manager.getPropertyValue(AppActionSettings.ALGORITHM_IS_STILL_RUNNING_TITLE.name());
            String algorithmStillRunningMsg = manager.getPropertyValue(AppActionSettings.ALGORITHM_IS_STILL_RUNNING_QUIT_APPLICATION_MESSAGE.name());
            ConfirmationDialog.Option userExitChoice = uicomponent.getUserChoice(algorithmStillRunningTitle, algorithmStillRunningMsg);
            if (!userExitChoice.equals(ConfirmationDialog.Option.YES)) {
                return;
            }
        }

        if (isUnsaved.get()) {
            String unsavedTitle = manager.getPropertyValue(DataVisionSettings.SAVE_UNSAVED_WORK_TITLE.name());
            String unsavedMessage = manager.getPropertyValue(DataVisionSettings.SAVE_UNSAVED_WORK.name());
            ConfirmationDialog.Option userSaveChoice = uicomponent.getUserChoice(unsavedTitle, unsavedMessage);
            if (userSaveChoice.equals(ConfirmationDialog.Option.YES)) {
                proceedToSave();
            } else if (userSaveChoice.equals(ConfirmationDialog.Option.CANCEL)) {
                return;
            }
        }
        System.exit(0);
    }

    public void handleScreenshotRequest() throws SaveErrorException {
        AppUI uicomponent = (AppUI)ui;
        Path imageFilePath = null;
        try {
            WritableImage snapshot = ((AppUI)ui).getChart().snapshot(new SnapshotParameters(), null);
            String description = manager.getPropertyValue(DataVisionSettings.IMAGE_FILE_EXT_DESC.name());
            String extension = manager.getPropertyValue(DataVisionSettings.IMAGE_FILE_EXT.name());
            imageFilePath = uicomponent.getSaveFile(description, extension);

            if (imageFilePath != null) {
                String imageFormat = manager.getPropertyValue(DataVisionSettings.IMAGE_FILE_EXT.name()).substring(1);
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), imageFormat, imageFilePath.toFile());
            }
        } catch (IOException e) {
            if (!imageFilePath.toFile().canWrite()) {
                throw new SaveErrorException(imageFilePath, "you don't have permission to write to the field!");
            }
            throw new SaveErrorException(imageFilePath, SaveErrorException.UNKNOWN_REASON);
        }
    }

    public void handleEditDoneRequest(ActionEvent e) throws InvalidDataFormatException {
        AppUI uicomponent = ((AppUI)ui);
        if (algorithmRunning.get()) {
            String algorithmStillRunningTitle = manager.getPropertyValue(AppActionSettings.ALGORITHM_IS_STILL_RUNNING_TITLE.name());
            String algorithmStillRunningMsg = manager.getPropertyValue(AppActionSettings.ALGORITHM_IS_STILL_RUNNING_STOP_ALGORITHM_MESSAGE.name());
            ConfirmationDialog.Option userExitChoice = uicomponent.getUserChoice(algorithmStillRunningTitle, algorithmStillRunningMsg);
            if (!userExitChoice.equals(ConfirmationDialog.Option.YES)) {
                return;
            }
        }
        algorithmRunning.set(false);

        Button editDoneToggleButton = (Button)e.getSource();
        String currentText = editDoneToggleButton.getText();
        String edit = manager.getPropertyValue(DataVisionSettings.EDIT_BUTTON_TEXT.name());
        String done = manager.getPropertyValue(DataVisionSettings.DONE_BUTTON_TEXT.name());
        if (currentText.equals(edit)) {
            ((AppUI)ui).setTextAreaDisabledState(false);
            editDoneToggleButton.setText(done);
            uicomponent.getAlgorithmSelectionSystem().clear();
        } else {
            AppData datacomponent = (AppData) data;
            Data.DataValidity validity;
            if ((validity = datacomponent.checkDataFromTextArea()).valid()) {
                uicomponent.setTextAreaDisabledState(true);
                editDoneToggleButton.setText(edit);
                uicomponent.setMetaDataLabelVisible(true);
                uicomponent.getAlgorithmSelectionSystem().begin();
                if (saveFilePath == null) {
                    uicomponent.updateMetaDataLabel(datacomponent.getMetaData());
                } else {
                    uicomponent.updateMetaDataLabel(datacomponent.getMetaData(saveFilePath.toString()));
                }
            } else {
                throw new InvalidDataFormatException(validity.getError());
            }
        }
    }

    public void handleAlgorithmRunRequest(String algorithmType, String algorithmName, AlgorithmConfigurationDialog algorithmConfig, CountDownLatch jobCount) {
        AppData datacomponent = (AppData)data;
        AppUI uicomponent = (AppUI)ui;

        uicomponent.setChartVisible(true);
        if (!algorithmRunning.get()) {
            DataSet dataSet = datacomponent.generateDataSetFromTextArea();
            algorithmAdapter = AlgorithmAPI.getAlgorithmAdapter(algorithmType, uicomponent.getPlotAPI(), new DataAPI(data));
            algorithmAdapter.createAlgorithm(algorithmType, algorithmName, dataSet, algorithmConfig);
            algorithmRunning.set(true);
        }

        Thread algorithmThread;
        if (algorithmConfig.getContinuousRun()) {
            algorithmThread = new Thread(() -> {
                try {
                    while (algorithmAdapter.toContinue() && algorithmRunning.get()) {
                        algorithmAdapter.runAlgorithm();
                        chartNotHasData.set(false);
                    }
                    jobCount.countDown();
                    algorithmRunning.set(false);
                    Platform.runLater(uicomponent.getAlgorithmSelectionSystem()::previous);
                } catch (InterruptedException ex) {
                    uicomponent.handleInterruptedException();
                }
            });
        } else {
            algorithmThread = new Thread(() -> {
                try {
                    if (algorithmAdapter.toContinue() && algorithmRunning.get()) {
                        algorithmAdapter.runAlgorithm();
                        Thread.sleep(1000);
                        chartNotHasData.set(false);
                        jobCount.countDown();
                    }
                    if (!algorithmAdapter.toContinue()) {
                        algorithmRunning.set(false);
                        Platform.runLater(uicomponent.getAlgorithmSelectionSystem()::previous);
                    }
                } catch (InterruptedException ex) {
                    uicomponent.handleInterruptedException();
                }
            });
        }
        algorithmThread.start();
    }

    private void proceedToLoad() throws InvalidDataFormatException, LoadErrorException {
        Path loadFilePath = null;
        try {
            AppData datacomponent = (AppData) data;
            AppUI uicomponent = (AppUI)ui;
            String description = manager.getPropertyValue(DataVisionSettings.DATA_FILE_EXT_DESC.name());
            String extension = manager.getPropertyValue(DataVisionSettings.DATA_FILE_EXT.name());
            loadFilePath = uicomponent.getLoadFile(description, extension);
            if (loadFilePath == null) {
                return;
            }

            if (!loadFilePath.toFile().exists()) {
                throw new LoadErrorException(loadFilePath, "the file does not exists!");
            }
            if (!loadFilePath.toFile().canRead()) {
                throw new LoadErrorException(loadFilePath, "you don't have permission to read the file!");
            }
            if (!datacomponent.loadFromFile(loadFilePath).success()) {
                throw new LoadErrorException(loadFilePath, LoadErrorException.UNKNOWN_REASON);
            }
            Data.DataValidity validity;
            if ((validity = datacomponent.strictCheckDataFromTextArea()).valid()) {
                uicomponent.setTextAreaVisible(true);
                uicomponent.setTextAreaDisabledState(true);
                uicomponent.setEditDoneToggleButtonVisible(false);
                uicomponent.getButtonStorage().get(DataVisionSettings.EDIT_DONE_TOGGLE_BUTTON.name()).setText(manager.getPropertyValue(DataVisionSettings.EDIT_BUTTON_TEXT.name()));
                uicomponent.setMetaDataLabelVisible(true);
                uicomponent.getAlgorithmSelectionSystem().begin();
                isUnsaved.set(false);
                uicomponent.updateMetaDataLabel(datacomponent.getMetaData(loadFilePath.toString()));
                saveFilePath = loadFilePath;
            } else {
                uicomponent.clear();
                throw new InvalidDataFormatException(validity.getError());
            }
        } catch (IOException e) {
            throw new LoadErrorException(loadFilePath, LoadErrorException.UNKNOWN_REASON);
        }
    }

    private void proceedToSave() throws InvalidDataFormatException, SaveErrorException {
        try {
            AppData datacomponent = (AppData)data;
            AppUI uicomponent = (AppUI)ui;
            Data.DataValidity validity;
            if ((validity = datacomponent.checkDataFromTextArea()).valid()) {
                if (saveFilePath == null) {
                    String description = manager.getPropertyValue(DataVisionSettings.DATA_FILE_EXT_DESC.name());
                    String extension = manager.getPropertyValue(DataVisionSettings.DATA_FILE_EXT.name());
                    saveFilePath = uicomponent.getSaveFile(description, extension);
                }
                if (saveFilePath != null && datacomponent.saveToFile(saveFilePath).success()) {
                    isUnsaved.set(false);
                }
            } else {
                throw new InvalidDataFormatException(validity.getError());
            }
        } catch (IOException e) {
            if (!saveFilePath.toFile().canWrite()) {
                throw new SaveErrorException(saveFilePath, "you don't have permission to write to the file!");
            }
            throw new SaveErrorException(saveFilePath, SaveErrorException.UNKNOWN_REASON);
        }
    }

    /* FIXME */
    public void setUpDataSync(TextArea textArea) {
        ((AppData)data).textAreaText.bindBidirectional(textArea.textProperty());
    }
}

