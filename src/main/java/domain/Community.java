package domain;

import java.util.List;

/**
 * A community: a connected component of the network graph
 * (users linked to each other by friendships, directly or indirectly).
 *
 * @param members     the users of the community
 * @param longestPath the length of the longest path in the community (number of friendships)
 */
public record Community(List<User> members, int longestPath) {
}
