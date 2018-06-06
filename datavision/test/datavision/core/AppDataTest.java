package datavision.core;

import datavision.algorithms.DataSet;
import javafx.geometry.Point2D;
import org.junit.Test;
import vision.core.ApplicationTemplate;
import vision.utils.propertymanager.PropertyManager;

import static org.junit.Assert.*;

public class AppDataTest {

    /**
     * Test the parsing of a single line of TSD-formatted data with boundary values.
     *
     * The selected boundary values were single characters (a for the label and b for the name)
     * because that is the smallest amount of characters you can have before the labels and names
     * are treated as invalid.
     */
    @Test
    public void testParsingSingleLine() {
        String data = "@a\tb\t2,3";
        AppData appData = new AppData(PropertyManager.getManager());
        DataSet dataSet = appData.generateDataSetFromDataString(data);

        assertEquals("b", dataSet.getLabels().get("@a"));
        assertEquals(new Point2D(2,3), dataSet.getPoints().get("@a"));
    }

}