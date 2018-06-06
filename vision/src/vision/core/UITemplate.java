package vision.core;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vision.utils.propertymanager.PropertyManager;
import vision.utils.settings.AppInitSettings;
import vision.utils.settings.AppSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a general framework for creating a user interface.  Users of the Vision
 * framework would need to instantiate the workspace at the application level.  Portions of this 
 * code is inspired by Professor Banerjee.
 *
 * @author Patrick Chan
 * @author Professor Banerjee
 */
public class UITemplate extends UI {   

    /** The overall pane that contains the toolbar on the top and the {@link UITemplate#workspace} on the bottom*/
    protected Pane appPane;
    /** The pane where all the application's panes (below the toolbar) resides in */
    protected Pane workspace;

    /** The storage for all buttons that needs to be accessible on the instance level */
    protected Map<String, Button> buttonStorage;
    /** The toolbar at the top of each application */
    protected ToolBar toolbar;
    /** The image that serves as the application's logo */
    protected Image logo;

    /**
     * Creates an instance of the UITemplate class that takes a {@link Stage} parameter which specifies the window that
     * would house all the GUI components and a {@link PropertyManager} parameter which grants access to string values
     * that would otherwise be hardcoded.
     *
     * @param primaryStage The {@link Stage} that houses all the GUI components
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     */
    public UITemplate(Stage primaryStage, PropertyManager manager) {
        this.appTitle = manager.getPropertyValue(AppSettings.TITLE.name());
        this.primaryStage = primaryStage;
        buttonStorage = new HashMap<>();

        setLogo(manager);
        setToolbar(manager);
        setToolbarHandlers(manager);
        initWindow(manager);
        setCSS(manager);
    }

    /**
     * Initialize the application.  This should be overridden in the subclass.
     */
    @Override
    public void initialize() {
        PropertyManager manager = PropertyManager.getManager();
        String message = manager.getPropertyValue(AppInitSettings.NOT_INITIALIZABLE_MSG.name());
        throw new UnsupportedOperationException(message);
    }

    /**
     * Creates the toolbar
     *
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     */
    protected void setToolbar(PropertyManager manager) {
        Button newButton = makeButton(manager,
                                      AppSettings.NEW_ICON.name(),
                                      AppSettings.NEW_TOOLTIP.name(), false);
        Button saveButton = makeButton(manager,
                                       AppSettings.SAVE_ICON.name(),
                                       AppSettings.SAVE_TOOLTIP.name(), true);
        Button loadButton = makeButton(manager,
                                       AppSettings.LOAD_ICON.name(),
                                       AppSettings.LOAD_TOOLTIP.name(), false);
        Button printButton = makeButton(manager,
                                        AppSettings.PRINT_ICON.name(),
                                        AppSettings.PRINT_TOOLTIP.name(), true);
        Button exitButton = makeButton(manager,
                                       AppSettings.EXIT_ICON.name(),
                                       AppSettings.EXIT_TOOLTIP.name(), false);
        toolbar = new ToolBar(newButton, saveButton, loadButton, printButton, exitButton);
    }

    /**
     * Initialize the toolbar handlers.  Implementation is left to the subclass as we don't initialize on the framework
     * level.
     */
    protected void setToolbarHandlers(PropertyManager manager) {
        
    }

    /**
     * A convenience method for creating new buttons
     *
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     * @param iconKey The key for the button icon in the xml file
     * @param tooltipKey The key for the tooltip text in the xml file
     * @param disabled Whether the button should be disabled at initialization
     * @return a{@link Button} that has the specified icon and tooltip and the disable property set as specified
     */
    protected Button makeButton(PropertyManager manager, String iconKey, String tooltipKey, boolean disabled) {
        String name = manager.getPropertyValue(iconKey);
        String tooltip = manager.getPropertyValue(tooltipKey);
        
        String iconPath = getIconPath(manager, name);
        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        Button button = new Button(null, iv);
        button.getStyleClass().add(manager.getPropertyValue(AppSettings.TOOLBAR_BUTTON.name()));
        button.setTooltip(new Tooltip(tooltip));
        button.setDisable(disabled);
        buttonStorage.put(iconKey, button);
        return button;
    }

    /**
     * Set the logo for this application
     *
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     */
    protected void setLogo(PropertyManager manager) {
        String logoName = manager.getPropertyValue(AppSettings.LOGO.name());
        String logoPath = getIconPath(manager, logoName);
        logo = new Image(getClass().getResourceAsStream(logoPath));
    }

    /**
     * Set the CSS for this application
     *
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     */
    protected void setCSS(PropertyManager manager){        
        addCSSSheet(String.join(manager.getPropertyValue(AppSettings.SEPARATOR.name()),
                                manager.getPropertyValue(AppSettings.GUI_RESOURCE_PATH.name()),
                                manager.getPropertyValue(AppSettings.CSS_RESOURCE_PATH.name()),
                                manager.getPropertyValue(AppSettings.CSS_RESOURCE_FILENAME.name())));
    }

    /**
     * Initialize the main window that houses all the GUI components
     *
     * @param manager An instance of {@link PropertyManager} to get access to string values that would
     *                otherwise be hardcoded.
     */
    protected void initWindow(PropertyManager manager) {
        primaryStage.setTitle(appTitle);
        boolean isResizable = manager.getPropertyValueAsBoolean(AppSettings.IS_WINDOW_RESIZABLE.name());
        primaryStage.setResizable(isResizable);
        appPane = new VBox();
        appPane.getChildren().add(toolbar);

        int windowWidth = manager.getPropertyValueAsInt(AppSettings.WINDOW_WIDTH.name());
        int windowHeight = manager.getPropertyValueAsInt(AppSettings.WINDOW_HEIGHT.name());
        if (windowWidth < 1 || windowHeight < 1) {
            primaryScene = new Scene(appPane);
        } else {
            primaryScene = new Scene(appPane, windowWidth, windowHeight);
        }
        
        primaryStage.getIcons().add(logo);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    /**
     * Clears the user interface.  There is nothing to be cleared on the framework level.  Users of the Vision framework
     * should implement this method as they see fit.
     */
    protected void clear() {
        
    } 
    
}
