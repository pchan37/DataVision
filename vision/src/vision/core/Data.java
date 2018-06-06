package vision.core;

import vision.lib.utils.FileOperationSuccess;
import vision.lib.utils.Validity;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The abstract base class for the data handling side of the application
 */
public abstract class Data {

    /**
     * A wrapper class around a boolean for a more readable way to determine if the result of a data validity check is
     * valid
     */
    public class DataValidity extends Validity {

        /**
         * Creates an instance of the DataValidity class given a boolean parameter specifying whether the data is valid
         * or not and a {@link String} parameter for the reason of the error, if any.
         *
         * @param isValid Whether the data is valid or not as determined by a data validation function
         * @param error The reason for the error, if any
         */
        public DataValidity(boolean isValid, String error) {
            this.isValid = isValid;
            this.error = error;
        }

    }

    /**
     * Treat the given {@link String} parameter as data and load it as necessary in the specific application
     *
     * @param data The data to be loaded represented as a {@link String}
     */
    public abstract void loadFromString(String data);

    /**
     * Load the data from the given {@link Path} parameter into an output as determined by the specific application
     *
     * @param filePath The {@link Path} from which to load the data from
     * @return Whether the operation was successful or not via {@link FileOperationSuccess}
     * @throws IOException When there is an error in reading from a file
     */
    public abstract FileOperationSuccess loadFromFile(Path filePath) throws IOException;

    /**
     * Save the data to the given {@link Path} parameter as determined by the specific application
     *
     * @param filePath The {@link Path} from which to save the data to
     * @return Whether the operation was successful or not via {@link FileOperationSuccess}
     * @throws IOException When there is an error in writing to a file
     */
    public abstract FileOperationSuccess saveToFile(Path filePath) throws IOException;

    /**
     * Clear the data
     */
    public abstract void clear();

    /**
     * Treat the given {@link String} parameter as data and validates it
     *
     * @param dataString The data to be validated
     * @return whether the data is valid or not vai {@link DataValidity}
     */
    public abstract DataValidity checkData(String dataString);

}
