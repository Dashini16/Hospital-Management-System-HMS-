package medicinemanagements;
import java.io.IOException;

import filereaders.InitialDataMedicine;
import filereaders.InitialDataStaff;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Scanner;

public class InventoryManagementControl {
    //private InitialDataStaff data;
    private InitialDataMedicine medicineData;
    //private List<ReplenishmentRequest> replenishmentRequests; // List to store replenishment requests

    public InventoryManagementControl( InitialDataMedicine medicineData) {
        this.medicineData = medicineData;
        medicineData.reloadData();
        //this.replenishmentRequests = new ArrayList<>(); // Initialize the request list
    }


    public void addNewMedicine(Scanner scanner) {
        System.out.print("Enter new medicine name: ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println("Error: Medicine name cannot be empty.");
            return;
        }
        
        // Check if the medicine already exists
        if (findMedicineByName(name) != null) {
            System.out.println("Error: Medicine with the name '" + name + "' already exists.");
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
    
        // Create new medicine object
        Medicine newMedicine = new Medicine(name, initialStock, lowStockLevelAlert);
    
        // Attempt to add new medicine to the CSV file
        try {
            medicineData.appendData("hms/src/data/Medicine_List.csv", newMedicine); // Update CSV file
            System.out.println("Medicine added successfully.");
        } catch (IOException e) {
            System.out.println("Error adding medicine: " + e.getMessage());
        }
    }
    
    public void displayInventory() {
        System.out.println("Current Medication Inventory:");
        int index = 1;
        for (Medicine medicine : medicineData.getLists()) {
            System.out.println(index + ". " + medicine.getName() + "," + medicine.getInitialStock() + "," + medicine.getLowStockLevelAlert());
            index++;
        }
    }
    
    // Helper method to select a medicine by number
    private Medicine selectMedicineByNumber(Scanner scanner) {
        displayInventory(); // Show the inventory with numbering
        System.out.print("Enter the number corresponding to the medicine: ");
        
        int choice = -1;
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
                if (choice > 0 && choice <= medicineData.getLists().size()) {
                    return medicineData.getLists().get(choice - 1); // Return the selected medicine
                } else {
                    System.out.println("Error: Please enter a valid number between 1 and " + medicineData.getLists().size() + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid integer.");
            }
        }
    }
    
    public void removeMedicine(Scanner scanner) {
        Medicine medicine = selectMedicineByNumber(scanner);
        if (medicine != null) {
            medicineData.getLists().remove(medicine); // Remove from memory
            try {
                medicineData.rewriteMedicines("hms/src/data/Medicine_List.csv"); // Update CSV file
                System.out.println("Medicine removed successfully.");
            } catch (IOException e) {
                System.out.println("Error removing medicine: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }
    
    public void updateStockInitial(Scanner scanner) {
        Medicine medicine = selectMedicineByNumber(scanner);
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
                medicineData.rewriteMedicines("hms/src/data/Medicine_List.csv"); // Update CSV file
                System.out.println("Initial stock updated for " + medicine.getName());
            } catch (IOException e) {
                System.out.println("Error updating initial stock: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }
    
    public void updateLowStockLevelAlert(Scanner scanner) {
        Medicine medicine = selectMedicineByNumber(scanner);
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
                medicineData.rewriteMedicines("hms/src/data/Medicine_List.csv"); // Update CSV file
                System.out.println("Low stock alert updated for " + medicine.getName());
            } catch (IOException e) {
                System.out.println("Error updating low stock level alert: " + e.getMessage());
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }
    
    
    private Medicine findMedicineByName(String name) {
        for (Medicine medicine : medicineData.getLists()) {
            if (medicine.getName().equalsIgnoreCase(name)) {
                return medicine;
            }
        }
        return null; // Not found
    }
}