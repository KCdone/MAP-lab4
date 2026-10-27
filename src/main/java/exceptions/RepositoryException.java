package exceptions;

/**
 * Thrown when the data cannot be persisted or loaded (unreadable file, corrupt line,
 * invalid configuration). Wraps the original exception (IOException, NumberFormatException,
 * DateTimeParseException etc.) as cause.
 */
public class RepositoryException extends SocialNetworkException {

    /**
     * @param message the description of the error
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * @param message the description of the error
     * @param cause   the original exception (exception chaining)
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
