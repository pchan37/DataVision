package vision.lib.exceptions.numericalexception;

/**
 * This exception is to be thrown when one expects an value to be a non-negative value, but that wasn't the case
 *
 * @author Patrick Chan
 */
public class MustBeNonNegativeException extends NumericBoundException {

    /**
     * Creates an instance of the MustBeNonNegativeException class
     */
    public MustBeNonNegativeException() {}

    /**
     * Creates an instance of the MustBeNonNegativeException class that takes a {@link String} parameter for the reason
     * whey the exception was thrown.
     *
     * @param reason The reason why the exception was thrown
     */
    public MustBeNonNegativeException(String reason) {
        super(reason);
    }

}
