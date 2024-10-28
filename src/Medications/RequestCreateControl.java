import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RequestCreateControl {
    private InitialData data;
    private List<ReplenishmentRequest> replenishmentRequests; // List to store replenishment requests

    public RequestCreateControl(InitialData data) {
        this.data = data;
        this.replenishmentRequests = new ArrayList<>(); // Initialize the request list
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

    private Medicine findMedicineByName(String name) {
        for (Medicine medicine : data.getMedicines()) {
            if (medicine.getName().equalsIgnoreCase(name)) {
                return medicine;
            }
        }
        return null; // Not found
    }
}