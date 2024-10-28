import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AdministratorReplenishmentRequestManagementUI {
    private List<ReplenishmentRequest> replenishmentRequests;
    private InitialData data;
    private ReplenishmentRequestManagementControl replenishmentManager;

    public AdministratorReplenishmentRequestManagementUI(InitialData data, List<ReplenishmentRequest> replenishmentRequests) {
        this.data = data;
        this.replenishmentRequests = data.getReplenishmentRequests();
        this.replenishmentManager = new ReplenishmentRequestManagementControl(data, replenishmentRequests);
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
                    data.reloadData();
                    replenishmentManager.viewReplenishmentRequests();
                    break;

                case 2:
                    replenishmentManager.approveReplenishment(scanner);
                    data.reloadData();
                    break;

                case 3:
                    return; // Go back to the inventory menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


}
