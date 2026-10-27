package domain;

import java.util.Objects;

/**
 * Base class for all the entities of the domain; every entity has an identifier.
 *
 * @param <ID> the type of the identifier
 */
public abstract class Entity<ID> {
    private ID id;

    /**
     * @return the id of the entity, or null if it was not set
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id the new id of the entity
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Two entities are equal if they have the same type and the same id.
     *
     * @param o the object to compare with
     * @return true if o is an entity of the same type with the same id, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    /**
     * @return the hash code computed only from the id (consistent with equals)
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
