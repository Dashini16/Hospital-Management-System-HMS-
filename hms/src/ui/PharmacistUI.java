package ui;

import users.*;
import filereaders.*;
import medicinemanagements.*;
import usermanagement.PasswordManagement;
import authorization.AuthorizationControl;


import java.util.Scanner;

import appointments.AppointmentManagementControl;

public class PharmacistUI {
    private Pharmacist pharmacist;
    //private InitialData data;
    //private AuthorizationControl authControl; 

    public PharmacistUI(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
        //this.data = data;
    }

    public void displayMenu() {
        InitialDataMedicine dataMedicine = new InitialDataMedicine();
        InitialDataStaff data = new InitialDataStaff();
        InitialDataPatient dataPatient = new InitialDataPatient();
        InitialDataAppointments dataAppointments = new InitialDataAppointments();
        InitialDataAppointmentSlots dataAppointmentSlots = new InitialDataAppointmentSlots();
        InitialDatareplenishmentRequest dataReplenishmentRequest = new InitialDatareplenishmentRequest();

        dataMedicine.reloadData();
        data.reloadData();
        dataPatient.reloadData();
        dataAppointments.reloadData();
        dataAppointmentSlots.reloadData();
        dataReplenishmentRequest.reloadData();

        // Print the pharmacist menu to the console
        AuthorizationControl authControl = new AuthorizationControl();
        // Load users into AuthorizationControl (assuming you have this in your code)
        authControl.loadCredentialsFromStaff(data,dataPatient); 

        while (true) {
            System.out.println("Pharmacist Menu:");
            System.out.println("===================================");
            System.out.println("1. View Appointment Outcome Record");
            System.out.println("2. Update Prescription Status");
            System.out.println("3. View Medication Inventory");
            System.out.println("4. Submit Replenishment Request");
            System.out.println("5. Change Password");
            System.out.println("6. Logout");
            System.out.println("===================================");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Add code to view appointment outcome record

                    System.out.println("\n==================================="); 
                    System.out.println("View Appointment Outcome Record");
                    System.out.println("===================================\n"); 
                    AppointmentManagementControl appointmentManagementControl = new AppointmentManagementControl(dataMedicine, dataPatient, data,dataAppointments, dataAppointmentSlots);
                    appointmentManagementControl.viewOutcomeRecords();
                    break;
                case 2:
                    // Add code to update prescription status
                    
                    System.out.println("\n==================================="); 
                    System.out.println("Update Prescription Status");
                    System.out.println("===================================\n"); // Add a border for the login section
                    appointmentManagementControl = new AppointmentManagementControl(dataMedicine, dataPatient, data,dataAppointments, dataAppointmentSlots);
                    appointmentManagementControl.updatePrescriptionStatus();
                    break;
                case 3:
                    // Add code to view medication inventory
                    //System.out.println("Viewing medication inventory...");
                    System.out.println("\n==================================="); 
                    System.out.println("View Medication Inventory");
                    System.out.println("===================================\n"); // Add a border for the login section
                    InventoryManagementControl inventoryManager = new InventoryManagementControl(dataMedicine);
                    inventoryManager.displayInventory();
                    break;
                case 4:
                    // Add code to submit replenishment request
                    //System.out.println("Submitting replenishment request...");
                    System.out.println("\n==================================="); 
                    System.out.println("Submit Replenishment Request");
                    System.out.println("===================================\n"); // Add a border for the login section
                    RequestCreateControl requestCreateControl = new RequestCreateControl(dataMedicine, dataReplenishmentRequest);
                    requestCreateControl.createReplenishmentRequest(scanner);
                    break;
                case 5:
                    System.out.println("\n==================================="); 
                    System.out.println("Change Password");
                    System.out.println("===================================\n"); // Add a border for the login section
                    PasswordManagement staffManagementControl = new PasswordManagement(data,dataPatient);
                    //staffManagementControl.changeStaffPassword(scanner);
                    staffManagementControl.changePassword(scanner);
                    break;
                case 6:
                    System.out.println("Logging Out...");
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
