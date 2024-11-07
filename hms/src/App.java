import authorization.AuthorizationControl;
import filereaders.InitialDataPatient;
import filereaders.InitialDataStaff;
import usermanagement.PasswordManagement;
import users.Users;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        authorise_and_display_menu();
    }

    public static void authorise_and_display_menu() {
        // Initialize AuthorizationControl, InitialData, and PasswordManagement
        AuthorizationControl authControl = new AuthorizationControl();
        InitialDataStaff initialData = new InitialDataStaff();
        InitialDataPatient initialDataPatient = new InitialDataPatient();
        PasswordManagement passwordManagement = new PasswordManagement(initialData,initialDataPatient);

        // Import initial data (patients, staff, medicines)
        initialData.importData();
        initialDataPatient.importData();
        
        // Load credentials from the imported staff data
        authControl.loadCredentialsFromStaff(initialData, initialDataPatient);

        // Scanner for user input
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;

        System.out.println("===================================");
        System.out.println(" Welcome to the Health Management System!");
        System.out.println("===================================");

        while (!loggedIn) {
            System.out.println("\nPlease choose an option:");
            System.out.println(" 1. Login");
            System.out.println(" 2. Exit");
            System.out.println("===================================");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("2")) {
                System.out.println("Exiting the application...");
                break;
            } else if (!choice.equals("1")) {
                System.out.println("Invalid choice. Please enter 1 to login or 2 to exit.");
                continue; // Re-prompt for option
            }
            
            System.out.println("==================================="); 
            System.out.println("\nPlease log in:");
            System.out.println("===================================");

            try {
                System.out.print("User ID: ");
                String userID = scanner.nextLine().trim();

                System.out.print("Password: ");
                String password = scanner.nextLine().trim();

                if (userID.isEmpty() || password.isEmpty()) {
                    System.out.println("User ID and Password cannot be empty. Please try again.");
                    continue; // Go back to the option menu
                }

                // Attempt to log in the user
                Users user = authControl.login(userID, password);

                if (user != null) {
                    authControl.createSession(user);

                    // Check if the password is the default
                    if (password.equals("defaultPasswords")) {
                        System.out.println("\nYou are using the default password. Please update it for security purposes.");
                        
                        // Prompt to change password based on role
                        passwordManagement.changePassword(scanner);
                    }

                    // Check if the user is authorized based on their role
                    if (authControl.authorize(userID, user.getRole().name())) {
                        System.out.println("\n===================================");
                        System.out.println("Welcome, " + user.getName() + ".");
                        System.out.println("===================================\n");

                        // Only display menu if the password change was successful
                        if (!password.equals("defaultPasswords") || 
                            (password.equals("defaultPasswords") && !user.getPassword().equals("defaultPasswords"))) {
                            user.displayMenu();
                            loggedIn = true; // User is now logged in
                        }
                    } else {
                        System.out.println("Authorization failed. Access denied.");
                    }
                } else {
                    System.out.println("Login failed. Please check your credentials and try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Thank you for using the application. Goodbye!");
    }
}
