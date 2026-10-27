package domain.validators;

import domain.Friendship;
import domain.User;
import exceptions.ValidationException;
import repository.Repository;

/**
 * Validation strategy for friendships. A friendship is valid if both users are given,
 * are different, exist in the user repository, and the date is set.
 * Because the existence check is done here, friendships loaded from the file
 * that point to missing users are rejected too, so the two files stay consistent.
 */
public class FriendshipValidator implements Validator<Friendship> {
    private final Repository<Long, User> userRepo;

    /**
     * @param userRepo the repository used to check that both users exist
     */
    public FriendshipValidator(Repository<Long, User> userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * All the errors are reported at once, in a single ValidationException.
     *
     * @param friendship the friendship to be validated
     * @throws ValidationException if the friendship is null or not valid
     */
    @Override
    public void validate(Friendship friendship) {
        if (friendship == null) {
            throw new ValidationException("The friendship must not be null.");
        }
        StringBuilder errors = new StringBuilder();
        Long id1 = friendship.getIdUser1();
        Long id2 = friendship.getIdUser2();

        if (id1 == null || id2 == null) {
            errors.append("Both users must be specified. ");
        } else {
            if (id1.equals(id2)) {
                errors.append("A user cannot be friends with himself. ");
            }
            if (userRepo.findOne(id1) == null) {
                errors.append("There is no user with id " + id1 + ". ");
            }
            if (!id1.equals(id2) && userRepo.findOne(id2) == null) {
                errors.append("There is no user with id " + id2 + ". ");
            }
        }
        if (friendship.getDate() == null) {
            errors.append("The friendship date must be specified. ");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString().trim());
        }
    }
}
