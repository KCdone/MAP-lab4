package ui;

import domain.Community;
import domain.Friendship;
import domain.User;
import domain.UserCriteria;
import exceptions.SocialNetworkException;
import service.NetworkService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Console user interface: reads commands, calls the service and shows the result or the error.
 * This is the layer that handles the exceptions, by showing a message to the user.
 */
public class ConsoleUI {
    private final NetworkService service;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * @param service the service of the application
     */
    public ConsoleUI(NetworkService service) {
        this.service = service;
    }

    /**
     * Runs the main loop, until the user chooses option 0.
     */
    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String option = scanner.nextLine().trim();
            try {
                running = execute(option);
            } catch (NumberFormatException e) {
                System.out.println("Error: a number must be entered.");
            } catch (SocialNetworkException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (UnsupportedOperationException e) {
                System.out.println("Operation unavailable: " + e.getMessage());
            }
        }
    }

    private boolean execute(String option) {
        switch (option) {
            case "1":
                addUser();
                return true;
            case "2":
                removeUser();
                return true;
            case "3":
                addFriend();
                return true;
            case "4":
                removeFriend();
                return true;
            case "5":
                showUsers();
                return true;
            case "6":
                showFriendships();
                return true;
            case "7":
                System.out.println("Number of communities: " + service.countCommunities());
                return true;
            case "8":
                showMostSociable();
                return true;
            case "9":
                searchUsers();
                return true;
            case "0":
                System.out.println("Goodbye!");
                return false;
            default:
                System.out.println("Unknown option.");
                return true;
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Add user             2. Remove user (by id)");
        System.out.println("3. Add friend            4. Remove friend");
        System.out.println("5. Show users            6. Show friendships");
        System.out.println("7. Number of communities 8. Most sociable community");
        System.out.println("9. Search users (by last name, without friends)");
        System.out.println("0. Exit");
        System.out.print("> ");
    }

    private void addUser() {
        String username = read("Username: ");
        String firstName = read("First name: ");
        String lastName = read("Last name: ");
        String email = read("Email: ");
        System.out.println("Added: " + service.addUser(username, firstName, lastName, email));
    }

    private void removeUser() {
        Long id = Long.parseLong(read("User id: "));
        System.out.println("Removed: " + service.removeUser(id));
    }

    private void addFriend() {
        String username1 = read("Username of the first user: ");
        String username2 = read("Username of the second user: ");
        System.out.println("Added: " + describe(service.addFriendship(username1, username2)));
    }

    private void removeFriend() {
        String username1 = read("Username of the first user: ");
        String username2 = read("Username of the second user: ");
        System.out.println("Removed: " + describe(service.removeFriendship(username1, username2)));
    }

    private void showUsers() {
        service.getAllUsers().forEach(System.out::println);
    }

    private void showFriendships() {
        service.getAllFriendships().forEach(f -> System.out.println(describe(f)));
    }

    private void showMostSociable() {
        Optional<Community> result = service.mostSociableCommunity();
        if (result.isEmpty()) {
            System.out.println("There are no users in the network.");
            return;
        }
        Community community = result.get();
        System.out.println("Most sociable community (longest path = " + community.longestPath() + "):");
        community.members().forEach(u -> System.out.println("  " + u));
    }

    /**
     * Demonstration of a new search criterion built only with lambdas (UserCriteria),
     * combined with and, without adding a findByXxx method to the repository.
     */
    private void searchUsers() {
        String lastName = read("Last name: ");
        Predicate<User> criteria = UserCriteria.byLastName(lastName)
                .and(UserCriteria.withoutFriends(service.usersWithFriends()));
        List<User> result = service.searchUsers(criteria);
        if (result.isEmpty()) {
            System.out.println("No user found.");
        } else {
            result.forEach(System.out::println);
        }
    }

    private String describe(Friendship f) {
        return service.findUser(f.getIdUser1()).getUsername() + " <-> "
                + service.findUser(f.getIdUser2()).getUsername() + " (since " + f.getDate() + ")";
    }

    private String read(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
