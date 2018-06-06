package vision.lib.utils;

/**
 * A wrapper class around a boolean used to convey whether a file operation was successful or
 * not in a more readable way.
 *
 * @author Patrick Chan
 */
public class FileOperationSuccess {

    /** A boolean storing whether the file operation was successful or not */
    private boolean isSuccessful;

    /**
     * Creates an instance of the FileOperationSuccess class that takes a boolean parameter to
     * denote whether the file operation was successful or not.
     *
     * @param isSuccessful whether the file operation was successful or not
     */
    public FileOperationSuccess(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    /**
     * Main API method for determining if the file operation was successful
     *
     * @return whether the file operation was successful or not
     */
    public boolean success() {
        return isSuccessful;
    }

}