package datavision.lib.ui.algorithmselection;

import datavision.api.AlgorithmAPI;
import datavision.api.DataAPI;
import datavision.core.AppAction;
import datavision.algorithms.algorithmconfig.AlgorithmConfigurationDialog;
import datavision.utils.settings.AlgorithmSelectionMenuSettings;
import datavision.utils.settings.DataVisionSettings;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import vision.core.Action;
import vision.core.UITemplate;
import vision.lib.ui.selection.Menu;
import vision.lib.ui.selection.MenuBuilder;
import vision.utils.propertymanager.PropertyManager;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class AlgorithmSelectionMenu extends Menu {

    private String algorithmType;
    private Map<String, AlgorithmConfigurationDialog> configurationDialogMap;
    private Button algorithmRunButton;

    AlgorithmSelectionMenu(Stage owner, UITemplate ui, Action action, DataAPI dataAPI, String algorithmType, Map<String, AlgorithmConfigurationDialog> configurationDialogMap, PropertyManager manager) {
        super(ui);
        this.algorithmType = algorithmType;
        this.configurationDialogMap = configurationDialogMap;
        buildMenu(owner, (AppAction)action, dataAPI, algorithmType, manager);
        configureMenu();
    }

    private void buildMenu(Stage owner, AppAction action, DataAPI dataAPI, String algorithmType, PropertyManager manager) {
        MenuBuilder menuBuilder = new MenuBuilder();

        Map<String, List<String>> algorithmMap = AlgorithmAPI.getAlgorithmsMap();
        List<String> algorithms = algorithmMap.get(algorithmType);
        ToggleGroup algorithmTG = new ToggleGroup();

        for(String algo: algorithms) {
            String algorithmName = algo.substring(algo.lastIndexOf(".") + 1);
            RadioButton rb = new RadioButton(algorithmName);
            rb.setToggleGroup(algorithmTG);
            rb.selectedProperty().addListener((observable, oldValue, newValue) -> setAlgorithmRunButtonVisible(configurationDialogMap.get(algorithmName).isValid()));

            String configButtonTooltip = manager.getPropertyValue(DataVisionSettings.CONFIG_BUTTON_TOOLTIP.name());
            Button configButton = generateConfigButton(manager);
            configButton.setOnAction(event -> {
                AlgorithmConfigurationDialog configurationDialog = configurationDialogMap.get(algorithmName);
                configurationDialog.showDialog();
                setAlgorithmRunButtonVisible(configurationDialog.isValid() && rb.isSelected());
            });

            configButton.setTooltip(new Tooltip(configButtonTooltip));
            configButton.getStyleClass().add(manager.getPropertyValue(DataVisionSettings.CONFIG_BUTTON_STYLE.name()));

            String ALGORITHM_RUN_CONFIGURATION = manager.getPropertyValue(AlgorithmSelectionMenuSettings.ALGORITHM_RUN_CONFIGURATION.name());
            AlgorithmConfigurationDialog configurationDialog = AlgorithmAPI.getAlgorithmConfigurationDialog(algorithmType, owner, dataAPI, ALGORITHM_RUN_CONFIGURATION);
            configurationDialogMap.putIfAbsent(algorithmName, configurationDialog);

            menuBuilder.addRow(rb, null, configButton);
        }

        Button backButton = getBackButton(manager);
        setAlgorithmRunButton(manager, action, algorithmType, algorithmTG);
        menuBuilder.addEmptyRow();
        menuBuilder.addRow(backButton, null, algorithmRunButton);

        menu = menuBuilder.getMenu();
    }

    private Button generateConfigButton(PropertyManager manager) {
        String configButtonIcon = manager.getPropertyValue(DataVisionSettings.CONFIG_BUTTON_ICON.name());
        String iconPath = getIconPath(manager, configButtonIcon);

        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        iv.setFitHeight(20);
        iv.setFitWidth(20);

        return new Button(null, iv);
    }

    private void setAlgorithmRunButton(PropertyManager manager, AppAction action, String algorithmType, ToggleGroup algorithmTG) {
        String run = manager.getPropertyValue(DataVisionSettings.RUN_TEXT.name());
        algorithmRunButton = new Button(run);

        algorithmRunButton.setOnAction(e -> {
            RadioButton selectedRadioButton = (RadioButton)algorithmTG.getSelectedToggle();
            String selectedAlgorithm = selectedRadioButton.getText();
            algorithmRunButton.setDisable(true);
            menu.setDisable(true);
            if (!configurationDialogMap.get(selectedAlgorithm).getContinuousRun()) {
                String RESUME = manager.getPropertyValue(AlgorithmSelectionMenuSettings.RESUME.name());
                algorithmRunButton.setText(RESUME);
            }
            CountDownLatch jobCount = new CountDownLatch(1);
            action.handleAlgorithmRunRequest(algorithmType, selectedAlgorithm, configurationDialogMap.get(selectedAlgorithm), jobCount);
            enableAlgorithmButtonAndMenuAfterAlgorithmRun(jobCount);
        });
        algorithmRunButton.managedProperty().bindBidirectional(algorithmRunButton.visibleProperty());
        setAlgorithmRunButtonVisible(false);
    }

    private void setAlgorithmRunButtonVisible(boolean isVisible) {
        algorithmRunButton.setVisible(isVisible);
    }

    private void enableAlgorithmButtonAndMenuAfterAlgorithmRun(CountDownLatch jobCount) {
        Thread waitForAlgorithmToFinish = new Thread(() -> {
            try {
                jobCount.await();
            } catch (InterruptedException ex) {
            }
            menu.setDisable(false);
            algorithmRunButton.setDisable(false);
        });
        waitForAlgorithmToFinish.start();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof  AlgorithmSelectionMenu && ((AlgorithmSelectionMenu)obj).algorithmType.equals(algorithmType));
    }
}
