package service;

import domain.Community;
import domain.Friendship;
import domain.Tuple;
import domain.User;
import exceptions.DuplicateEntityException;
import exceptions.EntityNotFoundException;
import exceptions.ValidationException;
import repository.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The logic of the application: users, friendships and communities.
 * Depends only on the Repository interface, so it does not know where the data is kept.
 * Checks the rules that involve more than one entity (unique username, existing users,
 * duplicate friendships); the format of a single entity is checked by the validators.
 */
public class NetworkService {
    private final Repository<Long, User> userRepo;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepo;

    /**
     * @param userRepo       the user repository
     * @param friendshipRepo the friendship repository
     */
    public NetworkService(Repository<Long, User> userRepo,
                          Repository<Tuple<Long, Long>, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    // ------------------------------------------------------------------ utilizatori

    /**
     * Adds a new user; the id is generated (the biggest id + 1).
     *
     * @param username  the username, unique in the network (case insensitive)
     * @param firstName the first name
     * @param lastName  the last name
     * @param email     the email address
     * @return the added user
     * @throws ValidationException                    if the username is blank or the data is not valid
     * @throws DuplicateEntityException               if the username is already used
     * @throws exceptions.RepositoryException         if the user cannot be persisted
     */
    public User addUser(String username, String firstName, String lastName, String email) {
        requireText(username, "The username");
        if (findByUsername(username).isPresent()) {
            throw new DuplicateEntityException("The username '" + username.trim() + "' is already used.");
        }
        User user = new User(nextUserId(), username.trim(), firstName, lastName, email);
        userRepo.save(user);
        return user;
    }

    /**
     * Removes a user together with all his friendships, so the data stays consistent.
     *
     * @param userId the id of the user
     * @return the removed user
     * @throws ValidationException            if userId is null
     * @throws EntityNotFoundException        if the user does not exist
     * @throws exceptions.RepositoryException if the change cannot be persisted
     */
    public User removeUser(Long userId) {
        User user = findUser(userId);

        Repository.stream(friendshipRepo.findAll())
                .filter(f -> f.involves(userId))
                .map(Friendship::getId)
                .collect(Collectors.toList())
                .forEach(friendshipRepo::delete);

        userRepo.delete(userId);
        return user;
    }

    /**
     * @return all the users
     */
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * @param userId the id of the user
     * @return the user with the given id
     * @throws ValidationException     if userId is null
     * @throws EntityNotFoundException if the user does not exist
     */
    public User findUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("The user id must be specified.");
        }
        return Optional.ofNullable(userRepo.findOne(userId))
                .orElseThrow(() -> new EntityNotFoundException("There is no user with id " + userId + "."));
    }

    /**
     * @param username the username (case insensitive)
     * @return the user with the given username
     * @throws ValidationException     if the username is blank
     * @throws EntityNotFoundException if there is no user with this username
     */
    public User findUserByUsername(String username) {
        requireText(username, "The username");
        return findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "There is no user with username '" + username.trim() + "'."));
    }

    /**
     * Searches among the users for the ones that match the given criteria
     * (built with CriteriiUtilizator and and/or/negate/Predicate.not).
     *
     * @param criteria the condition a user must meet
     * @return the matching users
     */
    public List<User> searchUsers(Predicate<User> criteria) {
        return userRepo.findAll(criteria);
    }

    /**
     * @return the ids of the users that have at least one friend, used by
     * CriteriiUtilizator.faraPrieteni
     */
    public Set<Long> usersWithFriends() {
        return Repository.stream(friendshipRepo.findAll())
                .flatMap(f -> Stream.of(f.getIdUser1(), f.getIdUser2()))
                .collect(Collectors.toSet());
    }

    // ------------------------------------------------------------------ prietenii

    /**
     * Creates a friendship between two existing users, dated now.
     *
     * @param username1 the username of the first user
     * @param username2 the username of the second user
     * @return the created friendship
     * @throws ValidationException            if a username is blank or both usernames are the same user
     * @throws EntityNotFoundException        if one of the users does not exist
     * @throws DuplicateEntityException       if the users are already friends
     * @throws exceptions.RepositoryException if the friendship cannot be persisted
     */
    public Friendship addFriendship(String username1, String username2) {
        User user1 = findUserByUsername(username1);
        User user2 = findUserByUsername(username2);
        if (user1.getId().equals(user2.getId())) {
            throw new ValidationException("A user cannot be friends with himself.");
        }
        if (friendshipRepo.findOne(Friendship.idFor(user1.getId(), user2.getId())) != null) {
            throw new DuplicateEntityException(user1.getUsername() + " and " + user2.getUsername()
                    + " are already friends.");
        }
        Friendship friendship = new Friendship(user1.getId(), user2.getId(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        friendshipRepo.save(friendship);
        return friendship;
    }

    /**
     * Removes the friendship between two users.
     *
     * @param username1 the username of the first user
     * @param username2 the username of the second user
     * @return the removed friendship
     * @throws ValidationException            if a username is blank
     * @throws EntityNotFoundException        if one of the users does not exist or they are not friends
     * @throws exceptions.RepositoryException if the change cannot be persisted
     */
    public Friendship removeFriendship(String username1, String username2) {
        User user1 = findUserByUsername(username1);
        User user2 = findUserByUsername(username2);
        return Optional.ofNullable(friendshipRepo.delete(Friendship.idFor(user1.getId(), user2.getId())))
                .orElseThrow(() -> new EntityNotFoundException(user1.getUsername() + " and " + user2.getUsername()
                        + " are not friends."));
    }

    /**
     * @return all the friendships
     */
    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepo.findAll();
    }

    /**
     * @param userId the id of a user
     * @return the friends of the user
     */
    public List<User> friendsOf(Long userId) {
        return Repository.stream(friendshipRepo.findAll())
                .filter(f -> f.involves(userId))
                .map(f -> f.getIdUser1().equals(userId) ? f.getIdUser2() : f.getIdUser1())
                .map(this::findUser)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------ comunitati

    /**
     * The number of communities = the number of connected components of the network graph.
     * A user without friends is a community by himself.
     *
     * @return the number of communities
     */
    public int countCommunities() {
        return findComponents(buildGraph()).size();
    }

    /**
     * The most sociable community = the connected component that contains the longest path.
     * A path does not visit the same user twice; its length is the number of friendships on it.
     *
     * @return the community, or an empty Optional if the network has no users
     */
    public Optional<Community> mostSociableCommunity() {
        Map<Long, Set<Long>> graph = buildGraph();
        return findComponents(graph).stream()
                .map(component -> toCommunity(component, graph))
                .max(Comparator.comparingInt(Community::longestPath));
    }

    // ------------------------------------------------------------------ metode ajutatoare

    private void requireText(String value, String what) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(what + " must not be empty.");
        }
    }

    private Optional<User> findByUsername(String username) {
        String wanted = username.trim();
        return Repository.stream(userRepo.findAll())
                .filter(u -> u.getUsername().equalsIgnoreCase(wanted))
                .findFirst();
    }

    private Long nextUserId() {
        return Repository.stream(userRepo.findAll())
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    /**
     * Builds the graph: for each user, the set of friend ids.
     */
    private Map<Long, Set<Long>> buildGraph() {
        Map<Long, Set<Long>> graph = new TreeMap<>();
        Repository.stream(userRepo.findAll()).forEach(u -> graph.put(u.getId(), new TreeSet<>()));
        Repository.stream(friendshipRepo.findAll()).forEach(f -> {
            graph.get(f.getIdUser1()).add(f.getIdUser2());
            graph.get(f.getIdUser2()).add(f.getIdUser1());
        });
        return graph;
    }

    /**
     * Finds the communities (connected components) of the graph, using bfs. The while loop
     * driving the traversal is kept classic on purpose (see readme.txt): it is not a simple
     * parcurgere of an existing collection but a graph algorithm with mutable state (the queue
     * and the visited set that grow as the graph is explored); only the processing of the
     * neighbours of the current node is written with a stream, as suggested in the assignment.
     */
    private List<Set<Long>> findComponents(Map<Long, Set<Long>> graph) {
        List<Set<Long>> components = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        graph.keySet().stream()
                .filter(start -> !visited.contains(start))
                .forEach(start -> {
                    Set<Long> component = new TreeSet<>();
                    Deque<Long> queue = new ArrayDeque<>();
                    queue.add(start);
                    visited.add(start);

                    while (!queue.isEmpty()) {
                        Long current = queue.poll();
                        component.add(current);
                        graph.get(current).stream()
                                .filter(neighbour -> !visited.contains(neighbour))
                                .forEach(neighbour -> {
                                    visited.add(neighbour);
                                    queue.add(neighbour);
                                });
                    }
                    components.add(component);
                });
        return components;
    }

    private Community toCommunity(Set<Long> component, Map<Long, Set<Long>> graph) {
        List<User> members = component.stream().map(this::findUser).collect(Collectors.toList());
        int longest = component.stream()
                .mapToInt(start -> dfsLongest(start, graph, new HashSet<>(Set.of(start))))
                .max().orElse(0);
        return new Community(members, longest);
    }

    /**
     * Returns the length of the longest path starting from the current user without going
     * through already visited users. Kept as classic backtracking recursion on purpose (see
     * readme.txt): it is the exception explicitly accepted in the assignment, because a
     * recursive stream version of this exact backtracking is harder to read than the loop.
     */
    private int dfsLongest(Long current, Map<Long, Set<Long>> graph, Set<Long> visited) {
        int max = 0;
        for (Long neighbour : graph.get(current)) {
            if (!visited.contains(neighbour)) {
                visited.add(neighbour);
                max = Math.max(max, 1 + dfsLongest(neighbour, graph, visited));
                visited.remove(neighbour);
            }
        }
        return max;
    }
}
