package vision.lib.exceptions.numericalexception;

/**
 * This exception is to be thrown when one expects an value to be a non-zero value, but that wasn't the case
 *
 * @author Patrick Chan
 */
public class MustBeNonZeroException extends NumericBoundException {

    /**
     * Creates an instance of the MustBeNonZeroException class
     */
    public MustBeNonZeroException() {

    }

    /**
     * Creates an instance of the MustBeNonZeroException class that takes a {@link String} parameter for the reason
     * whey the exception was thrown.
     *
     * @param reason The reason why the exception was thrown
     */
    public MustBeNonZeroException(String reason) {
        super(reason);
    }

}
