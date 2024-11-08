package ui;

import users.*;

import appointments.*;

import java.util.Scanner;


import authorization.*;
import databackups.DatabaseBackUp;
import filereaders.*;
import usermanagement.*;


public class AdministratorUI {
    private Administrator administrator;

    public AdministratorUI(Administrator administrator) {
        this.administrator = administrator;
    }

    public void displayMenu() {
        // Print the administrator menu to the console
        InitialDataMedicine dataMedicine = new InitialDataMedicine();
        InitialDataStaff data = new InitialDataStaff();
        InitialDataPatient dataPatient = new InitialDataPatient();
        InitialDataAppointments dataAppointments = new InitialDataAppointments();
        InitialDataAppointmentSlots dataAppointmentSlots = new InitialDataAppointmentSlots();
        data.importData(); // Load data
        AuthorizationControl authControl = new AuthorizationControl();
        // Load users into AuthorizationControl (assuming you have this in your code)
        authControl.loadCredentialsFromStaff(data, dataPatient); 

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Administrator Menu:");
            System.out.println("===================================");
            System.out.println("1. View and Manage Hospital Staff");
            System.out.println("2. View Appointments Details");
            System.out.println("3. Manage Medication Inventory");
            System.out.println("4. Patients Management");
            System.out.println("5. Change Password");
            System.out.println("6. Backup Database");
            System.out.println("7. Logout");
            System.out.println("===================================");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("\n==================================="); 
                    System.out.println("View and Manage Hospital Staff");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AdministratorStaffManagementUI staffManager = new AdministratorStaffManagementUI(data);
                    staffManager.manageStaff();
                    break;
                case 2:
                    // Call the appropriate method for appointments
                    System.out.println("\n==================================="); 
                    System.out.println("View Appointments Details");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentManagementControl appointmentManager = new AppointmentManagementControl(dataMedicine, dataPatient, data,dataAppointments, dataAppointmentSlots);
                    appointmentManager.viewonlyallAppointments();
                    break;
                case 3:
                    System.out.println("\n==================================="); 
                    System.out.println("Manage Medication Inventory");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AdministratorInventoryManagementUI inventoryManager = new AdministratorInventoryManagementUI(dataMedicine);
                    inventoryManager.manageInventory();
                    break;
                case 4:
                    System.out.println("\n==================================="); 
                    System.out.println("Patients Management");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AdministratorPatientManagementUI patientManager = new AdministratorPatientManagementUI(dataPatient);
                    patientManager.managePatients();
                    break;
                case 5:
                    System.out.println("\n==================================="); 
                    System.out.println("Change Password");
                    System.out.println("===================================\n"); // Add a border for the login section
                    PasswordManagement passwordManagement = new PasswordManagement(data, dataPatient);

                    //staffManagementControl.changeStaffPassword(scanner);
                    passwordManagement.changePassword(scanner);
                    break;
                case 6:
                    System.out.println("\n==================================="); 
                    System.out.println("Backup Database");
                    System.out.println("===================================\n"); // Add a border for the login section
                    DatabaseBackUp.backupDatabase();
                    System.out.println("Backup Successful");
                    break;
                case 7:
                    System.out.println("Logging Out...");
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
