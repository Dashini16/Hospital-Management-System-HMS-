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
            System.out.println("1. Request Medication");
            System.out.println("2. Update Initial Stock");
            System.out.println("3. View Replenishment Requests");
            System.out.println("4. Approve/Replenish Request");
            System.out.println("5. Go back to the menu");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createReplenishmentRequest(scanner);
                    break;

                case 2:
                    updateStockInitial(scanner);
                    break;

                case 3:
                    viewReplenishmentRequests();
                    break;

                case 4:
                    approveReplenishment(scanner);
                    break;

                case 5:
                    return; // Go back to the menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayInventory() {
        System.out.println("Current Medication Inventory:");
        for (Medicine medicine : data.getMedicines()) {
            System.out.println("Medicine: " + medicine.getName() + " | Stock: " + medicine.getInitialStock());
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

    private void updateStockInitial(Scanner scanner) {
        System.out.print("Enter medicine name to update stock: ");
        String name = scanner.nextLine();

        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            System.out.print("Enter new initial stock: ");
            int newStock = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            medicine.setInitialStock(newStock);
            System.out.println("Initial stock updated for " + name + ": " + newStock);
        } else {
            System.out.println("Medicine not found.");
        }
    }

    private void viewReplenishmentRequests() {
        System.out.println("\nCurrent Replenishment Requests:");
        for (ReplenishmentRequest request : replenishmentRequests) {
            System.out.println("Medicine: " + request.getMedicineName() + 
                " | Requested Stock: " + request.getRequestedStock() + 
                " | Status: " + request.getStatus());
        }
    }

    private void approveReplenishment(Scanner scanner) {
        System.out.print("Enter the name of the medicine to approve replenishment for: ");
        String name = scanner.nextLine();

        for (ReplenishmentRequest request : replenishmentRequests) {
            if (request.getMedicineName().equalsIgnoreCase(name)) {
                // Approve the request and update the stock
                Medicine medicine = findMedicineByName(name);
                if (medicine != null) {
                    medicine.setInitialStock(medicine.getInitialStock() + request.getRequestedStock());
                    request.setStatus(RequestStatus.FULFILLED);
                    System.out.println("Replenishment approved for " + name);
                } else {
                    System.out.println("Medicine not found in inventory.");
                }
                return; // Exit after processing the request
            }
        }
        System.out.println("No replenishment request found for " + name);
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
