package vision.core;

import javafx.application.Application;
import javafx.stage.Stage;

import vision.lib.ui.dialog.ErrorDialog;
import vision.utils.propertymanager.PropertyManager;
import vision.utils.settings.AppInitSettings;
import vision.utils.xmlutils.InvalidXMLFileFormatException;

/**
 * This class serves as a template for a Vision application.  The user is expected to
 * expand upon this class to get a working application.  Portions of this class were 
 * inspired by Professor Banerjee.
 * 
 * @author Patrick Chan
 * @author Ritwik Banerjee
 */
public class ApplicationTemplate extends Application {

    /** Grants access to string values that would otherwise have to be hardcoded */
    public final PropertyManager manager = PropertyManager.getManager();

    /**
     * A wrapper around a boolean for a more readable way of checking if an initialization is successful
     */
    public class InitState {

        /** Whether the initialization was successful or not */
        private boolean success;

        /**
         * Creates an instance of the InitState class that takes a boolean parameter specifying if the initialization
         * was successful or not
         *
         * @param success specify whether the initialization was successful or not
         */
        public InitState(boolean success) {
            this.success = success;
        }

        /**
         * Return if the initialization was successful or not
         *
         * @return if the initialization was successful or not
         */
        public boolean success() {
            return this.success;
        }

    }

    /**
     * Start the JavaFX application
     *
     * @param primaryStage The {@link Stage} for this application
     */
    @Override
    public void start(Stage primaryStage) {
        if (manager == null || !initializeProperties().success()) {
            handlePropertyLoadingError(primaryStage);
        }
        if (!initializeUI(primaryStage).success()) {
            handleUILoadingError(primaryStage);
        }
    }

    /**
     * Initialize the properties in the {@link PropertyManager}
     *
     * @return whether initialization of the {@link PropertyManager} was successful or not via {@link InitState}
     */
    protected InitState initializeProperties() {
        boolean success;
        try {
            manager.loadProperties(ApplicationTemplate.class,
                                   AppInitSettings.PROPERTIES_XML_FILE.getParameterName(),
                                   AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            success = true;
        } catch (InvalidXMLFileFormatException e) {
            success = false;
        }
        return new InitState(success);
    }

    /**
     * Initialize the UI
     *
     * @param primaryStage The {@link Stage} for this application
     * @return whether initialization of the UI was successful or not via {@link InitState}
     */
    protected InitState initializeUI(Stage primaryStage) {
        boolean success;
        UI ui = new UITemplate(primaryStage, manager);
        try {
            ui.initialize();
            success = true;
        } catch (UnsupportedOperationException e) {
            success = false;
        }
        return new InitState(success);
    }

    /**
     * Handles any errors while loading the properties in the {@link PropertyManager} by popping up an error dialog and
     * closing the application afterward.
     *
     * @param owner The {@link Stage} for this application
     */
    protected void handlePropertyLoadingError(Stage owner) {
        String errorTitle = AppInitSettings.APP_LOAD_ERROR_TITLE.getParameterName();
        String errorMessage = AppInitSettings.PROPERTIES_LOAD_ERROR_MESSAGE.getParameterName();

        ErrorDialog errorDialog = new ErrorDialog(owner, errorTitle, errorMessage);
        errorDialog.showAndWait();

        System.exit(1);
    }

    /**
     * Handle any errors while loading the UI by popping up an error dialog and closing the application afterward.
     *
     * @param owner The {@link Stage} for this application
     */
    protected void handleUILoadingError(Stage owner) {
        String errorTitle = AppInitSettings.NOT_SUPPORTED_FOR_TEMPLATE_ERROR_TITLE.getParameterName();
        String errorMsg = AppInitSettings.OPERATION_NOT_SUPPORTED.getParameterName();

        ErrorDialog errorDialog = new ErrorDialog(owner, errorTitle, errorMsg);
        errorDialog.showAndWait();

        System.exit(1);
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}