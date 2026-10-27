package exceptions;

/**
 * Thrown when a requested user or friendship does not exist.
 */
public class EntityNotFoundException extends SocialNetworkException {

    /**
     * @param message what was not found
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
