package ui;

import users.Administrator;
import ui.AdministratorStaffManagementUI;
import ui.AdministratorPatientManagementUI;
import ui.AdministratorInventoryManagementUI;

import java.util.Scanner;
import filereaders.InitialData; // Adjust this import according to where InitialData is located

public class AdministratorUI {
    private Administrator administrator;

    public AdministratorUI(Administrator administrator) {
        this.administrator = administrator;
    }

    public void displayMenu() {
        // Print the administrator menu to the console
        InitialData data = new InitialData();
        data.importData(); // Load data

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Administrator Menu:");
            System.out.println("1. View and Manage Hospital Staff");
            System.out.println("2. View Appointments Details");
            System.out.println("3. Manage Medication Inventory");
            System.out.println("4. Patients Management");
            System.out.println("5. Logout");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    AdministratorStaffManagementUI staffManager = new AdministratorStaffManagementUI(data);
                    staffManager.manageStaff();
                    break;
                case 2:
                    // Call the appropriate method for appointments
                    break;
                case 3:
                    AdministratorInventoryManagementUI inventoryManager = new AdministratorInventoryManagementUI(data);
                    inventoryManager.manageInventory();
                    break;
                case 4:
                    AdministratorPatientManagementUI patientManager = new AdministratorPatientManagementUI(data);
                    patientManager.managePatients();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
