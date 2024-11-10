package ui;
import medicinemanagements.InventoryManagementControl;
import medicinemanagements.ReplenishmentRequest;
import ui.AdministratorInventoryManagementUI;


import filereaders.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class AdministratorInventoryManagementUI {

    private InitialDataMedicine data;
    private InitialDatareplenishmentRequest replenishmentRequestData;
    private List<ReplenishmentRequest> replenishmentRequests; // List to store replenishment requests
    private InventoryManagementControl inventoryManager;

    public AdministratorInventoryManagementUI(InitialDataMedicine data) {
        this.data = data;
        
        this.replenishmentRequestData = new InitialDatareplenishmentRequest(); 
        this.replenishmentRequestData.reloadData(); // Load data from CSV
        this.replenishmentRequests = replenishmentRequestData.getLists(); // populate list from replenishment data
        
        this.inventoryManager = new InventoryManagementControl(data);
        data.reloadData();
    }

    public void manageInventory() {
        Scanner scanner = new Scanner(System.in);
        while (true) {

            System.out.println("\nChoose an action:");
            System.out.println("1. Add New Medication");
            System.out.println("2. Remove Medication");
            System.out.println("3. Update Initial Stock");
            System.out.println("4. Update Low Stock Level Alert");
            System.out.println("5. Manage Replenishment Requests");
            System.out.println("6. View Medication Inventory");
            System.out.println("7. Go back to the menu");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) 
            {
                case 1:
                    System.out.println("\n===================================");
                    System.out.println("Add New Medication");
                    System.out.println("===================================\n");
                    inventoryManager.addNewMedicine(scanner);
                    break;

                case 2:
                    System.out.println("\n===================================");
                    System.out.println("Remove Medication");
                    System.out.println("===================================\n");
                    inventoryManager.removeMedicine(scanner);
                    break;

                case 3:
                    System.out.println("\n===================================");
                    System.out.println("Update Initial Stock");
                    System.out.println("===================================\n");
                    inventoryManager.updateStockInitial(scanner);
                    break;

                case 4:
                    System.out.println("\n===================================");
                    System.out.println("Update Low Stock Level Alert");
                    System.out.println("===================================\n");
                    inventoryManager.updateLowStockLevelAlert(scanner);
                    break;

                case 5:
                    System.out.println("\n===================================");
                    System.out.println("Manage Replenishment Requests");
                    System.out.println("===================================\n");
                    AdministratorReplenishmentRequestManagementUI requestManager = new AdministratorReplenishmentRequestManagementUI(replenishmentRequestData,data, replenishmentRequests);
                    requestManager.manageRequests();
                    break;

                case 6:
                    System.out.println("\n===================================");
                    System.out.println("Medication Inventory");
                    System.out.println("===================================\n");
                    inventoryManager.displayInventory();
                    break;

                case 7:
                    return; // Go back to the menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            
        }
    }
}
