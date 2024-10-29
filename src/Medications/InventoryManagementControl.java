import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Scanner;

public class InventoryManagementControl {
    private InitialData data;
    //private List<ReplenishmentRequest> replenishmentRequests; // List to store replenishment requests

    public InventoryManagementControl(InitialData data) {
        this.data = data;
        //this.replenishmentRequests = new ArrayList<>(); // Initialize the request list
    }


    protected void addNewMedicine(Scanner scanner) {
        System.out.print("Enter new medicine name: ");
        String name = scanner.nextLine();
    
        System.out.print("Enter initial stock: ");
        int initialStock = scanner.nextInt();
    
        System.out.print("Enter low stock level alert: ");
        int lowStockLevelAlert = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        Medicine newMedicine = new Medicine(name, initialStock, lowStockLevelAlert);
        try {
            data.appendMedicine("./data/Medicine_List.csv", newMedicine); // Update CSV file
            System.out.println("Medicine added successfully.");
        } catch (IOException e) {
            System.out.println("Error adding medicine: " + e.getMessage());
        }
    }
    
    protected void removeMedicine(Scanner scanner) {
        System.out.print("Enter medicine name to remove: ");
        String name = scanner.nextLine();
    
        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            data.getMedicines().remove(medicine); // Remove from memory
            try {
                data.rewriteMedicines("./data/Medicine_List.csv"); // Update CSV file
                System.out.println("Medicine removed successfully.");
            } catch (IOException e) {
                System.out.println("Error removing medicine: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }
    /*
    protected void displayInventory() {
        System.out.println("Current Medication Inventory:");
        for (Medicine medicine : data.getMedicines()) {
            System.out.println("Medicine: " + medicine.getName() + ", Stock: " + medicine.getInitialStock()+ ", Low Stock Alert: " + medicine.getLowStockLevelAlert());
        }
    }

    protected void createReplenishmentRequest(Scanner scanner) {
        System.out.print("Enter medicine name: ");
        String name = scanner.nextLine();
    
        System.out.print("Enter requested stock quantity: ");
        int requestedStock = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        ReplenishmentRequest request = new ReplenishmentRequest(name, requestedStock);
        replenishmentRequests.add(request); // Add to in-memory list
        System.out.println("Replenishment request created for " + name + " with quantity " + requestedStock);
    
        // Save request to CSV file
        try {
            data.appendReplenishmentRequest("../data/Replenishment_Requests.csv", request);
        } catch (IOException e) {
            System.out.println("Error saving replenishment request: " + e.getMessage());
        }
    }
    */

    protected void updateStockInitial(Scanner scanner) {
        System.out.print("Enter medicine name to update stock: ");
        String name = scanner.nextLine();

        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            System.out.print("Enter new initial stock: ");
            int newStock = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            medicine.setInitialStock(newStock);
            try {
                data.rewriteMedicines("./data/Medicine_List.csv"); // Update CSV file
                System.out.println("Initial stock updated for " + name);
            } catch (IOException e) {
                System.out.println("Error updating initial stock: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }

    protected void updateLowStockLevelAlert(Scanner scanner) {
        System.out.print("Enter medicine name to update Low Stock Level Alert: ");
        String name = scanner.nextLine();

        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            System.out.print("Enter new Low Stock Level Alert: ");
            int lowStockLevelAlert = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            medicine.setLowStockLevelAlert(lowStockLevelAlert);
            try {
                data.rewriteMedicines("./data/Medicine_List.csv"); // Update CSV file
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