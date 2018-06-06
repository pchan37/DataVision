package vision.utils.propertymanager;

import java.net.URL;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import vision.utils.settings.AppInitSettings;
import vision.utils.xmlutils.InvalidXMLFileFormatException;
import vision.utils.xmlutils.XMLUtilities;

/**
 * This class gives you access to the properties defined in the xml files.  It uses XMLUtilities to
 * load the data from the xml files. The majority of the code is taken from Professor Banerjee.
 * 
 * @author Professor Banerjee
 * @author Patrick Chan
 */
public class PropertyManager {

    private static final XMLUtilities xmlUtilities = new XMLUtilities();
    private static PropertyManager propertyManager;
    private Map<String, String> properties;
    private Map<String, List<String>> propertyOptions;

    // Constant for the path separator (can't put in xml files as they hasn't been loaded)
    public static final String SEPARATOR = "/";
    
    // Constants required to load the elements and their properties from the XML properties file
    public static final String PROPERTY_ELEMENT              = "property";
    public static final String PROPERTY_LIST_ELEMENT         = "property_list";
    public static final String PROPERTY_OPTIONS_LIST_ELEMENT = "property_options_list";
    public static final String PROPERTY_OPTIONS_ELEMENT      = "property_options";
    public static final String OPTION_ELEMENT                = "option";
    public static final String NAME_ATTRIBUTE                = "name";
    public static final String VALUE_ATTRIBUTE               = "value";

    /** Path of the properties resource folder, relative to the root resource folder for the application */
    public static final String PROPERTIES_RESOURCE_RELATIVE_PATH = "properties";
    
    private PropertyManager() {
        if (propertyManager != null) {
            throw new IllegalStateException("This is a singleton!");
        }        
        properties = new HashMap<>();
        propertyOptions = new HashMap<>();
    }

    public static PropertyManager getManager() {
        if (propertyManager == null) {
            propertyManager = new PropertyManager();
            try {
                propertyManager.loadProperties(propertyManager.getClass(),
                                               AppInitSettings.PROPERTIES_XML_FILE.getParameterName(),
                                               AppInitSettings.SCHEMA_DEFINITION_FILE.getParameterName());
            } catch (InvalidXMLFileFormatException e) {
                propertyManager = null;
            }
        }
        return propertyManager;
    }    

    public void addProperty(String property, String value) {
        properties.put(property, value);
    }

    public String getPropertyValue(String property) {
        return properties.get(property);
    }

    public int getPropertyValueAsInt(String property) throws NullPointerException, NumberFormatException {
        return Integer.parseInt(properties.get(property));
    }

    public boolean getPropertyValueAsBoolean(String property) {
        return Boolean.parseBoolean(properties.get(property));
    }
       
    public void addPropertyOption(String property, String option) {
        if (properties.get(property) == null)
            throw new NoSuchElementException(String.format("Property \"%s\" does not exist.", property));
        List<String> propertyoptionslist = propertyOptions.get(property);
        if (propertyoptionslist == null)
            propertyoptionslist = new ArrayList<>();
        propertyoptionslist.add(option);
        propertyOptions.put(property, propertyoptionslist);
    }

    public List<String> getPropertyOptions(String property) {
        if (properties.get(property) == null)
            throw new NoSuchElementException(String.format("Property \"%s\" does not exist.", property));
        return propertyOptions.get(property);
    }

    public boolean hasProperty(Object property) {
        return properties.get(property.toString()) != null;
    }

    public void loadProperties(Class c, String xmlFilename, String schemaFilename) throws InvalidXMLFileFormatException {
        URL xmlFileResource = c.getClassLoader()
            .getResource(PROPERTIES_RESOURCE_RELATIVE_PATH + SEPARATOR + xmlFilename);
        URL schemaFileResource = c.getClassLoader()
            .getResource(PROPERTIES_RESOURCE_RELATIVE_PATH + SEPARATOR + schemaFilename);

        Document   document         = xmlUtilities.loadXMLDocument(xmlFileResource, schemaFileResource);
        Node       propertyListNode = xmlUtilities.getNodeWithName(document, PROPERTY_LIST_ELEMENT);
        List<Node> propNodes        = xmlUtilities.getChildrenWithName(propertyListNode, PROPERTY_ELEMENT);
        for (Node n : propNodes) {
            NamedNodeMap attributes = n.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                String attName  = attributes.getNamedItem(NAME_ATTRIBUTE).getTextContent();
                String attValue = attributes.getNamedItem(VALUE_ATTRIBUTE).getTextContent();
                properties.put(attName, attValue);
            }
        }

        Node propertyOptionsListNode = xmlUtilities.getNodeWithName(document, PROPERTY_OPTIONS_LIST_ELEMENT);
        if (propertyOptionsListNode != null) {
            List<Node> propertyOptionsNodes = xmlUtilities.getChildrenWithName(propertyOptionsListNode,
                                                                               PROPERTY_OPTIONS_ELEMENT);
            for (Node n : propertyOptionsNodes) {
                NamedNodeMap      attributes = n.getAttributes();
                String            name       = attributes.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
                ArrayList<String> options    = new ArrayList<>();
                propertyOptions.put(name, options);
                List<Node> optionsNodes = xmlUtilities.getChildrenWithName(n, OPTION_ELEMENT);
                for (Node oNode : optionsNodes) {
                    String option = oNode.getTextContent();
                    options.add(option);
                }
            }
        }
    }
    
}
