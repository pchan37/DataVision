package vision.lib.exceptions.numericalexception;

/**
 * This exception is thrown when one expects a value to follow some numerical bounds, but wasn't the case.  This class
 * also serves as the base class for more specific exceptions of this type.
 *
 * @author Patrick Chan
 */
public class NumericBoundException extends Exception {

    /** The reason for which the exception is thrown */
    protected String reason;

    /**
     * Creates an instance of the NumericBoundException class without specifying a reason.
     *
     * @see NumericBoundException#NumericBoundException(String)
     */
    public NumericBoundException() {

    }

    /**
     * Creates an instance of the NumericBoundException class that takes a {@link String} parameter which specify the
     * reason for the exception.
     *
     * @param reason Specifies the reason for why the exception was thrown
     * @see NumericBoundException#NumericBoundException()
     */
    public NumericBoundException(String reason) {
        this.reason = reason;
    }

    /**
     * Return the reason of the exception
     *
     * @return The reason of the exception
     */
    public String getReason() {
        return reason;
    }
}
