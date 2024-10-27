import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InventoryManager {
    private InitialData data;
    private List<ReplenishmentRequest> replenishmentRequests; // List to store replenishment requests

    public InventoryManager(InitialData data) {
        this.data = data;
        this.replenishmentRequests = new ArrayList<>(); // Initialize the request list
    }

    public void manageInventory() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayInventory();

            System.out.println("\nChoose an action:");
            System.out.println("1. Add New Medication");
            System.out.println("2. Remove Medication");
            System.out.println("3. Request Medication");
            System.out.println("4. Update Initial Stock");
            System.out.println("5. Update Low Stock Level Alert");
            System.out.println("6. Manage Replenishment Requests");
            System.out.println("7. Go back to the menu");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addNewMedicine(scanner);
                    break;

                case 2:
                    removeMedicine(scanner);
                    break;

                case 3:
                    createReplenishmentRequest(scanner);
                    break;

                case 4:
                    updateStockInitial(scanner);
                    break;

                case 5:
                    updateLowStockLevelAlert(scanner);
                    break;

                case 6:
                    ReplenishmentRequestManager requestManager = new ReplenishmentRequestManager(data, replenishmentRequests);
                    requestManager.manageRequests();
                    break;

                case 7:
                    return; // Go back to the menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayInventory() {
        System.out.println("Current Medication Inventory:");
        for (Medicine medicine : data.getMedicines()) {
            System.out.println("Medicine: " + medicine.getName() + ", Stock: " + medicine.getInitialStock()+ ", Low Stock Alert: " + medicine.getLowStockLevelAlert());
        }
    }

    private void createReplenishmentRequest(Scanner scanner) {
        System.out.print("Enter medicine name: ");
        String name = scanner.nextLine();

        System.out.print("Enter requested stock quantity: ");
        int requestedStock = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Create and add a new replenishment request
        ReplenishmentRequest request = new ReplenishmentRequest(name, requestedStock);
        replenishmentRequests.add(request);
        System.out.println("Replenishment request created for " + name + " with quantity " + requestedStock);
    }


    private void addNewMedicine(Scanner scanner) {
        System.out.print("Enter new medicine name: ");
        String name = scanner.nextLine();

        System.out.print("Enter initial stock: ");
        int initialStock = scanner.nextInt();

        System.out.print("Enter low stock level alert: ");
        int lowStockLevelAlert = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Medicine newMedicine = new Medicine(name, initialStock, lowStockLevelAlert);
        try {
            data.appendMedicine("../data/Medicine_List.csv", newMedicine); // Update CSV file
            System.out.println("Medicine added successfully.");
        } catch (IOException e) {
            System.out.println("Error adding medicine: " + e.getMessage());
        }
    }

    private void removeMedicine(Scanner scanner) {
        System.out.print("Enter medicine name to remove: ");
        String name = scanner.nextLine();

        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            data.getMedicines().remove(medicine); // Remove from memory
            try {
                data.rewriteMedicines("../data/Medicine_List.csv"); // Update CSV file
                System.out.println("Medicine removed successfully.");
            } catch (IOException e) {
                System.out.println("Error removing medicine: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }


    private void updateStockInitial(Scanner scanner) {
        System.out.print("Enter medicine name to update stock: ");
        String name = scanner.nextLine();

        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            System.out.print("Enter new initial stock: ");
            int newStock = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            medicine.setInitialStock(newStock);
            try {
                data.rewriteMedicines("../data/Medicine_List.csv"); // Update CSV file
                System.out.println("Initial stock updated for " + name);
            } catch (IOException e) {
                System.out.println("Error updating initial stock: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }

    private void updateLowStockLevelAlert(Scanner scanner) {
        System.out.print("Enter medicine name to update Low Stock Level Alert: ");
        String name = scanner.nextLine();

        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            System.out.print("Enter new Low Stock Level Alert: ");
            int lowStockLevelAlert = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            medicine.setLowStockLevelAlert(lowStockLevelAlert);
            try {
                data.rewriteMedicines("../data/Medicine_List.csv"); // Update CSV file
                System.out.println("Low stock alert updated for " + name);
            } catch (IOException e) {
                System.out.println("Error updating low stock level alert: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }


    private Medicine findMedicineByName(String name) {
        for (Medicine medicine : data.getMedicines()) {
            if (medicine.getName().equalsIgnoreCase(name)) {
                return medicine;
            }
        }
        return null; // Not found
    }
}