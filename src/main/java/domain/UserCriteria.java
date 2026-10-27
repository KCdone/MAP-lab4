package domain;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Search criteria for users. Each method returns a Predicate&lt;User&gt; (not a boolean),
 * so the criteria can be combined with and, or, negate or Predicate.not before being
 * passed to Repository.findAll(Predicate). No findByXxx method is added to the repository.
 */
public class UserCriteria {

    private UserCriteria() {
    }

    /**
     * @param lastName the last name to look for (case insensitive)
     * @return a predicate that matches the users with this last name
     */
    public static Predicate<User> byLastName(String lastName) {
        return user -> user.getLastName().equalsIgnoreCase(lastName);
    }

    /**
     * @param prefix the prefix of the first name to look for (case insensitive)
     * @return a predicate that matches the users whose first name starts with the prefix
     */
    public static Predicate<User> byFirstNameStartingWith(String prefix) {
        return user -> user.getFirstName().toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * @param idsOfUsersWithFriends the ids of the users that have at least one friend
     *                              (see NetworkService.usersWithFriends())
     * @return a predicate that matches the users with no friends
     */
    public static Predicate<User> withoutFriends(Set<Long> idsOfUsersWithFriends) {
        return user -> !idsOfUsersWithFriends.contains(user.getId());
    }
}
