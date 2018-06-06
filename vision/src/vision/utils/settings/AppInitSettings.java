package vision.utils.settings;

/**
 * This is the set of parameters necessary for the initialization of a Vision application.
 * Portions of this code is inspired by Professor Banerjee.
 * 
 * @author Patrick Chan
 * @author Professor Banerjee
 */
public enum AppInitSettings {

    // Title for any loading error during application initialization
    APP_LOAD_ERROR_TITLE("Load Error"),

    // Standard error messages when properties can't be loaded during start-up
    PROPERTIES_LOAD_ERROR_MESSAGE("An error occurred while loading the property file."),

    // The framework-level XML properties file
    PROPERTIES_XML_FILE("properties.xml"),

    // The app-level XML properties file
    WORKSPACE_XML_FILE("app-properties.xml"),

    // The schema definition for the properties file
    SCHEMA_DEFINITION_FILE("property-schema.xsd"),

    // Title for error dialogs informing of unsupported operations on the template level
    NOT_SUPPORTED_FOR_TEMPLATE_ERROR_TITLE("Operation Not Supported for Templates"),

    // Message for error dialogs informing of unsupported operations on the template level
    OPERATION_NOT_SUPPORTED("This operation must be implemented in a child class."),

    // Message to be displayed when user tries to initialize UITemplate
    NOT_INITIALIZABLE_MSG("The user interface cannot be initialized at template-level. A child class should be implemented."),

    ALGORITHMS_ALGORITHMCONFIG_ALGORITHM_CONFIGURATION_DIALOG_XML("algorithms/algorithmconfig/AlgorithmConfigurationDialog-properties.xml"),
    ALGORITHMS_ALGORITHMCONFIG_CLUSTERER_CONFIGURATION_DIALOG_XML("algorithms/algorithmconfig/ClustererConfigurationDialog-properties.xml"),

    API_ALGORITHM_API_XML("api/AlgorithmAPI-properties.xml"),
    API_PLOT_API_XML("api/PlotAPI-properties.xml"),

    CORE_APP_ACTION_XML("core/AppAction-properties.xml"),
    CORE_APP_UI_XML("core/AppUI-properties.xml"),

    LIB_SELECTION_ALGORITHM_SELECTION_MENU_XML("lib/selection/AlgorithmSelectionMenu-properties.xml");

    private String parameterName;

    AppInitSettings(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }
    
}
