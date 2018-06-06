package datavision.lib.ui.algorithmselection;

import datavision.algorithms.algorithmadapters.AlgorithmAdapter;
import datavision.api.AlgorithmAPI;
import datavision.api.DataAPI;
import datavision.core.AppAction;
import datavision.algorithms.algorithmconfig.AlgorithmConfigurationDialog;
import vision.core.Action;
import vision.core.UITemplate;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import vision.lib.ui.selection.Menu;
import vision.lib.ui.selection.MenuBuilder;
import vision.utils.propertymanager.PropertyManager;

import java.util.*;

public class AlgorithmTypeSelectionMenu extends Menu {

    private Map<String, AlgorithmConfigurationDialog> configurationDialogMap;

    AlgorithmTypeSelectionMenu(Stage owner, UITemplate ui, Action action, DataAPI dataAPI, Pane container, PropertyManager manager) {
        super(ui);
        configurationDialogMap = new HashMap<>();
        buildMenu(owner, (AppAction)action, dataAPI, container, manager);
        configureMenu();
    }

    private void buildMenu(Stage owner, AppAction action, DataAPI dataAPI, Pane container, PropertyManager manager) {
        MenuBuilder menuBuilder = new MenuBuilder();

        Map<String, List<String>> algorithmMap = AlgorithmAPI.getAlgorithmsMap();
        ToggleGroup algorithmTypeTG = new ToggleGroup();

        for(String algorithmType: algorithmMap.keySet()) {
            AlgorithmAdapter algorithmAdapter = AlgorithmAPI.getAlgorithmAdapter(algorithmType, null, dataAPI);
            RadioButton rb = new RadioButton(algorithmType);
            if (!algorithmAdapter.isValidForDataSet()){
                continue;
            }
            rb.setToggleGroup(algorithmTypeTG);
            rb.selectedProperty().addListener(((observable, oldValue, newValue) -> {
                AlgorithmSelectionMenu algorithmSelectionMenu = new AlgorithmSelectionMenu(owner, ui, action, dataAPI, rb.getText(), configurationDialogMap, manager);

                // FIXME
                // Hack to make sure we don't add twice; a better solution would be to not trigger the click event twice
                if (selectionSystem.addMenu(algorithmSelectionMenu)) {
                    container.getChildren().add(algorithmSelectionMenu.getScrollableMenu());
                    selectionSystem.next();
                    rb.setSelected(false);
                }
            }));
            menuBuilder.addRow(rb);
        }
        menu = menuBuilder.getMenu();
        ScrollPane sp = menuBuilder.getScrollableMenu();
        sp.managedProperty().bindBidirectional(sp.visibleProperty());
        sp.visibleProperty().bindBidirectional(menu.visibleProperty());
        container.getChildren().add(sp);
    }

    public GridPane getMenu() {
        return menu;
    }

}
