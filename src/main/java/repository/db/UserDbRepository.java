package repository.db;

import domain.User;
import domain.validators.Validator;
import repository.Repository;

/**
 * Database repository for users. Skeleton only: the JDBC code is written in lab 5.
 */
public class UserDbRepository implements Repository<Long, User> {
    private static final String NOT_IMPLEMENTED = "Database persistence is implemented in lab 5.";

    private final Validator<User> validator;
    private final String url;
    private final String user;
    private final String password;

    /**
     * @param validator the validation strategy for users
     * @param url       the JDBC url of the database
     * @param user      the database user
     * @param password  the database password
     */
    public UserDbRepository(Validator<User> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always (implemented in lab 5)
     */
    @Override
    public User findOne(Long id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always (implemented in lab 5)
     */
    @Override
    public Iterable<User> findAll() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always (implemented in lab 5)
     */
    @Override
    public User save(User entity) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always (implemented in lab 5)
     */
    @Override
    public User delete(Long id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always (implemented in lab 5)
     */
    @Override
    public User update(User entity) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}
