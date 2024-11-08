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
    
                //Medicine medicine = findMedicineByName(request.getMedicineName());
                UserLookup userLookup = new UserLookup();
                Medicine medicine = userLookup.findByID(request.getMedicineName(), medicineData.getLists(), med -> med.getName().equalsIgnoreCase(request.getMedicineName()));
    
                if (medicine != null) {
                    // Update stock and set request status to fulfilled
                    medicine.setInitialStock(medicine.getInitialStock() + request.getRequestedStock());
                    request.setStatus(RequestStatus.FULFILLED);
                    System.out.println("Replenishment approved for " + request.getMedicineName() +
                                       " with " + request.getRequestedStock() + " units.");
    
                    // Save changes to CSV files
                    try {
                        medicineData.rewriteMedicines("hms/src/data/Medicine_List.csv");
                        replenishmentRequestData.rewriteReplenishmentRequests("hms/src/data/Replenishment_Requests.csv", replenishmentRequests);
                        System.out.println("Updates saved to file.");
                    } catch (IOException e) {
                        System.out.println("Error updating files: " + e.getMessage());
                    }
                    break; // Exit after successful approval
                } else {
                    System.out.println("Medicine not found in inventory for request: " + request.getMedicineName());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    
    public void viewReplenishmentRequests() {
        System.out.println("\nCurrent Replenishment Requests:");
    
        if (replenishmentRequests == null || replenishmentRequests.isEmpty()) {
            System.out.println("No replenishment requests available.");
            return;
        }
    
        int count = 1;
        for (ReplenishmentRequest request : replenishmentRequests) {
            try {
                System.out.println(count + ". " + request.getMedicineName() + ", " +
                                   request.getRequestedStock() + ", " + request.getStatus());
                count++;
            } catch (Exception e) {
                System.out.println("Error retrieving request details: " + e.getMessage());
            }
        }
    }

}
