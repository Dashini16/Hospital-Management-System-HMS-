import java.util.Scanner;

public class HospitalStaffManager {
    private InitialData data;

    public HospitalStaffManager(InitialData data) {
        this.data = data;
    }

    public void manageStaff() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Current Hospital Staff:");
            for (Doctor doctor : data.getDoctors()) {
                System.out.println("Doctor: " + doctor.getName() + " (ID: " + doctor.getUserID() + ")");
            }
            for (Pharmacist pharmacist : data.getPharmacists()) {
                System.out.println("Pharmacist: " + pharmacist.getName() + " (ID: " + pharmacist.getUserID() + ")");
            }
            for (Administrator admin : data.getAdministrators()) {
                System.out.println("Administrator: " + admin.getName() + " (ID: " + admin.getUserID() + ")");
            }

            System.out.println("\nChoose an action:");
            System.out.println("1. Add Doctor");
            System.out.println("2. Add Pharmacist");
            System.out.println("3. Add Administrator");
            System.out.println("4. Go back to the menu");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Add Doctor
                    System.out.print("Enter doctor's name: ");
                    String doctorName = scanner.nextLine();
                    System.out.print("Enter doctor's ID: ");
                    String doctorID = scanner.nextLine();
                    System.out.print("Enter doctor's contact info: ");
                    String contactInfo = scanner.nextLine();
                    
                    // Create a new Doctor object
                    Doctor newDoctor = new Doctor(doctorID, doctorName, contactInfo);
                    data.getDoctors().add(newDoctor);
                    System.out.println("Doctor added successfully: " + doctorName);
                    break;

                case 2:
                    // Add Pharmacist
                    System.out.print("Enter pharmacist's name: ");
                    String pharmacistName = scanner.nextLine();
                    System.out.print("Enter pharmacist's ID: ");
                    String pharmacistID = scanner.nextLine();
                    System.out.print("Enter pharmacist's contact info: ");
                    String contactInfo_pharmacist = scanner.nextLine();
                    
                    // Create a new Pharmacist object
                    Pharmacist newPharmacist = new Pharmacist(pharmacistID, pharmacistName, contactInfo_pharmacist);
                    data.getPharmacists().add(newPharmacist);
                    System.out.println("Pharmacist added successfully: " + pharmacistName);
                    break;

                case 3:
                    // Add Administrator
                    System.out.print("Enter administrator's name: ");
                    String adminName = scanner.nextLine();
                    System.out.print("Enter administrator's ID: ");
                    String adminID = scanner.nextLine();
                    System.out.print("Enter administrator's contact info: ");
                    String contactInfo_administrator = scanner.nextLine();
                    
                    // Create a new Administrator object
                    Administrator newAdmin = new Administrator(adminID, adminName, contactInfo_administrator);
                    data.getAdministrators().add(newAdmin);
                    System.out.println("Administrator added successfully: " + adminName);
                    break;

                case 4:
                    return; // Go back to the menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
