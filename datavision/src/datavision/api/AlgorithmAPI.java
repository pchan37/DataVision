package datavision.api;

import datavision.algorithms.algorithmadapters.AlgorithmAdapter;
import datavision.algorithms.algorithmconfig.AlgorithmConfigurationDialog;
import datavision.utils.settings.AlgorithmAPISettings;
import javafx.stage.Stage;
import vision.utils.propertymanager.PropertyManager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlgorithmAPI {

    public static AlgorithmAdapter getAlgorithmAdapter(String algorithmType, PlotAPI plotAPI, DataAPI dataAPI) {
        Map<String, String> algorithmAdaptersMap = getAlgorithmAdaptersMap();
        try {
            Class<?> klass = Class.forName(algorithmAdaptersMap.get(algorithmType));
            Constructor<?> constructor = klass.getConstructor(PlotAPI.class, DataAPI.class);
            return (AlgorithmAdapter)constructor.newInstance(plotAPI, dataAPI);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            // FIXME add proper exception handling
        }
        return null;
    }

    public static Class getAlgorithmRawClass(String algorithmType, String algorithmName) {
        try {
            String algorithmClassName = null;
            Map<String, List<String>> algorithmsMap = getAlgorithmsMap();
            for (String algorithm: algorithmsMap.get(algorithmType)) {
                if (algorithm.endsWith(algorithmName)) {
                    algorithmClassName = algorithm;
                }
            }
            Class<?> klass = Class.forName(algorithmClassName);
            return klass;
        } catch (ClassNotFoundException ex) {
            // FIXME add proper exception handling
        }
        return null;
    }

    public static AlgorithmConfigurationDialog getAlgorithmConfigurationDialog(String algorithmType, Stage owner, DataAPI dataAPI, String title) {
        Map<String, String> algorithmConfigurationDialogsMap = getAlgorithmConfigurationDialogsMap();
        try {
            Class<?> klass = Class.forName(algorithmConfigurationDialogsMap.get(algorithmType));
            Constructor<?> constructor = klass.getConstructor(Stage.class, DataAPI.class, String.class);
            return (AlgorithmConfigurationDialog)constructor.newInstance(owner, dataAPI, title);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            // FIXME add proper exception handling
        }
        return null;
    }

    public static Map<String, String> getAlgorithmAdaptersMap() {
        PropertyManager manager = PropertyManager.getManager();
        Path algorithmAdapterPath = getAlgorithmAdapterPath();

        String CLASS_EXTENSION = manager.getPropertyValue(AlgorithmAPISettings.CLASS_EXTENSION.name());
        List<String> pathList;
        try (Stream<Path> paths = Files.walk(algorithmAdapterPath)) {
            pathList = paths.filter(path -> path != null && path.toFile().isFile() && path.toString().split("\\.class").length == 1 && path.toString().endsWith(CLASS_EXTENSION))
                            .map(path -> path.getFileName().toString().split("\\.class")[0])
                            .collect(Collectors.toList());

            Map<String, String> algorithmAdaptersMap = new HashMap<>();
            for(String path: pathList) {
                String ALGORITHM_ADAPTER_CLASS_FORMAT_STRING = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_ADAPTER_CLASS_FORMAT_STRING.name());
                String algorithmAdapterClassName = String.format(ALGORITHM_ADAPTER_CLASS_FORMAT_STRING, path);
                try {
                    Class<?> klass = Class.forName(algorithmAdapterClassName);
                    if (!Modifier.isAbstract(klass.getModifiers())) {
                        String ALGORITHM_ADAPTER_SUFFIX = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_ADAPTER_SUFFIX.name());
                        algorithmAdaptersMap.putIfAbsent(path.substring(0, path.lastIndexOf(ALGORITHM_ADAPTER_SUFFIX)), algorithmAdapterClassName);
                    }
                } catch (ClassNotFoundException ex) {
                    // FIXME add proper exception handling
                    System.out.println(ex.getStackTrace());
                }
            }
            return algorithmAdaptersMap;
        } catch (IOException e) {

        }
        return null;
    }

    public static Map<String, List<String>> getAlgorithmsMap() {
        PropertyManager manager = PropertyManager.getManager();
        Path algorithmPath = getAlgorithmPath();

        String CLASS_EXTENSION = manager.getPropertyValue(AlgorithmAPISettings.CLASS_EXTENSION.name());
        List<Path> pathList;
        try (Stream<Path> paths = Files.walk(algorithmPath)) {
            pathList = paths.filter(path -> path != null && path.toFile().isFile() && path.toString().split("\\.class").length == 1 && path.toString().endsWith(CLASS_EXTENSION))
                            .collect(Collectors.toList());

            Map<String, List<String>> algorithmsMap = new HashMap<>();
            String ALGORITHM_DIRECTORY = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_DIRECTORY.name());
            String ALGORITHM_ADAPTER_DIRECTORY = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_ADAPTER_DIRECTORY.name());
            String ALGORITHM_CONFIGURATION_DIALOG_DIRECTORY = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_CONFIGURATION_DIALOG_DIRECTORY.name());
            for(Path path: pathList) {
                String rawDirectory = path.getName(path.getNameCount() - 2).toString();
                String directory = Character.toTitleCase(rawDirectory.charAt(0)) + rawDirectory.substring(1);
                if (rawDirectory.equals(ALGORITHM_DIRECTORY) || rawDirectory.equals(ALGORITHM_ADAPTER_DIRECTORY) || rawDirectory.equals(ALGORITHM_CONFIGURATION_DIALOG_DIRECTORY)) {
                    continue;
                }
                String algorithmName = path.getFileName().toString().split("\\.class")[0];
                String ALGORITHM_CLASS_FORMAT_STRING = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_CLASS_FORMAT_STRING.name());
                String algorithmClassName = String.format(ALGORITHM_CLASS_FORMAT_STRING, directory.toLowerCase(), algorithmName);
                try {
                    Class<?> klass = Class.forName(algorithmClassName);
                    if (!Modifier.isAbstract(klass.getModifiers())) {
                        if (algorithmsMap.containsKey(directory)) {
                            algorithmsMap.get(directory).add(algorithmClassName);
                        } else {
                            algorithmsMap.put(directory, new ArrayList<>());
                            algorithmsMap.get(directory).add(algorithmClassName);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex.getStackTrace());
                }
            }
            return algorithmsMap;
        } catch (IOException e) {

        }
        return null;
    }

    public static Map<String, String> getAlgorithmConfigurationDialogsMap() {
        PropertyManager manager = PropertyManager.getManager();
        Path algorithmConfigurationDialogPath = getAlgorithmConfigurationDialogPath();

        List<String> pathList;
        String CLASS_EXTESNION = manager.getPropertyValue(AlgorithmAPISettings.CLASS_EXTENSION.name());
        try (Stream<Path> paths = Files.walk(algorithmConfigurationDialogPath)) {
            pathList = paths.filter(path -> path != null && path.toFile().isFile() && path.toString().split("\\.class").length == 1 && path.toString().endsWith(CLASS_EXTESNION))
                    .map(path -> path.getFileName().toString().split("\\.class")[0])
                    .collect(Collectors.toList());

            Map<String, String> algorithmConfigurationDialogsMap = new HashMap<>();
            for(String path: pathList) {
                String ALGORITHM_CONFIG_CLASS_FORMAT_STRING = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_CONFIG_CLASS_FORMAT_STRING.name());
                String algorithmConfigurationDialogClassName = String.format(ALGORITHM_CONFIG_CLASS_FORMAT_STRING, path);
                try {
                    Class<?> klass = Class.forName(algorithmConfigurationDialogClassName);
                    if (!Modifier.isAbstract(klass.getModifiers())) {
                        String ALGORITHM_CONFIG_SUFFIX = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_CONFIG_SUFFIX.name());
                        algorithmConfigurationDialogsMap.putIfAbsent(path.substring(0, path.lastIndexOf(ALGORITHM_CONFIG_SUFFIX)), algorithmConfigurationDialogClassName);
                    }
                } catch (ClassNotFoundException ex) {
                    // FIXME add proper exception handling
                    System.out.println(ex.getStackTrace());
                }
            }
            return algorithmConfigurationDialogsMap;
        } catch (IOException e) {

        }
        return null;
    }

    private static Path getResourcePath(String path) {
        try {
            return Paths.get(AlgorithmAPI.class.getResource(path).toURI()).normalize();
        } catch (URISyntaxException e) {
            /*
             * FIXME
             * This exception shouldn't be thrown upon production release...  If there's time, we should implement a
             * better exception handling mechanism as we don't want to just silence it and cause issues during future
             * modifications.  However, for the final project milestone, this is considered low priority.
             */
            return null;
        }
    }

    private static Path getAlgorithmAdapterPath() {
        PropertyManager manager = PropertyManager.getManager();
        String ALGORITHM_ADAPTER_PATH = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_ADAPTER_PATH.name());
        return getResourcePath(ALGORITHM_ADAPTER_PATH);
    }

    private static Path getAlgorithmPath() {
        PropertyManager manager = PropertyManager.getManager();
        String ALGORITHM_PATH = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_PATH.name());
        return getResourcePath(ALGORITHM_PATH);
    }

    private static Path getAlgorithmConfigurationDialogPath() {
        PropertyManager manager = PropertyManager.getManager();
        String ALGORITHM_CONFIG_PATH = manager.getPropertyValue(AlgorithmAPISettings.ALGORITHM_CONFIG_PATH.name());
        return getResourcePath(ALGORITHM_CONFIG_PATH);
    }

}
