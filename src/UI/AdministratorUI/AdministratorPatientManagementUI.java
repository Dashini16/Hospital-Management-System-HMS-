import java.io.IOException;
import java.util.Scanner;

public class AdministratorPatientManagementUI {

    private InitialData data;
    private PatientManagementControl patientManager;

    public AdministratorPatientManagementUI(InitialData data) {
        this.data = data;
        this.patientManager = new PatientManagementControl(data);
    }

    public void managePatients() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose an action:");
            System.out.println("1. Display All Patients");
            System.out.println("2. Add New Patient");
            System.out.println("3. Update Patient Information");
            System.out.println("4. Delete Patient");
            System.out.println("5. Go back to the menu");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    patientManager.displayPatients();
                    break;

                case 2:
                    patientManager.addPatient(scanner);
                    break;

                case 3:
                    patientManager.updatePatient(scanner);
                    break;

                case 4:
                    patientManager.deletePatient(scanner);
                    break;

                case 5:
                    return; // Go back to the menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
