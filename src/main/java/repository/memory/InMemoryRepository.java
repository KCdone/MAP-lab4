package repository.memory;

import domain.Entity;
import domain.validators.Validator;
import repository.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository that keeps the entities only in memory, in a Map.
 * It receives a validation strategy (Validator) and uses it on save and update.
 *
 * @param <ID> the type of the id of the entities
 * @param <E>  the type of the entities saved in the repository
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private final Map<ID, E> entities = new HashMap<>();
    private final Validator<E> validator;

    /**
     * @param validator the validation strategy used on save and update
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
    }

    @Override
    public E findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        validator.validate(entity);
        if (entities.containsKey(entity.getId())) {
            return entity;
        }
        entities.put(entity.getId(), entity);
        return null;
    }

    @Override
    public E delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        return entities.remove(id);
    }

    @Override
    public E update(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }
        validator.validate(entity);
        if (!entities.containsKey(entity.getId())) {
            return entity;
        }
        entities.put(entity.getId(), entity);
        return null;
    }
}
