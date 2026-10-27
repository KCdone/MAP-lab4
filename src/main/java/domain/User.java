package domain;

/**
 * A user of the social network. The username is unique in the network
 * and is used to identify the user in the user interface.
 */
public class User extends Entity<Long> {
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    /**
     * @param id        the id of the user
     * @param username  the unique username
     * @param firstName the first name
     * @param lastName  the last name
     * @param email     the email address
     */
    public User(Long id, String username, String firstName, String lastName, String email) {
        setId(id);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the user as text
     */
    @Override
    public String toString() {
        return "User{id=" + getId() + ", username=" + username + ", " + firstName + " " + lastName
                + ", email=" + email + "}";
    }
}
