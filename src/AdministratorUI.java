import java.util.Scanner;

public class AdministratorUI {
    private Administrator administrator;

    public AdministratorUI(Administrator administrator) {
        this.administrator = administrator;
    }

    public void displayMenu() {
        // Print the administrator menu to the console
        InitialData data = new InitialData();
        data.importData(); // Load data

        while (true) {
            System.out.println("Administrator Menu:");
            System.out.println("1. View and Manage Hospital Staff");
            System.out.println("2. View Appointments Details");
            System.out.println("3. Manage Medication Inventory");
            System.out.println("4. Logout");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    AdministratorStaffManagementUI staffManager = new AdministratorStaffManagementUI(data);
                    staffManager.manageStaff();
                    break;
                case 2:
                    // viewAppointmentDetails();
                    break;
                case 3:
                    AdministratorInventoryManagementUI inventoryManager = new AdministratorInventoryManagementUI(data);
                    inventoryManager.manageInventory();
                    break;
                case 4:
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
