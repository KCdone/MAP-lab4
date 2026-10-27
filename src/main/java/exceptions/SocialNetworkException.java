package exceptions;

/**
 * Base unchecked exception of the application. All the custom exceptions extend it,
 * so the user interface can handle them in a single catch.
 */
public class SocialNetworkException extends RuntimeException {

    /**
     * @param message the description of the error
     */
    public SocialNetworkException(String message) {
        super(message);
    }

    /**
     * @param message the description of the error
     * @param cause   the original exception (exception chaining)
     */
    public SocialNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
