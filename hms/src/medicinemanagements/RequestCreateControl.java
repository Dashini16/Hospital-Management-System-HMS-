package medicinemanagements;

import filereaders.InitialDataMedicine;
import filereaders.InitialDataStaff;
import filereaders.InitialDatareplenishmentRequest;
import lookups.UserLookup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import authorization.AuthorizationControl;
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
    // Automatically get the Pharmacist ID through AuthorizationControl
    String requestBy = AuthorizationControl.getCurrentUserId();
    
    System.out.println("\nRequest replenishment for existing low-stock medicine.");
    System.out.println("\nLow-stock Medicines:");
    System.out.printf("%-5s %-20s %-15s %-15s%n", "No.", "Medicine Name", "Stock Quantity", "Low Stock Alert");
    System.out.println("-------------------------------------------------------");

    // Fetch low-stock medicines
    List<Medicine> lowStockMedicines = data.getLists().stream()
            .filter(med -> med.getInitialStock() < med.getLowStockLevelAlert())
            .collect(Collectors.toList());

    if (lowStockMedicines.isEmpty()) {
        System.out.println("No low-stock medicines available for replenishment.");
        return;
    }

    // Display low-stock medicines
    for (int i = 0; i < lowStockMedicines.size(); i++) {
        Medicine medicine = lowStockMedicines.get(i);
        System.out.printf("%-5d %-20s %-15d %-15d%n", 
                          i + 1, 
                          medicine.getName(), 
                          medicine.getInitialStock(), 
                          medicine.getLowStockLevelAlert());
    }

    // Select a medicine to replenish
    System.out.print("\nSelect the number of the medicine to replenish: ");
    int medicineIndex;
    try {
        medicineIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (medicineIndex < 0 || medicineIndex >= lowStockMedicines.size()) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a valid number.");
        return;
    }

    Medicine selectedMedicine = lowStockMedicines.get(medicineIndex);
    String medicineName = selectedMedicine.getName();

    // Request quantity from the user
    int requestedStock;
    while (true) {
        System.out.print("Enter requested stock quantity (positive integer): ");
        try {
            requestedStock = Integer.parseInt(scanner.nextLine().trim());
            if (requestedStock <= 0) {
                System.out.println("Error: Please enter a positive integer for the stock quantity.");
            } else {
                break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input. Please enter a valid integer for the stock quantity.");
        }
    }

    // Create and save the replenishment request
    ReplenishmentRequest request = new ReplenishmentRequest(medicineName, requestedStock, requestBy);
    request.setStatus(RequestStatus.PENDING);
    replenishmentRequests.add(request); // Add to in-memory list
    System.out.println("Replenishment request created for " + medicineName + " with quantity " + requestedStock);

    // Save request to CSV file
    try {
        dataReplenishmentRequest.appendData("hms/src/data/Replenishment_Requests.csv", request);
        System.out.println("Replenishment request saved successfully.");
    } catch (IOException e) {
        System.out.println("Error saving replenishment request: " + e.getMessage());
    }
}
}