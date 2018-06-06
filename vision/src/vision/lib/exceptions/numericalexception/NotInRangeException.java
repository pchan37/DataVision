package vision.lib.exceptions.numericalexception;

/**
 * This exception is to be thrown when one expects an value to be within a certain range, but that wasn't the case
 *
 * @author Patrick Chan
 */
public class NotInRangeException extends NumericBoundException {

    /** The reason for why the exception was thrown */
    private ErrorValue errorValue;

    /** Constants for why the exception was thrown */
    public enum ErrorValue {

        BELOW_MINIMUM_VALUE,
        ABOVE_MAXIMUM_VALUE

    }

    /** Creates an instance of the NotInRangeException class in cases when the reason is unknown */
    public NotInRangeException() {

    }

    /**
     * Creates an instance of the NotInRangeException class that takes a {@link ErrorValue} parameter for why the
     * exception was thrown.
     *
     * @param errorValue The reason for why the exception was thrown as a {@link ErrorValue}
     */
    public NotInRangeException(ErrorValue errorValue) {
        this.errorValue = errorValue;
    }

    /**
     * Returns the reason for why the exception was thrown
     * @return The reason for why the exception was thrown as a {@link ErrorValue}
     */
    public ErrorValue getErrorValue() {
        return errorValue;
    }

}
