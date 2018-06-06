package vision.lib.ui.selection;

import vision.core.UIElement;
import vision.core.UITemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * This class ties multiple menus in a series together and provide a powerful interface for
 * navigating between them.
 *
 * @author Patrick Chan
 */
public abstract class SelectionSystem extends UIElement {

    /** A list of the menus that this selection system would rotate through */
    protected List<Menu> menus;
    /** The current index of the {@link SelectionSystem#menus} list that is being shown */
    protected int currentIndex;
    /** The UI component for which this selection system is part of */
    protected UITemplate ui;

    public SelectionSystem() {

    }

    /**
     * Creates an instance of the SelectionSystem class that takes an {@link UITemplate} parameter that this
     * selection system is part of.
     *
     * @param ui The UI component for which this selection system is part of
     */
    public SelectionSystem(UITemplate ui) {
        this.ui = ui;
    }

    /**
     * Add a menu to the end of {@link SelectionSystem#menus} and link the given {@link Menu} to this selection
     * system.
     *
     * @param menu The menu you are adding to the selection system
     * @return Whether the menu was added successfully or not
     */
    public boolean addMenu(Menu menu) {
        menus.add(menu);
        menu.setSelectionSystem(this);
        return true;
    }

    /**
     * Remove the given {@link Menu} parameter from the {@link SelectionSystem#menus}
     *
     * @param menu The menu to be removed
     */
    public void removeMenu(Menu menu) {
        menus.remove(menu);
        menu.setSelectionSystem(null);
    }

    /**
     * Show the first menu
     */
    public void begin() {
        currentIndex = 0;
        Menu currentMenu = menus.get(currentIndex);
        currentMenu.show();
    }

    /**
     * Clear all the menus in {@link SelectionSystem#menus}
     */
    public void clear() {
        if (menus.size() > 0) {
            Menu currentMenu = menus.get(currentIndex);
            currentMenu.hide();
        }

        menus = new ArrayList<>();
        currentIndex = 0;
    }

    /**
     * Go to the previous menu if possible.  Otherwise, stay on the current menu.
     */
    public void previous() {
        if (currentIndex <= 0) {
            return;
        }
        Menu currentMenu = menus.get(currentIndex);
        currentMenu.hide();
        removeMenu(currentMenu);
        Menu previousMenu = menus.get(currentIndex - 1);
        previousMenu.show();
        currentIndex--;
    }

    /**
     * Go to the next menu if possible.  Otherwise, stay on the current menu.
     */
    public void next() {
        if (currentIndex >= menus.size() - 1) {
            return;
        }
        Menu currentMenu = menus.get(currentIndex);
        currentMenu.hide();
        Menu nextMenu = menus.get(currentIndex + 1);
        nextMenu.show();
        currentIndex++;
    }

}
