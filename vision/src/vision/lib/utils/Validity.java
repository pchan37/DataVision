package vision.lib.utils;

/**
 * A wrapper class around a boolean used to convey whether an operation was valid or
 * not in a more readable way.
 *
 * @author Patrick Chan
 */
public class Validity {

    /** A boolean storing the state of whether an operation was successful or not */
    protected boolean isValid;
    /** A {@link String} storing the error message if any */
    protected String error;

    public Validity() {

    }

    /**
     * Creates an instance of the Validity class with a boolean parameter for denoting
     * whether the operation was valid and a {@link String} parameter for the error message.
     *
     * @param isValid whether the operation was successful or not
     * @param error the error message
     */
    public Validity(boolean isValid, String error) {
        this.isValid = isValid;
        this.error = error;
    }

    /**
     * Main API method for determining if the operation was valid or not
     *
     * @return whether the operation was valid or not
     */
    public boolean valid() {
        return isValid;
    }

    /**
     * Main API method for determining what the corresponding error was
     *
     * @return the error
     */
    public String getError() {
        return error;
    }

}