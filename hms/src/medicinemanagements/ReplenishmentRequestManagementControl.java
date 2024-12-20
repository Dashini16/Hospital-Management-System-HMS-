package medicinemanagements;

import java.io.IOException;
import java.util.List;

import enums.RequestStatus;
import filereaders.InitialDataMedicine;
import filereaders.InitialDataStaff;
import filereaders.InitialDatareplenishmentRequest;
import lookups.UserLookup;

import java.util.Scanner;

public class ReplenishmentRequestManagementControl {
    private List<ReplenishmentRequest> replenishmentRequests;
    private InitialDataMedicine medicineData;
    private InitialDatareplenishmentRequest replenishmentRequestData;
    public ReplenishmentRequestManagementControl( List<ReplenishmentRequest> replenishmentRequests, InitialDataMedicine medicineData, InitialDatareplenishmentRequest replenishmentRequestData) {
        this.replenishmentRequests = replenishmentRequests;  // Properly initialize the replenishment requests list
        this.medicineData = medicineData;
        this.replenishmentRequestData = replenishmentRequestData;

        medicineData.reloadData();
        replenishmentRequestData.reloadData();
    }


    public void approveReplenishment(Scanner scanner) {
        viewReplenishmentRequests(); // Display the list of replenishment requests
    
        if (replenishmentRequests == null || replenishmentRequests.isEmpty()) {
            System.out.println("No replenishment requests available to approve.");
            return;
        }
    
        while (true) { // Loop for input validation
            System.out.print("Enter the number associated with the request you want to approve: ");
            String input = scanner.nextLine().trim();
    
            try {
                int index = Integer.parseInt(input) - 1; // Convert input to 0-based index
    
                // Check if the index is within bounds
                if (index < 0 || index >= replenishmentRequests.size()) {
                    System.out.println("Invalid input. Please enter a valid request number.");
                    continue; // Prompt again for input
                }
    
                ReplenishmentRequest request = replenishmentRequests.get(index);
    
                // Check if the request has already been fulfilled
                if (request.getStatus() == RequestStatus.FULFILLED) {
                    System.out.println("This request has already been fulfilled and cannot be approved again.");
                    continue; // Prompt again for input
                }
    
                // Check if the request is for a new medicine
                if (request.getIsNewMedicine()) {
                    // Prompt for low stock level alert
                    System.out.print("Enter low stock level alert for " + request.getMedicineName() + ": ");
                    int lowStockLevelAlert;
                    try {
                        lowStockLevelAlert = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Using default low stock level alert of 10.");
                        lowStockLevelAlert = 10; // Default value if input is invalid
                    }
    
                    // Create new medicine entry
                    Medicine newMedicine = new Medicine(request.getMedicineName(), request.getRequestedStock(), lowStockLevelAlert);
                    medicineData.getLists().add(newMedicine); // Add new medicine to in-memory list
    
                    System.out.println("New medicine " + request.getMedicineName() + " added to inventory with initial stock of " 
                                       + request.getRequestedStock() + " and low stock alert of " + lowStockLevelAlert);
    
                } else {
                    // Existing medicine: Find and update
                    UserLookup userLookup = new UserLookup();
                    Medicine medicine = userLookup.findByID(request.getMedicineName(), medicineData.getLists(),
                                                            med -> med.getName().equalsIgnoreCase(request.getMedicineName()));
                    
                    if (medicine == null) {
                        System.out.println("Medicine not found in inventory for request: " + request.getMedicineName());
                        continue;
                    }
    
                    // Update stock for existing medicine
                    medicine.setInitialStock(medicine.getInitialStock() + request.getRequestedStock());
                    System.out.println("Stock updated for " + request.getMedicineName() + " with " + request.getRequestedStock() + " units.");
                }
    
                // Set request status to fulfilled
                request.setStatus(RequestStatus.FULFILLED);
    
                // Save updates to CSV files
                try {
                    medicineData.rewriteMedicines("hms/src/data/Medicine_List.csv");
                    replenishmentRequestData.rewriteReplenishmentRequests("hms/src/data/Replenishment_Requests.csv", replenishmentRequests);
                    System.out.println("Updates saved to file.");
                } catch (IOException e) {
                    System.out.println("Error updating files: " + e.getMessage());
                }
                break; // Exit after successful approval
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    
    
    public void viewReplenishmentRequests() {
        System.out.println("\n========== Current Replenishment Requests ==========\n");
    
        if (replenishmentRequests == null || replenishmentRequests.isEmpty()) {
            System.out.println("No replenishment requests available.");
            return;
        }
    
        // Print table headers
        System.out.printf("%-5s %-20s %-15s %-15s %-15s %-15s%n", "No.", "Medicine Name", "Requested Stock", "Status", "Requested By", "Is New Medicine");
        System.out.println("-----------------------------------------------------------------------------------------");
    
        // Print each replenishment request in a formatted way
        int count = 1;
        for (ReplenishmentRequest request : replenishmentRequests) {
            try {
                System.out.printf("%-5d %-20s %-15d %-15s %-15s %-15s%n", 
                    count, 
                    request.getMedicineName(), 
                    request.getRequestedStock(), 
                    request.getStatus(), 
                    request.getRequestBy(), 
                    request.getIsNewMedicine() ? "Yes" : "No"
                );
                count++;
            } catch (Exception e) {
                System.out.println("Error retrieving request details: " + e.getMessage());
            }
        }
        System.out.println("=========================================================================================");
    }
    

}
