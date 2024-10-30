package medicinemanagements;

import filereaders.*;

import java.io.IOException;
import java.util.List;

import enums.RequestStatus;
import filereaders.InitialData;

import enums.RequestStatus;
import filereaders.InitialData;
import java.util.Scanner;

public class ReplenishmentRequestManagementControl {
    private List<ReplenishmentRequest> replenishmentRequests;
    private InitialData data;

    public ReplenishmentRequestManagementControl(InitialData data, List<ReplenishmentRequest> replenishmentRequests) {
        this.data = data;
        this.replenishmentRequests = data.getReplenishmentRequests();
    }


    public void viewReplenishmentRequests() {
        System.out.println("\nCurrent Replenishment Requests:");
        for (ReplenishmentRequest request : replenishmentRequests) {
            System.out.println("Medicine: " + request.getMedicineName() + 
                " | Requested Stock: " + request.getRequestedStock() + 
                " | Status: " + request.getStatus());
        }
    }

    public void approveReplenishment(Scanner scanner) {
        System.out.print("Enter the name of the medicine to approve replenishment for: ");
        String name = scanner.nextLine().trim();
    
        boolean requestApproved = false; // Flag to check if any request was approved
    
        for (ReplenishmentRequest request : replenishmentRequests) {
            if (request.getMedicineName().equalsIgnoreCase(name)) {
                // Approve the request and update the stock
                Medicine medicine = findMedicineByName(name);
                if (medicine != null) {
                    // Update the initial stock
                    medicine.setInitialStock(medicine.getInitialStock() + request.getRequestedStock());
                    request.setStatus(RequestStatus.FULFILLED);
                    System.out.println("Replenishment approved for " + name + " with " + request.getRequestedStock() + " units.");
    
                    requestApproved = true; // Mark that at least one request was approved
                } else {
                    System.out.println("Medicine not found in inventory for request.");
                }
            }
        }
    
        // Check if any requests were approved to update the CSV files
        if (requestApproved) {
            try {
                // Update the Medicine_List.csv with the new stock values
                data.rewriteMedicines("hms\\src\\data\\Medicine_List.csv");
                System.out.println("Medicine stock updated in the file.");
    
                // Update the Replenishment_Requests.csv with the updated statuses
                data.rewriteReplenishmentRequests("hms\\src\\data\\Replenishment_Requests.csv", replenishmentRequests);
                System.out.println("Replenishment requests updated in the file.");
            } catch (IOException e) {
                System.out.println("Error updating files: " + e.getMessage());
            }
        } else {
            System.out.println("No replenishment request found for " + name);
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
