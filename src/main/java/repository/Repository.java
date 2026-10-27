package repository;

import domain.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * CRUD operations repository interface.
 *
 * @param <ID> the type of the id of the entities
 * @param <E>  the type of the entities saved in the repository
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     * @param id the id of the entity to be returned; must not be null
     * @return the entity with the given id, or null if there is no entity with this id
     * @throws IllegalArgumentException if id is null
     */
    E findOne(ID id);

    /**
     * @return all the entities
     */
    Iterable<E> findAll();

    /**
     * @param entity the entity to be saved; must not be null
     * @return null if the entity was saved, or the given entity if its id already exists
     * @throws IllegalArgumentException       if entity is null
     * @throws exceptions.ValidationException if the entity is not valid
     * @throws exceptions.RepositoryException if the entity cannot be persisted
     */
    E save(E entity);

    /**
     * @param id the id of the entity to be removed; must not be null
     * @return the removed entity, or null if there is no entity with this id
     * @throws IllegalArgumentException       if id is null
     * @throws exceptions.RepositoryException if the change cannot be persisted
     */
    E delete(ID id);

    /**
     * @param entity the entity to be updated; must not be null
     * @return null if the entity was updated, or the given entity if its id does not exist
     * @throws IllegalArgumentException       if entity is null
     * @throws exceptions.ValidationException if the entity is not valid
     * @throws exceptions.RepositoryException if the change cannot be persisted
     */
    E update(E entity);

    /**
     * Searches among all the entities for the ones that match the given criteria.
     * The criteria are built outside the repository (for example in CriteriiUtilizator)
     * and combined with and, or, negate or Predicate.not; the repository does not need
     * a specific findByXxx method for every criteria.
     *
     * @param filter the condition an entity must meet to be returned
     * @return the entities that match the filter, in the order given by findAll()
     */
    default List<E> findAll(Predicate<E> filter) {
        return stream(findAll()).filter(filter).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * findAll() returns Iterable, which has no stream() method; this turns it into a Stream,
     * so it is written once here instead of in every place that needs to process the entities
     * with the Stream API.
     *
     * @param iterable the iterable to wrap
     * @param <T>      the type of the elements
     * @return a sequential stream over the iterable
     */
    static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
