package domain.validators;

import exceptions.ValidationException;

/**
 * Validation strategy (Strategy pattern). The repository receives a Validator
 * and calls validate without knowing the concrete class.
 * Functional interface (lab 4): a new rule can be written as a lambda and attached
 * with andThen, without creating a new class.
 *
 * @param <T> the type of the validated entities
 */
@FunctionalInterface
public interface Validator<T> {

    /**
     * @param entity the entity to be validated
     * @throws ValidationException if the entity is null or not valid;
     *                             the message contains all the errors found
     */
    void validate(T entity);

    /**
     * Combines this validator with another one: both run, in this order, on the same entity.
     * Used to add a new rule (as a lambda) on top of an existing validator, without
     * touching the existing validator class.
     *
     * @param other the validator run after this one
     * @return a validator that runs this validator, then other
     */

    // permite lipirea unei reguli noi lambda peste validatorul vechi fara sa modificam clasa veche
    default Validator<T> andThen(Validator<T> other) {
        return entity -> {
            this.validate(entity);
            other.validate(entity);
        };
    }
}
