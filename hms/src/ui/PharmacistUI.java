package ui;

import users.Pharmacist;
import filereaders.InitialData;
import medicinemanagements.*;
import java.util.Scanner;

public class PharmacistUI {
    private Pharmacist pharmacist;
    //private InitialData data;

    public PharmacistUI(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
        //this.data = data;
    }

    public void displayMenu() {
        InitialData data = new InitialData();
        data.importData(); // Load data
        // Print the pharmacist menu to the console
        while (true) {
            System.out.println("Pharmacist Menu:");
            System.out.println("1. View Appointment Outcome Record");
            System.out.println("2. Update Prescription Status");
            System.out.println("3. View Medication Inventory");
            System.out.println("4. Submit Replenishment Request");
            System.out.println("5. Logout");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Add code to view appointment outcome record
                    System.out.println("Viewing appointment outcome record...");
                    break;
                case 2:
                    // Add code to update prescription status
                    System.out.println("Updating prescription status...");
                    break;
                case 3:
                    // Add code to view medication inventory
                    System.out.println("Viewing medication inventory...");
                    InventoryDisplayControl displayManager = new InventoryDisplayControl(data);
                    displayManager.displayInventory();
                    break;
                case 4:
                    // Add code to submit replenishment request
                    //System.out.println("Submitting replenishment request...");
                    RequestCreateControl requestCreateControl = new RequestCreateControl(data);
                    requestCreateControl.createReplenishmentRequest(scanner);
                    break;
                case 5:
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
