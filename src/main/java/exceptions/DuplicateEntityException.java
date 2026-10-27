package exceptions;

/**
 * Thrown when something that already exists is added again
 * (a username already in use, two users that are already friends).
 */
public class DuplicateEntityException extends SocialNetworkException {

    /**
     * @param message what already exists
     */
    public DuplicateEntityException(String message) {
        super(message);
    }
}
