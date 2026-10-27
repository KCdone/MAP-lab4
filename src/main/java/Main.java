import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import domain.validators.Validator;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;
import repository.RepositoryFactory;
import service.NetworkService;
import ui.ConsoleUI;

/**
 * Entry point: builds the layers (repository -> service -> ui) and starts the application.
 * Depends only on the Repository interface; the concrete repositories are created by RepositoryFactory.
 */
public class Main {

    /**
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            RepositoryFactory factory = new RepositoryFactory();

            // regula noua de validare (lab 4): lambda compusa cu andThen peste UserValidator,
            // fara sa se modifice UserValidator si fara o clasa noua
            Validator<User> usernameNuIncepeCuCifra = user -> {
                if (!user.getUsername().isEmpty() && Character.isDigit(user.getUsername().charAt(0))) {
                    throw new ValidationException("The username must not start with a digit.");
                }
            };
            Validator<User> userValidator = new UserValidator().andThen(usernameNuIncepeCuCifra);

            Repository<Long, User> userRepo = factory.createUserRepository(userValidator);
            Repository<Tuple<Long, Long>, Friendship> friendshipRepo =
                    factory.createFriendshipRepository(new FriendshipValidator(userRepo));
            new ConsoleUI(new NetworkService(userRepo, friendshipRepo)).run();
        } catch (RepositoryException e) {
            System.out.println("The application cannot start: " + e.getMessage());
        }
    }
}
