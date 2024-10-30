import java.util.Scanner;

public class DoctorUI {
    private Doctor doctor;

    public DoctorUI(Doctor doctor) {
        this.doctor = doctor;
    }

    public void displayMenu() {
        InitialData data = new InitialData();
        data.importData(); // Load data
        // Print the doctor menu to the console
        while (true) {
            System.out.println("Doctor Menu:");
            System.out.println("1. View Patient Medical Records");
            System.out.println("2. Update Patient Medical Records");
            System.out.println("3. View Personal Schedule");
            System.out.println("4. Set Availability for Appointments");
            System.out.println("5. Accept or Decline Appointment Requests");
            System.out.println("6. View Upcoming Appointments");
            System.out.println("7. Record Appointment Outcome");
            System.out.println("8. Logout");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Add code to view patient medical records
                    System.out.println("Viewing patient medical records...");
                    break;
                case 2:
                    // Add code to update patient medical records
                    System.out.println("Updating patient medical records...");
                    break;
                case 3:
                    // Add code to view personal schedule
                    System.out.println("Viewing personal schedule...");
                    break;
                case 4:
                    // Add code to set availability for appointments
                    System.out.println("Setting availability for appointments...");
                    break;
                case 5:
                    // Add code to accept or decline appointment requests
                    System.out.println("Accepting or declining appointment requests...");
                    break;
                case 6:
                    // Add code to view upcoming appointments
                    System.out.println("Viewing upcoming appointments...");
                    break;
                case 7:
                    // Add code to record appointment outcome
                    System.out.println("Recording appointment outcome...");

                    break;
                case 8:
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
