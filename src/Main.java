import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        authorise_and_display_menu();
    }

    public static void authorise_and_display_menu() 
    {
                // Initialize the AuthorizationControl and InitialData
                AuthorizationControl authControl = new AuthorizationControl();
                InitialData initialData = new InitialData();
        
                // Import initial data (patients, staff, medicines)
                initialData.importData();
        
                // Populate AuthorizationControl with sample credentials (userID and password)
                // This is just an example; in a real application, credentials would be securely stored
                authControl.addCredential("D001", "password1");
                authControl.addCredential("D002", "password2");
                authControl.addCredential("P001", "password3");
                authControl.addCredential("A001", "password4");
        
                // Scanner for user input
                Scanner scanner = new Scanner(System.in);
        
                System.out.println("Welcome! Please log in.");
                System.out.print("User ID: ");
                String userID = scanner.nextLine();
        
                System.out.print("Password: ");
                String password = scanner.nextLine();
        
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
                    } else {
                        System.out.println("Authorization failed. Access denied.");
                    }
                } else {
                    System.out.println("Login failed. Please check your credentials and try again.");
                }
        
                // Close the scanner
                scanner.close();
    }

}
