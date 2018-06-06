package datavision.lib.ui.algorithmselection;

import datavision.api.DataAPI;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import vision.core.Action;
import vision.core.UITemplate;
import vision.lib.ui.selection.Menu;
import vision.lib.ui.selection.SelectionSystem;
import vision.utils.propertymanager.PropertyManager;

import java.util.ArrayList;

public class AlgorithmSelectionSystem extends SelectionSystem {

    private Stage owner;
    private Action action;
    private DataAPI dataAPI;
    private Pane container;
    private PropertyManager manager;

    public AlgorithmSelectionSystem(Stage owner, UITemplate ui, Action action, DataAPI dataAPI, Pane container, PropertyManager manager) {
        super(ui);
        this.owner = owner;
        this.action = action;
        this.dataAPI = dataAPI;
        this.container = container;
        this.manager = manager;
        menus = new ArrayList<>();
    }

    @Override
    public boolean addMenu(Menu menu) {
        // FIXME
        // Making sure that menus aren't added twice, best way is to avoid triggering the click event multiple times
        if (menus.size() > 0 && (menu.equals(menus.get(menus.size() - 1)) || menu instanceof AlgorithmTypeSelectionMenu)) {
            return false;
        }
        return super.addMenu(menu);
    }

    @Override
    public void removeMenu(Menu menu) {
        super.removeMenu(menu);
        container.getChildren().remove(container.getChildren().size() - 1);
    }

    @Override
    public void clear() {
        if (menus.size() > 0) {
            Menu currentMenu = menus.get(currentIndex);
            currentMenu.hide();
            while (menus.size() > 0) {
                removeMenu(menus.get(0));
            }
        }
        menus = new ArrayList<>();
        currentIndex = 0;
    }

    @Override
    public void begin() {
        initAlgorithmSelectionSystem();
        super.begin();
    }

    private void initAlgorithmSelectionSystem() {
        clear();
        AlgorithmTypeSelectionMenu algorithmTypeSelectionMenu = new AlgorithmTypeSelectionMenu(owner, ui, action, dataAPI, container, manager);
        addMenu(algorithmTypeSelectionMenu);
    }
}
