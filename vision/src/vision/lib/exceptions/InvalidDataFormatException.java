package vision.lib.exceptions;

/**
 * This exception is thrown when the data is not in the format expected.
 *
 * @author Patrick Chan
 */
public class InvalidDataFormatException extends Exception {

    /** The reason for why the exception was thrown */
    private String reason;

    /** A constant for when the reason is unknown */
    public static final String UNKNOWN_REASON = "";

    /**
     * Creates an instance of the InvalidDataFormatException class that takes a {@link String} parameter which specifies
     * the reason this exception was thrown.
     *
     * @param reason
     */
    public InvalidDataFormatException(String reason) {
        this.reason = reason;
    }

    /**
     * Return the reason for why the exception is thrown
     *
     * @return The reason for why the exception is thrown
     */
    public String getReason() {
        return reason;
    }

}
