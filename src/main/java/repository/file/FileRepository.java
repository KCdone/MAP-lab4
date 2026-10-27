package repository.file;

import domain.Entity;
import domain.validators.Validator;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;
import repository.memory.InMemoryRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Repository that keeps the entities in memory and in a UTF-8 text file, one entity per line.
 * Composition instead of Template Method (lab 4): instead of an abstract class with a subclass
 * per entity, a single generic class receives the line &lt;-&gt; entity conversion as two
 * Function parameters (built in RepositoryFactory). The file is loaded when the repository is
 * created and rewritten after every successful save, delete or update.
 *
 * @param <ID> the type of the id of the entities
 * @param <E>  the type of the entities saved in the repository
 */
public class FileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final Path filePath;
    private final Function<String, E> lineToEntity;
    private final Function<E, String> entityToLine;

    /**
     * Creates the repository and loads the entities from the file.
     * A missing file means an empty repository; the file is created at the first change.
     *
     * @param validator    the validation strategy for the entities
     * @param fileName     the relative path of the text file
     * @param lineToEntity turns a non-blank line of the file into an entity (not validated yet)
     * @param entityToLine turns an entity into the line of text that describes it
     * @throws RepositoryException if the file cannot be read or contains a corrupt or invalid line
     */
    public FileRepository(Validator<E> validator, String fileName,
                           Function<String, E> lineToEntity, Function<E, String> entityToLine) {
        super(validator);
        this.filePath = Path.of(fileName);
        this.lineToEntity = lineToEntity;
        this.entityToLine = entityToLine;
        loadData();
    }

    private void loadData() {
        if (!Files.exists(filePath)) {
            return;
        }
        List<String> lines;
        try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            throw new RepositoryException("Cannot read file " + filePath + ": " + e.getMessage(), e);
        }
        IntStream.range(0, lines.size())
                .filter(i -> !lines.get(i).isBlank())
                .forEach(i -> loadLine(lines.get(i).trim(), i + 1));
    }

    private void loadLine(String line, int lineNumber) {
        try {
            E entity = lineToEntity.apply(line);
            // super, nu this: save-ul suprascris ar rescrie fisierul in timp ce il citim
            if (super.save(entity) != null) {
                throw new RepositoryException(location(lineNumber) + "duplicate id " + entity.getId() + ".");
            }
        } catch (IllegalArgumentException | DateTimeParseException | ValidationException e) {
            throw new RepositoryException(location(lineNumber) + e.getMessage(), e);
        }
    }

    private String location(int lineNumber) {
        return "Line " + lineNumber + " of " + filePath + " is wrong: ";
    }

    private void writeToFile() {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            List<String> lines = Repository.stream(findAll()).map(entityToLine).collect(Collectors.toList());
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RepositoryException("Cannot write file " + filePath + ": " + e.getMessage(), e);
        }
    }

    /**
     * Saves the entity in memory and, if it was saved, rewrites the file.
     * {@inheritDoc}
     */
    @Override
    public E save(E entity) {
        E result = super.save(entity);
        if (result == null) {
            writeToFile();
        }
        return result;
    }

    /**
     * Removes the entity from memory and, if it existed, rewrites the file.
     * {@inheritDoc}
     */
    @Override
    public E delete(ID id) {
        E result = super.delete(id);
        if (result != null) {
            writeToFile();
        }
        return result;
    }

    /**
     * Updates the entity in memory and, if it was updated, rewrites the file.
     * {@inheritDoc}
     */
    @Override
    public E update(E entity) {
        E result = super.update(entity);
        if (result == null) {
            writeToFile();
        }
        return result;
    }
}
