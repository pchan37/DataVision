package vision.lib.exceptions;

import java.nio.file.Path;

/**
 * This exception is thrown when there is an error when one is loading data from a path
 *
 * @author Patrick Chan
 */
public class LoadErrorException extends Exception {

    /** The path that resulted in a {@link LoadErrorException} */
    private Path path;
    /** The reason for why the exception was thrown */
    private String reason;

    /** Constant for when the reason is unknown */
    public static final String UNKNOWN_REASON = "";

    /**
     * Creates an instance of the LoadErrorException class that takes a {@link Path} parameter which specifies the path
     * that led to the throwing of this exception and a {@link String} parameter which specifies the reason why the
     * exception was thrown.
     *
     * @param path The {@link Path} that led to the throwing of this exception.
     * @param reason The reason that the exception was thrown
     */
    public LoadErrorException(Path path, String reason) {
        this.path = path;
        this.reason = reason;
    }

    /**
     * Return the path that led to the throwing of this exception
     *
     * @return the path that led to the throwing of this exception
     */
    public Path getPath() {
        return path;
    }

    /**
     * Return the reason for why the exception was thrown
     *
     * @return The reason for why the exception was thrown
     */
    public String getReason() {
        return reason;
    }

}
