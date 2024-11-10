package ui;

import reportmanagement.PatientReportControl;
import filereaders.InitialDataPatient;

import java.util.Scanner;

public class AdministratorReportUI {
    private PatientReportControl reportControl;

    public AdministratorReportUI(InitialDataPatient dataPatient) {
        this.reportControl = new PatientReportControl(dataPatient);
    }

    public void displayReportMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== Patient Report Analysis =====");
            System.out.println("1. Age Distribution of Patients");
            System.out.println("2. Diagnosis Distribution of Patients");
            System.out.println("3. Gender Distribution of Patients");
            System.out.println("4. Common Treatments Distribution");
            System.out.println("5. Average Age by Diagnosis");
            System.out.println("6. Exit");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    reportControl.displayAgeDistribution();
                    break;
                case "2":
                    reportControl.displayDiagnosisDistribution();
                    break;
                case "3":
                    reportControl.displayGenderDistribution();
                    break;
                case "4":
                    reportControl.displayTreatmentDistribution();
                    break;
                case "5":
                    reportControl.displayAverageAgeByDiagnosis();
                    break;
                case "6":
                    running = false;
                    System.out.println("Exiting Report Analysis...");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }
}
