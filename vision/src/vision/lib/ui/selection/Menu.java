package vision.lib.ui.selection;

import datavision.utils.settings.DataVisionSettings;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import vision.core.UIElement;
import vision.core.UITemplate;
import vision.utils.propertymanager.PropertyManager;

/**
 * This class provide some basic functionality for a menu including constructing a back and next button,
 * showing and hiding the menu, configuring the menu, and etc.  Utilizes a {@link GridPane} as the underlying
 * data-structure.
 *
 * @author Patrick Chan
 */
public abstract class Menu extends UIElement {

    /** The ui component that this menu is part of */
    protected UITemplate ui;
    /** The underlying pane for which the menu would be drawn on */
    protected GridPane menu;
    /** The {@link SelectionSystem} that this menu is part of */
    protected SelectionSystem selectionSystem;

    public Menu() {

    }

    /**
     * Creates an instance of the Menu class that takes an {@link UITemplate} parameter that this menu
     * is part of.
     *
     * @param ui The UI component that this menu is part of
     */
    public Menu(UITemplate ui) {
        this.ui = ui;
    }

    /**
     * Link this to the selection system which allows one to more conveniently navigate backward and forward
     * in a series of menus.
     *
     * @param selectionSystem An object that simplifies the backward and forward movements of multiple menus
     *                        in a series
     * @see SelectionSystem
     */
    public void setSelectionSystem(SelectionSystem selectionSystem) {
        this.selectionSystem = selectionSystem;
    }

    /**
     * A convenience method that constructs a back button for going back in a series of a menu.
     *
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     * @return A button that when clicked tells the selection system to display the previous menu
     * @see SelectionSystem
     */
    protected Button getBackButton(PropertyManager manager) {
        String back = manager.getPropertyValue(DataVisionSettings.BACK_TEXT.name());
        Button backButton = new Button(back);
        backButton.setOnAction(event -> selectionSystem.previous());
        return backButton;
    }

    /**
     * A convenience method that constructs a forward button for going forward in a series of a menu.
     *
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     * @return A button that when clicked tells the selection system to display the next menu
     * @see SelectionSystem
     */
    protected Button getNextButton(PropertyManager manager) {
        String next = manager.getPropertyValue(DataVisionSettings.NEXT_TEXT.name());
        Button nextButton = new Button(next);
        nextButton.setOnAction(event -> selectionSystem.next());
        return nextButton;
    }

    /**
     * Provides some sensible defaults for the underlying {@link GridPane}
     */
    protected void configureMenu() {
        menu.managedProperty().bindBidirectional(menu.visibleProperty());
        menu.setVisible(false);
        menu.setVgap(3);
        menu.setHgap(30);
    }

    /**
     * Make the menu visible
     */
    public void show() {
        menu.setVisible(true);
    }

    /**
     * Make the menu invisible.  With the default settings, the menu does not take up any room in this
     * state.
     */
    public void hide() {
        menu.setVisible(false);
    }

    /**
     * Clears the menu
     */
    public void clear() {}

    /**
     * Get the underlying pane that the menu is rendered on
     *
     * @return A {@link GridPane} that the menu is rendered on
     */
    public GridPane getMenu() {
        return menu;
    }

    /**
     * A convenience method for wrapping the underlying menu into a {@link ScrollPane} to allow the
     * user to scroll through it.
     *
     * @return A {@link ScrollPane} that wraps around the underlying {@link GridPane}
     */
    public ScrollPane getScrollableMenu() {
        ScrollPane sp = new ScrollPane(menu);
        sp.managedProperty().bindBidirectional(sp.visibleProperty());
        sp.visibleProperty().bindBidirectional(menu.visibleProperty());
        return sp;
    }
}
