package repository;

/**
 * The persistence types supported by RepositoryFactory.
 */
public enum RepositoryType {
    /** entities kept only in memory */
    MEMORY,
    /** entities kept in text files */
    FILE,
    /** entities kept in a database (implemented in lab 5) */
    DATABASE
}
