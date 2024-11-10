package ui;
import filereaders.*;
import usermanagement.*;

import java.util.Scanner;

public class AdministratorPatientManagementUI {

    private InitialDataPatient data;
    private PatientManagementControl patientManager;

    public AdministratorPatientManagementUI(InitialDataPatient data) {
        this.data = data;
        this.patientManager = new PatientManagementControl(data);
        data.reloadData();
        
    }

    public void managePatients() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Display All Patients");
            System.out.println("2. Add New Patient");
            //System.out.println("3. Update Patient Information");
            System.out.println("3. Delete Patient");
            System.out.println("4. Go back to the menu");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("\n===================================");
                    System.out.println("Display All Patients");
                    System.out.println("===================================\n");
                    patientManager.displayPatients();
                    break;

                case 2:
                    System.out.println("\n===================================");
                    System.out.println("Add New Patient");
                    System.out.println("===================================\n");
                    patientManager.addPatient(scanner);
                    break;

                //case 3:
                    //patientManager.updatePatient(scanner);
                    //break;

                case 3:
                    System.out.println("\n===================================");
                    System.out.println("Delete Patient");
                    System.out.println("===================================\n");
                    patientManager.deletePatient(scanner);
                    break;

                case 4:
                    return; // Go back to the menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
