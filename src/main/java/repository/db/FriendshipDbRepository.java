package repository.db;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;
import repository.Repository;

/**
 * Database repository for friendships. Skeleton only: the JDBC code is written in lab 5.
 */
public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private static final String NOT_IMPLEMENTED = "Database persistence is implemented in lab 5.";

    private final Validator<Friendship> validator;
    private final String url;
    private final String user;
    private final String password;

    /**
     * @param validator the validation strategy for friendships
     * @param url       the JDBC url of the database
     * @param user      the database user
     * @param password  the database password
     */
    public FriendshipDbRepository(Validator<Friendship> validator, String url, String user, String password) {
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
    public Friendship findOne(Tuple<Long, Long> id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always (implemented in lab 5)
     */
    @Override
    public Iterable<Friendship> findAll() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always (implemented in lab 5)
     */
    @Override
    public Friendship save(Friendship entity) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always (implemented in lab 5)
     */
    @Override
    public Friendship delete(Tuple<Long, Long> id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always (implemented in lab 5)
     */
    @Override
    public Friendship update(Friendship entity) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}
