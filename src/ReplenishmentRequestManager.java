import java.util.List;
import java.util.Scanner;

public class ReplenishmentRequestManager {
    private List<ReplenishmentRequest> replenishmentRequests;
    private InitialData data;

    public ReplenishmentRequestManager(InitialData data, List<ReplenishmentRequest> replenishmentRequests) {
        this.data = data;
        this.replenishmentRequests = replenishmentRequests;
    }

    public void manageRequests() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nReplenishment Request Management:");
            System.out.println("1. View Replenishment Requests");
            System.out.println("2. Approve Replenishment Request");
            System.out.println("3. Go back to the Inventory Menu");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewReplenishmentRequests();
                    break;

                case 2:
                    approveReplenishment(scanner);
                    break;

                case 3:
                    return; // Go back to the inventory menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
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
