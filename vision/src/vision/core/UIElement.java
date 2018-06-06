package vision.core;

import javafx.scene.Scene;
import javafx.stage.Stage;
import vision.utils.propertymanager.PropertyManager;
import vision.utils.settings.AppSettings;

/**
 * The abstract base class for all UI components.
 */
public abstract class UIElement {

    /** The scene for the main window of this application */
    protected Scene primaryScene;
    /** The stage for the main window of this application */
    protected Stage primaryStage;
    /** The title of this application */
    protected String appTitle;

    /**
     * Get the stage for the main window of this application
     *
     * @return The {@link Stage} for the main window of this application
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Get the scene for the main window of this application
     *
     * @return The {@link Scene} for the main window of this application
     */
    public Scene getPrimaryScene() {
        return primaryScene;
    }

    /**
     * Return the title of this application
     *
     * @return The title of this application
     */
    public String getTitle() {
        return appTitle;
    }

    /**
     * Given a {@link String} parameter denoting the filename of a CSS file, add it to the application set of CSS files
     *
     * @param filename The filename of a CSS file
     */
    protected void addCSSSheet(String filename) {
        primaryScene.getStylesheets().add(filename);
    }

    /**
     * Get the icon path given an instance of {@link PropertyManager} and the icon name
     *
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     * @param iconName The name of the icon
     * @return The path of the icon as a {@link String}
     */
    protected String getIconPath(PropertyManager manager, String iconName) {
        String SEPARATOR      = manager.getPropertyValue(AppSettings.SEPARATOR.name());
        String GUI_PATH       = manager.getPropertyValue(AppSettings.GUI_RESOURCE_PATH.name());
        String ICONS_PATH     = manager.getPropertyValue(AppSettings.ICONS_RESOURCE_PATH.name());
        String BASE_ICON_PATH = SEPARATOR + String.join(SEPARATOR, GUI_PATH, ICONS_PATH);
        return String.join(SEPARATOR, BASE_ICON_PATH, iconName);
    }

    /**
     * Clears the user interface.  There is nothing to be cleared on the abstract level.  Users of the Vision framework
     * should implement this method as they see fit.
     */
    protected abstract void clear();

}
