import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class AdministratorInventoryManagementUI {

    private InitialData data;
    private List<ReplenishmentRequest> replenishmentRequests; // List to store replenishment requests
    private InventoryManagementControl inventoryManager;

    public AdministratorInventoryManagementUI(InitialData data) {
        this.data = data;
        this.replenishmentRequests = new ArrayList<>(); // Initialize the request list
        this.inventoryManager = new InventoryManagementControl(data);
    }

    public void manageInventory() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            //displayInventory();

            System.out.println("\nChoose an action:");
            System.out.println("1. Add New Medication");
            System.out.println("2. Remove Medication");
            System.out.println("3. Request Medication");
            System.out.println("4. Update Initial Stock");
            System.out.println("5. Update Low Stock Level Alert");
            System.out.println("6. Manage Replenishment Requests");
            System.out.println("7. View Medication Inventory");
            System.out.println("8. Go back to the menu");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    inventoryManager.addNewMedicine(scanner);
                    break;

                case 2:
                    inventoryManager.removeMedicine(scanner);
                    break;

                case 3:
                    inventoryManager.createReplenishmentRequest(scanner);
                    break;

                case 4:
                    inventoryManager.updateStockInitial(scanner);
                    break;

                case 5:
                    inventoryManager.updateLowStockLevelAlert(scanner);
                    break;

                case 6:
                    AdministratorReplenishmentRequestManagementUI requestManager = new AdministratorReplenishmentRequestManagementUI(data, replenishmentRequests);
                    requestManager.manageRequests();
                    break;

                case 7:
                    inventoryManager.displayInventory();
                    break;

                case 8:
                    return; // Go back to the menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

 
}
