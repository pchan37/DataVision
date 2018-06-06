package datavision.core;

import datavision.utils.settings.DataVisionSettings;
import javafx.stage.Stage;
import vision.core.ApplicationTemplate;
import vision.core.UI;
import vision.utils.settings.AppInitSettings;
import vision.utils.xmlutils.InvalidXMLFileFormatException;

/*
 * The class from which the application would run.
 * 
 * @author Patrick Chan
 */
public final class DataVision extends ApplicationTemplate {

    @Override
    public void start(Stage primaryStage) {
        if (manager == null || !initializeProperties().success()) {
            handlePropertyLoadingError(primaryStage);
        }
        if (!initializeUI(primaryStage).success()) {
            handleUILoadingError(primaryStage);
        }
    }

    @Override
    protected InitState initializeProperties() {
        boolean success;
        try {
            manager.loadProperties(ApplicationTemplate.class,
                                   AppInitSettings.PROPERTIES_XML_FILE.getParameterName(),
                                   AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            manager.loadProperties(ApplicationTemplate.class,
                                   AppInitSettings.WORKSPACE_XML_FILE.getParameterName(),
                                   AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            manager.loadProperties(ApplicationTemplate.class,
                    AppInitSettings.ALGORITHMS_ALGORITHMCONFIG_ALGORITHM_CONFIGURATION_DIALOG_XML.getParameterName(),
                    AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            manager.loadProperties(ApplicationTemplate.class,
                    AppInitSettings.ALGORITHMS_ALGORITHMCONFIG_CLUSTERER_CONFIGURATION_DIALOG_XML.getParameterName(),
                    AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            manager.loadProperties(ApplicationTemplate.class,
                    AppInitSettings.API_ALGORITHM_API_XML.getParameterName(),
                    AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            manager.loadProperties(ApplicationTemplate.class,
                    AppInitSettings.API_PLOT_API_XML.getParameterName(),
                    AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            manager.loadProperties(ApplicationTemplate.class,
                    AppInitSettings.CORE_APP_ACTION_XML.getParameterName(),
                    AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            manager.loadProperties(ApplicationTemplate.class,
                    AppInitSettings.CORE_APP_UI_XML.getParameterName(),
                    AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            manager.loadProperties(ApplicationTemplate.class,
                    AppInitSettings.LIB_SELECTION_ALGORITHM_SELECTION_MENU_XML.getParameterName(),
                    AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            success = true;
        } catch (InvalidXMLFileFormatException e) {
            success = false;
        }
        return new InitState(success);
    }

    protected InitState initializeUI(Stage primaryStage) {
        boolean success;
        UI ui = new AppUI(primaryStage, manager);
        try {
            ui.initialize();
            success = true;
        } catch (UnsupportedOperationException e) {
            success = false;
        }
        return new InitState(success);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

