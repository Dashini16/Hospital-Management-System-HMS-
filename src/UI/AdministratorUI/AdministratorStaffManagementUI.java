import java.io.IOException;
import java.util.Scanner;

public class AdministratorStaffManagementUI {
    private InitialData data;
    private StaffManagementControl staffManager;

    public AdministratorStaffManagementUI(InitialData data) {
        this.data = data;
        this.staffManager = new StaffManagementControl(data); // Initialize staff manager
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
                    staffManager.addDoctor(scanner); // Call HospitalStaffManager methods
                    break;
                case 2:
                    staffManager.addPharmacist(scanner);
                    break;
                case 3:
                    staffManager.addAdministrator(scanner);
                    break;
                case 4:
                    staffManager.updateStaff(scanner);
                    break;
                case 5:
                    staffManager.deleteStaff(scanner);
                    break;
                case 6:
                    staffManager.displayStaff();
                    break;
                case 7:
                    return; // Go back to the menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
