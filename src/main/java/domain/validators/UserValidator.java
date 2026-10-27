package domain.validators;

import domain.User;
import exceptions.ValidationException;

/**
 * Validation strategy for users. A user is valid if:
 * the id is positive; the username has 3-30 characters (letters, digits, '.', '_', '-');
 * the first and last name start with an uppercase letter and contain only letters, spaces or '-';
 * the email has the form something@something.something.
 */
public class UserValidator implements Validator<User> {

    /**
     * All the errors are reported at once, in a single ValidationException.
     *
     * @param user the user to be validated
     * @throws ValidationException if the user is null or not valid
     */
    @Override
    public void validate(User user) {
        if (user == null) {
            throw new ValidationException("The user must not be null.");
        }
        StringBuilder errors = new StringBuilder();

        if (user.getId() == null || user.getId() <= 0) {
            errors.append("The id must be a positive number. ");
        }
        if (user.getUsername() == null || !user.getUsername().matches("[A-Za-z0-9._-]{3,30}")) {
            errors.append("The username must have 3-30 characters (letters, digits, '.', '_', '-'). ");
        }
        if (!isValidName(user.getFirstName())) {
            errors.append("The first name must start with an uppercase letter and contain only letters. ");
        }
        if (!isValidName(user.getLastName())) {
            errors.append("The last name must start with an uppercase letter and contain only letters. ");
        }
        if (user.getEmail() == null || !user.getEmail().matches("[^@\\s;]+@[^@\\s;]+\\.[^@\\s;]+")) {
            errors.append("The email is invalid. ");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString().trim());
        }
    }

    private static boolean isValidName(String name) {
        return name != null && name.matches("\\p{Lu}[\\p{L} -]*");
    }
}
