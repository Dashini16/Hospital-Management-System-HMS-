import java.util.Scanner;

public class Administrator extends User {
    public Administrator(String userID, String name, String contactInfo) {
        super(userID, name, Role.ADMINISTRATOR, contactInfo);
    }

    @Override
    public void displayMenu() {
        // Print the administrator menu to the console
        InitialData data = new InitialData();
        data.importData(); // Load data

        while (true) {
            System.out.println("Administrator Menu:");
            System.out.println("1. View and Manage Hospital Staff");
            System.out.println("2. View Appointments Details");
            System.out.println("3. View and Manage Medication Inventory");
            System.out.println("4. Approve Replenishment Requests");
            System.out.println("5. Logout");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    HospitalStaffManager staffManager = new HospitalStaffManager(data);
                    staffManager.manageStaff();
                    break;
                case 2:
                    // viewAppointmentDetails();
                    break;
                case 3:
                    InventoryManager inventoryManager = new InventoryManager(data);
                    inventoryManager.manageInventory();
                    break;
                case 4:
                    InventoryManager inventoryManagerreplenish = new InventoryManager(data);
                    inventoryManagerreplenish.manageInventory();
                    // approveReplenishmentRequests();
                    break;
                case 5:
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
