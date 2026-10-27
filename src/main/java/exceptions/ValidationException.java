package exceptions;

/**
 * Thrown when an entity or a value given by the user is not valid.
 */
public class ValidationException extends SocialNetworkException {

    /**
     * @param message the description of the validation errors
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * @param message the description of the validation errors
     * @param cause   the original exception (exception chaining)
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
