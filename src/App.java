import java.util.Scanner;
//import Authorization.AuthorizationControl;
//import FileReader.InitialData;
//import Users.User;

public class App {
    public static void main(String[] args) {
        authorise_and_display_menu();
    }

    public static void authorise_and_display_menu() {
        // Initialize the AuthorizationControl and InitialData
        AuthorizationControl authControl = new AuthorizationControl();
        InitialData initialData = new InitialData();

        // Import initial data (patients, staff, medicines)
        initialData.importData();

        // Load credentials from the imported staff data
        authControl.loadCredentialsFromStaff(initialData);

        // Scanner for user input
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;

        System.out.println("Welcome! Please log in.");

        while (!loggedIn) {
            try {
                System.out.print("User ID: ");
                String userID = scanner.nextLine().trim(); // Trim whitespace

                System.out.print("Password: ");
                String password = scanner.nextLine().trim(); // Trim whitespace

                // Validate inputs
                if (userID.isEmpty() || password.isEmpty()) {
                    System.out.println("User ID and Password cannot be empty. Please try again.");
                    continue; // Ask for input again
                }

                // Attempt to log in the user
                User user = authControl.login(userID, password);

                if (user != null) {
                    // Login successful, create a session for the user
                    authControl.createSession(user);
                    System.out.println("Login successful! Welcome, " + user.getName() + ".");

                    // Check if the user is authorized based on their role
                    if (authControl.authorize(userID, user.getRole().name())) {
                        // Display the appropriate menu for the user based on their role
                        user.displayMenu();
                        //System.out.println("Test");
                        loggedIn = true; // Exit loop after successful login and menu display
                    } else {
                        System.out.println("Authorization failed. Access denied.");
                    }
                } else {
                    System.out.println("Login failed. Please check your credentials and try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                // Optionally log the error
            }
        }

        // Close the scanner
        scanner.close();
        System.out.println("Thank you for using the application. Goodbye!");
    }
}
