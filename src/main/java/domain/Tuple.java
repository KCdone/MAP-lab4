package domain;

import java.util.Objects;

/**
 * An immutable pair of values; used as the id of a friendship.
 *
 * @param <E1> the type of the first value
 * @param <E2> the type of the second value
 */
public class Tuple<E1, E2> {
    private final E1 left;
    private final E2 right;

    /**
     * @param left  the first value
     * @param right the second value
     */
    public Tuple(E1 left, E2 right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @return the first value
     */
    public E1 getLeft() {
        return left;
    }

    /**
     * @return the second value
     */
    public E2 getRight() {
        return right;
    }

    /**
     * Two tuples are equal if both values are equal; handles null and objects of other types.
     *
     * @param o the object to compare with
     * @return true if o is a tuple with equal values, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple<?, ?> other)) return false;
        return Objects.equals(left, other.left) && Objects.equals(right, other.right);
    }

    /**
     * @return the hash code computed from both values (consistent with equals)
     */
    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    /**
     * @return the tuple as text, for example (1, 2)
     */
    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}
