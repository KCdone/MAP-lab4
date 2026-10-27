package domain;

import java.time.LocalDateTime;

/**
 * A friendship between two users. A friendship has no direction: (1, 2) and (2, 1)
 * are the same friendship, so the id is normalized with the smaller user id first.
 */
public class Friendship extends Entity<Tuple<Long, Long>> {
    private final LocalDateTime date;

    /**
     * @param idUserA the id of one of the users
     * @param idUserB the id of the other user
     * @param date    the moment the friendship was created
     */
    public Friendship(Long idUserA, Long idUserB, LocalDateTime date) {
        setId(idFor(idUserA, idUserB));
        this.date = date;
    }

    /**
     * Builds the normalized id of the friendship between two users (smaller id first),
     * so that it can be searched with findOne no matter the order of the users.
     *
     * @param idUserA the id of one of the users
     * @param idUserB the id of the other user
     * @return the normalized id
     */
    public static Tuple<Long, Long> idFor(Long idUserA, Long idUserB) {
        if (idUserA != null && idUserB != null && idUserA > idUserB) {
            return new Tuple<>(idUserB, idUserA);
        }
        return new Tuple<>(idUserA, idUserB);
    }

    /**
     * @return the id of the first user (the smaller one)
     */
    public Long getIdUser1() {
        return getId().getLeft();
    }

    /**
     * @return the id of the second user (the bigger one)
     */
    public Long getIdUser2() {
        return getId().getRight();
    }

    /**
     * @return the moment the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * @param userId the id of a user
     * @return true if the user is one of the two friends, false otherwise
     */
    public boolean involves(Long userId) {
        return userId != null && (userId.equals(getIdUser1()) || userId.equals(getIdUser2()));
    }

    /**
     * @return the friendship as text
     */
    @Override
    public String toString() {
        return "Friendship{" + getIdUser1() + " <-> " + getIdUser2() + ", since " + date + "}";
    }
}
