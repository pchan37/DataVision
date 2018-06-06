package vision.lib.exceptions.numericalexception;

/**
 * This exception is to be thrown when one expects an value to be a integer value, but that wasn't the case
 *
 * @author Patrick Chan
 */
public class MustBeIntegerException extends NumericBoundException {

    /**
     * Creates an instance of the MustBeIntegerException class
     */
    public MustBeIntegerException() {

    }

    /**
     * Creates an instance of the MustBeIntegerException class that takes a {@link String} parameter for the reason
     * whey the exception was thrown.
     *
     * @param reason The reason why the exception was thrown
     */
    public MustBeIntegerException(String reason) {
        super(reason);
    }

}
