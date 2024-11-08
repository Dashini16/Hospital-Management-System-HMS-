package medicinemanagements;

import filereaders.InitialDataMedicine;
import filereaders.InitialDataStaff;
import filereaders.InitialDatareplenishmentRequest;
import lookups.UserLookup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import enums.RequestStatus;

public class RequestCreateControl {
    private InitialDataMedicine data;
    private List<ReplenishmentRequest> replenishmentRequests; // List to store replenishment requests
    private InitialDatareplenishmentRequest dataReplenishmentRequest;

    public RequestCreateControl(InitialDataMedicine data, InitialDatareplenishmentRequest dataReplenishmentRequest) {
        this.data = data;
        this.replenishmentRequests = new ArrayList<>(); // Initialize the request list
        this.dataReplenishmentRequest = dataReplenishmentRequest;
        data.reloadData();
        dataReplenishmentRequest.reloadData();
    }

    public void createReplenishmentRequest(Scanner scanner) {
        System.out.print("Enter medicine name: ");
        String name = scanner.nextLine().trim(); // Trim whitespace from input
    
        // Check if the medicine exists
        //Medicine existingMedicine = findMedicineByName(name);
        UserLookup userLookup = new UserLookup();
        Medicine existingMedicine = userLookup.findByID(name, data.getLists(), med -> med.getName().equalsIgnoreCase(name));
    
        if (existingMedicine == null) {
            System.out.println("Medicine not found. Please enter a valid medicine name.");
            return; // Exit the method if medicine does not exist
        }
    
        int requestedStock = 0; // Variable to hold the requested stock
        boolean validInput = false; // Flag to check if the input is valid
    
        // Loop until valid stock quantity is entered
        while (!validInput) {
            System.out.print("Enter requested stock quantity (positive integer): ");
            try {
                requestedStock = Integer.parseInt(scanner.nextLine()); // Read and parse input
                if (requestedStock <= 0) {
                    System.out.println("Error: Please enter a positive integer for the stock quantity.");
                }  else {
                    validInput = true; // Input is valid
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid integer for the stock quantity.");
            }
        }
    
        // Create and add the replenishment request
        ReplenishmentRequest request = new ReplenishmentRequest(name, requestedStock);
        request.setStatus(RequestStatus.PENDING);
        replenishmentRequests.add(request); // Add to in-memory list
        System.out.println("Replenishment request created for " + name + " with quantity " + requestedStock);
    
        // Save request to CSV file
        try {
            dataReplenishmentRequest.appendData("hms/src/data/Replenishment_Requests.csv", request);
            System.out.println("Replenishment request saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving replenishment request: " + e.getMessage());
        }
    }
 
}