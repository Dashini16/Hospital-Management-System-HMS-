package ui;

import filereaders.InitialDataMedicine;
import filereaders.InitialDataStaff;
import filereaders.InitialDatareplenishmentRequest;
import medicinemanagements.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AdministratorReplenishmentRequestManagementUI {
    private InitialDatareplenishmentRequest data;
    private ReplenishmentRequestManagementControl replenishmentManager;
    private List<ReplenishmentRequest> replenishmentRequests;
    private InitialDataMedicine dataMedicine;

    public AdministratorReplenishmentRequestManagementUI(InitialDatareplenishmentRequest data,
            InitialDataMedicine dataMedicine, List<ReplenishmentRequest> replenishmentRequests) {

        // Initialize data and dataMedicine, ensuring they are not null
        if (data == null || dataMedicine == null) {
            throw new IllegalArgumentException("Data and dataMedicine cannot be null.");
        }

        this.data = data;
        this.dataMedicine = dataMedicine;
        

        data.reloadData();
        dataMedicine.reloadData();

        this.replenishmentManager = new ReplenishmentRequestManagementControl(replenishmentRequests, dataMedicine,
                data);

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
                    System.out.println("\n===================================");
                    System.out.println("Replenishment Requests");
                    System.out.println("===================================\n");
                    data.reloadData();
                    replenishmentManager.viewReplenishmentRequests();
                    break;

                case 2:
                    System.out.println("\n===================================");
                    System.out.println("Replenishment Requests");
                    System.out.println("===================================\n");
                    data.reloadData();
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
