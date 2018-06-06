package vision.lib.ui.selection;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

/**
 * This class utilizes the Builder pattern to abstract some of the tedious parts of constructing
 * a menu using a {@link GridPane}.
 *
 * @author Patrick Chan
 */
public class MenuBuilder {

    /** The underlying {@link GridPane} that we building the menu on top of */
    private GridPane menu;
    /** The current number of rows */
    private int numRows;
    /** The current number of columns */
    private int numCols;

    /**
     * Creates an instance of the MenuBuilder class
     */
    public MenuBuilder() {
        menu = new GridPane();
        numRows = 0;
        numCols = 0;
    }

    /**
     * Add an empty label so that it looks like we skip a row
     */
    public void addEmptyRow() {
        int rowIndex = numRows;
        menu.add(new Label(), 0, rowIndex);
        numRows++;
    }

    /**
     * Add an empty label so that it looks like we skip a column
     */
    public void addEmptyColumn() {
        int colIndex = numCols;
        menu.add(new Label(), colIndex, 0);
        numCols++;
    }

    /**
     * Add the Nodes in items as elements of the next row
     *
     * @param items The {@link Node}(s) that you want to add to the menu as the next row
     */
    public void addRow(Node... items) {
        int rowIndex = numRows;
        for(int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            menu.add(items[i], i, rowIndex);
        }
        numRows++;
    }

    /**
     * Add the nodes in items as elements of the next column
     *
     * @param items The {@link Node}(s) that you want to add to the menu as the next column
     */
    public void addColumn(Node... items) {
        int colIndex = numCols;
        for(int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            menu.add(items[i], colIndex, i);
        }
        numCols++;
    }

    /**
     * Retrieve the finished {@link GridPane} product
     *
     * @return The {@link GridPane} produced by this builder
     */
    public GridPane getMenu() {
        return menu;
    }

    /**
     * Retrieve a scrollable version of the finished {@link GridPane} product
     *
     * @return A {@link ScrollPane} containing the {@link GridPane} produced by this builder
     */
    public ScrollPane getScrollableMenu() {
        return new ScrollPane(menu);
    }

}