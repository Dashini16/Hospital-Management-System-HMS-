package ui;
import filereaders.InitialDataStaff;
import usermanagement.StaffManagementControl;

import java.util.Scanner;
//import Users.*;

//import HospitalStaffManagementControl;
public class AdministratorStaffManagementUI {
    private InitialDataStaff data;
    private StaffManagementControl staffManager;

    public AdministratorStaffManagementUI(InitialDataStaff data) {
        this.data = data;
        this.staffManager = new StaffManagementControl(data); // Initialize staff manager
        data.reloadData();
    }

    public void manageStaff() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Add Doctor");
            System.out.println("2. Add Pharmacist");
            System.out.println("3. Add Administrator");
            System.out.println("4. Update Staff");
            System.out.println("5. Delete Staff");
            System.out.println("6. View Staff");
            System.out.println("7. Go back to the menu");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    System.out.println("\n==================================="); 
                    System.out.println("Add Doctor");
                    System.out.println("===================================\n"); // Add a border for the login section
                    staffManager.addDoctor(scanner); // Call HospitalStaffManager methods
                    break;
                case 2:
                System.out.println("\n==================================="); 
                System.out.println("Add Pharmacist");
                System.out.println("===================================\n"); // Add a border for the login section
                    staffManager.addPharmacist(scanner);
                    break;
                case 3:
                System.out.println("\n==================================="); 
                System.out.println("Add Administrator");
                System.out.println("===================================\n"); // Add a border for the login section
                    staffManager.addAdministrator(scanner);
                    break;
                case 4:
                    System.out.println("\n==================================="); 
                    System.out.println("Update Staff");
                    System.out.println("===================================\n"); // Add a border for the login section
                    staffManager.updateStaff(scanner);
                    break;
                case 5:
                    System.out.println("\n==================================="); 
                    System.out.println("Delete Staff");
                    System.out.println("===================================\n"); // Add a border for the login section
                    staffManager.deleteStaff(scanner);
                    break;
                case 6:
                    System.out.println("\n==================================="); 
                    System.out.println("View Staff");
                    System.out.println("===================================\n"); // Add a border for the login section
                    staffManager.displayStaff();
                    break;
                case 7:
                    System.out.println("\n==================================="); 
                    System.out.println("Going back to the menu...");
                    System.out.println("===================================\n"); // Add a border for the login section
                    return; // Go back to the menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
