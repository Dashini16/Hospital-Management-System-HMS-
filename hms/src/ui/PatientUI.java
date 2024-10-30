package ui;

import users.Patient;
import java.util.Scanner;

public class PatientUI {
    private Patient patient;

    public PatientUI(Patient patient) {
        this.patient = patient;
    }

    public void displayMenu() {
        // Print the patient menu to the console
        while (true) {
            System.out.println("Patient Menu:");
            System.out.println("1. View Medical Record");
            System.out.println("2. Update Personal Information");
            System.out.println("3. View Available Appointment Slots");
            System.out.println("4. Schedule an Appointment");
            System.out.println("5. Reschedule an Appointment");
            System.out.println("6. Cancel an Appointment");
            System.out.println("7. View Scheduled Appointments");
            System.out.println("8. View Past Appointment Outcome Records");
            System.out.println("9. Logout");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Code to view medical record
                    System.out.println("Viewing medical record...");
                    break;
                case 2:
                    // Code to update personal information
                    System.out.println("Updating personal information...");
                    break;
                case 3:
                    // Code to view available appointment slots
                    System.out.println("Viewing available appointment slots...");
                    break;
                case 4:
                    // Code to schedule an appointment
                    System.out.println("Scheduling an appointment...");
                    break;
                case 5:
                    // Code to reschedule an appointment
                    System.out.println("Rescheduling an appointment...");
                    break;
                case 6:
                    // Code to cancel an appointment
                    System.out.println("Canceling an appointment...");
                    break;
                case 7:
                    // Code to view scheduled appointments
                    System.out.println("Viewing scheduled appointments...");
                    break;
                case 8:
                    // Code to view past appointment outcome records
                    System.out.println("Viewing past appointment outcome records...");
                    break;
                case 9:
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
