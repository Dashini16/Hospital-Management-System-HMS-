package medicinemanagements;
import java.io.IOException;
import filereaders.InitialData;
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


    public void addNewMedicine(Scanner scanner) {
        System.out.print("Enter new medicine name: ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println("Error: Medicine name cannot be empty.");
            return;
        }
        
        int initialStock = 0;
        int lowStockLevelAlert = 0;
        
        // Handle initial stock input with error checking
        while (true) {
            System.out.print("Enter initial stock: ");
            String initialStockInput = scanner.nextLine().trim();
            try {
                initialStock = Integer.parseInt(initialStockInput);
                if (initialStock < 0) {
                    System.out.println("Error: Initial stock must be a non-negative integer.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid integer for initial stock.");
            }
        }
    
        // Handle low stock level alert input with error checking
        while (true) {
            System.out.print("Enter low stock level alert: ");
            String lowStockInput = scanner.nextLine().trim();
            try {
                lowStockLevelAlert = Integer.parseInt(lowStockInput);
                if (lowStockLevelAlert < 0) {
                    System.out.println("Error: Low stock level alert must be a non-negative integer.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid integer for low stock level alert.");
            }
        }
    
        Medicine newMedicine = new Medicine(name, initialStock, lowStockLevelAlert);
        try {
            data.appendMedicine("hms\\src\\data\\Medicine_List.csv", newMedicine); // Update CSV file
            System.out.println("Medicine added successfully.");
        } catch (IOException e) {
            System.out.println("Error adding medicine: " + e.getMessage());
        }
    }
    
    
    public void removeMedicine(Scanner scanner) {
        System.out.print("Enter medicine name to remove: ");
        String name = scanner.nextLine().trim();
    
        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            data.getMedicines().remove(medicine); // Remove from memory
            try {
                data.rewriteMedicines("hms\\src\\data\\Medicine_List.csv"); // Update CSV file
                System.out.println("Medicine removed successfully.");
            } catch (IOException e) {
                System.out.println("Error removing medicine: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }
    
    public void updateStockInitial(Scanner scanner) {
        System.out.print("Enter medicine name to update stock: ");
        String name = scanner.nextLine().trim();
    
        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            int newStock = -1;
    
            // Handle new stock input with error checking
            while (true) {
                System.out.print("Enter new initial stock: ");
                String newStockInput = scanner.nextLine().trim();
                try {
                    newStock = Integer.parseInt(newStockInput);
                    if (newStock < 0) {
                        System.out.println("Error: Initial stock must be a non-negative integer.");
                        continue;
                    }
                    break; // Break loop if valid input is provided
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for initial stock.");
                }
            }
    
            medicine.setInitialStock(newStock);
            try {
                data.rewriteMedicines("hms\\src\\data\\Medicine_List.csv"); // Update CSV file
                System.out.println("Initial stock updated for " + name);
            } catch (IOException e) {
                System.out.println("Error updating initial stock: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }
    
    public void updateLowStockLevelAlert(Scanner scanner) {
        System.out.print("Enter medicine name to update Low Stock Level Alert: ");
        String name = scanner.nextLine().trim();
    
        Medicine medicine = findMedicineByName(name);
        if (medicine != null) {
            int lowStockLevelAlert = -1;
    
            // Handle low stock level alert input with error checking
            while (true) {
                System.out.print("Enter new Low Stock Level Alert: ");
                String lowStockInput = scanner.nextLine().trim();
                try {
                    lowStockLevelAlert = Integer.parseInt(lowStockInput);
                    if (lowStockLevelAlert < 0) {
                        System.out.println("Error: Low stock level alert must be a non-negative integer.");
                        continue;
                    }
                    break; // Break loop if valid input is provided
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for low stock level alert.");
                }
            }
    
            medicine.setLowStockLevelAlert(lowStockLevelAlert);
            try {
                data.rewriteMedicines("hms\\src\\data\\Medicine_List.csv"); // Update CSV file
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