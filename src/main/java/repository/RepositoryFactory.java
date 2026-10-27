package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.Validator;
import exceptions.RepositoryException;
import repository.db.FriendshipDbRepository;
import repository.db.UserDbRepository;
import repository.file.FileRepository;
import repository.memory.InMemoryRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * Simple Factory: the only class of the application that knows the concrete repositories.
 * The persistence type and its parameters are read from config.properties (on the classpath,
 * from src/main/resources); both repositories are created for the same persistence type.
 * Lab 4: instead of a switch, each RepositoryType is mapped to a Function that builds the
 * repository for the validator it receives; a new type only means adding an entry to the map.
 */
public class RepositoryFactory {
    private static final String CONFIG_FILE = "config.properties";

    private final Properties config = new Properties();
    private final RepositoryType type;
    private final Map<RepositoryType, Function<Validator<User>, Repository<Long, User>>> userFactories;
    private final Map<RepositoryType, Function<Validator<Friendship>, Repository<Tuple<Long, Long>, Friendship>>>
            friendshipFactories;

    /**
     * Loads the configuration and reads the persistence type (key repository.type).
     *
     * @throws RepositoryException if config.properties is missing or unreadable,
     *                             or repository.type is missing or unknown
     */
    public RepositoryFactory() {
        InputStream input = RepositoryFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (input == null) {
            throw new RepositoryException("The configuration file " + CONFIG_FILE
                    + " does not exist in src/main/resources.");
        }
        try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            config.load(reader);
        } catch (IOException e) {
            throw new RepositoryException("Cannot read " + CONFIG_FILE + ": " + e.getMessage(), e);
        }

        String value = require("repository.type");
        try {
            type = RepositoryType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RepositoryException("Unknown value for repository.type: '" + value
                    + "'. Allowed values: MEMORY, FILE, DATABASE.", e);
        }

        userFactories = Map.<RepositoryType, Function<Validator<User>, Repository<Long, User>>>of(
                RepositoryType.MEMORY, InMemoryRepository::new,
                RepositoryType.FILE, validator -> new FileRepository<>(validator, require("file.users"),
                        this::lineToUser, this::userToLine),
                RepositoryType.DATABASE, validator -> new UserDbRepository(validator, require("db.url"),
                        require("db.user"), require("db.password"))
        );
        friendshipFactories = Map
                .<RepositoryType, Function<Validator<Friendship>, Repository<Tuple<Long, Long>, Friendship>>>of(
                        RepositoryType.MEMORY, InMemoryRepository::new,
                        RepositoryType.FILE, validator -> new FileRepository<>(validator, require("file.friendships"),
                                this::lineToFriendship, this::friendshipToLine),
                        RepositoryType.DATABASE, validator -> new FriendshipDbRepository(validator, require("db.url"),
                                require("db.user"), require("db.password"))
                );
    }

    /**
     * @param validator the validation strategy for users
     * @return the user repository for the configured persistence type
     * @throws RepositoryException if a required configuration value is missing,
     *                             or the user file cannot be loaded
     */
    public Repository<Long, User> createUserRepository(Validator<User> validator) {
        return userFactories.get(type).apply(validator);
    }

    /**
     * @param validator the validation strategy for friendships
     * @return the friendship repository for the configured persistence type
     * @throws RepositoryException if a required configuration value is missing,
     *                             or the friendship file cannot be loaded
     */
    public Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(Validator<Friendship> validator) {
        return friendshipFactories.get(type).apply(validator);
    }

    // conversii linie <-> entitate pentru FileRepository; formatul nu se schimba fata de lab 3

    private User lineToUser(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != 5) {
            throw new IllegalArgumentException("must have 5 fields (id;username;firstName;lastName;email), has "
                    + fields.length + ".");
        }
        return new User(Long.parseLong(fields[0].trim()), fields[1].trim(), fields[2].trim(),
                fields[3].trim(), fields[4].trim());
    }

    private String userToLine(User user) {
        return user.getId() + ";" + user.getUsername() + ";" + user.getFirstName() + ";" + user.getLastName()
                + ";" + user.getEmail();
    }

    private Friendship lineToFriendship(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != 3) {
            throw new IllegalArgumentException("must have 3 fields (idUser1;idUser2;friendshipDate), has "
                    + fields.length + ".");
        }
        return new Friendship(Long.parseLong(fields[0].trim()), Long.parseLong(fields[1].trim()),
                LocalDateTime.parse(fields[2].trim()));
    }

    private String friendshipToLine(Friendship friendship) {
        return friendship.getIdUser1() + ";" + friendship.getIdUser2() + ";" + friendship.getDate();
    }

    private String require(String key) {
        String value = config.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new RepositoryException("Missing value for '" + key + "' in " + CONFIG_FILE + ".");
        }
        return value.trim();
    }
}
