package datavision.core;

import org.junit.Test;
import vision.core.ApplicationTemplate;
import vision.utils.propertymanager.PropertyManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class AppActionTest {

    /**
     * Ensure that the application is able to save file correctly by saving the data and comparing it
     * to the data read from the file.
     */
    @Test
    public void saveDataStringTest() throws IOException, URISyntaxException {
        AppData appData = new AppData(PropertyManager.getManager());
        Path rootPath = Paths.get(AppData.class.getResource("/datavision").toURI());
        Path directoryPath = Paths.get(rootPath.toString(), "resources/data");
        directoryPath.toFile().mkdirs();
        Path path = Paths.get(directoryPath.toString(), "oneLiner.tsd");
        System.out.println(path);
        String content = "@hello    bye 2,3";

        appData.saveToFile(path, content);
        assertEquals(Files.readAllLines(path).get(0), content);
    }

}