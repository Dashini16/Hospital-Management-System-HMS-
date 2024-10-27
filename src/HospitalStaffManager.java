import java.io.IOException;
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
            displayStaff(); // Call method to display staff

            System.out.println("\nChoose an action:");
            System.out.println("1. Add Doctor");
            System.out.println("2. Add Pharmacist");
            System.out.println("3. Add Administrator");
            System.out.println("4. Update Staff");
            System.out.println("5. Delete Staff");
            System.out.println("6. Go back to the menu");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addDoctor(scanner);
                    break;
                case 2:
                    addPharmacist(scanner);
                    break;
                case 3:
                    addAdministrator(scanner);
                    break;
                case 4:
                    updateStaff(scanner);
                    break;
                case 5:
                    deleteStaff(scanner);
                    break;
                case 6:
                    return; // Go back to the menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayStaff() {
        for (Doctor doctor : data.getDoctors()) {
            doctor.toStrings();
        }
        for (Pharmacist pharmacist : data.getPharmacists()) {
            pharmacist.toStrings();
        }
        for (Administrator admin : data.getAdministrators()) {
            admin.toStrings();
        }
    }

    private void addDoctor(Scanner scanner) {
        System.out.print("Enter doctor's ID: ");
        String doctorID = scanner.nextLine();
        System.out.print("Enter doctor's name: ");
        String doctorName = scanner.nextLine();
        System.out.print("Enter doctor's gender: ");
        String doctorGender = scanner.nextLine();
        System.out.print("Enter doctor's age: ");
        int doctorAge = scanner.nextInt();

        Doctor newDoctor = new Doctor(doctorID, doctorName, doctorGender, doctorAge);
        try {
            data.appendStaff("../data/Staff_List.csv", newDoctor); // Append to CSV
        } catch (IOException e) {
            System.out.println("Error saving doctor: " + e.getMessage());
        }
        System.out.println("Doctor added successfully: " + doctorName);
    }

    private void addPharmacist(Scanner scanner) {
        System.out.print("Enter pharmacist's ID: ");
        String pharmacistID = scanner.nextLine();
        System.out.print("Enter pharmacist's name: ");
        String pharmacistName = scanner.nextLine();
        System.out.print("Enter pharmacist's gender: ");
        String pharmacistGender = scanner.nextLine();
        System.out.print("Enter pharmacist's age: ");
        int pharmacistAge = scanner.nextInt();

        Pharmacist newPharmacist = new Pharmacist(pharmacistID, pharmacistName, pharmacistGender, pharmacistAge);
        try {
            data.appendStaff("../data/Staff_List.csv", newPharmacist); // Append to CSV
        } catch (IOException e) {
            System.out.println("Error saving pharmacist: " + e.getMessage());
        }
        System.out.println("Pharmacist added successfully: " + pharmacistName);
    }

    private void addAdministrator(Scanner scanner) {
        System.out.print("Enter administrator's ID: ");
        String adminID = scanner.nextLine();
        System.out.print("Enter administrator's name: ");
        String adminName = scanner.nextLine();
        System.out.print("Enter administrator's gender: ");
        String adminGender = scanner.nextLine();
        System.out.print("Enter administrator's age: ");
        int adminAge = scanner.nextInt();

        Administrator newAdmin = new Administrator(adminID, adminName, adminGender, adminAge);
        try {
            data.appendStaff("../data/Staff_List.csv", newAdmin); // Append to CSV
        } catch (IOException e) {
            System.out.println("Error saving administrator: " + e.getMessage());
        }
        System.out.println("Administrator added successfully: " + adminName);
    }

    private void updateStaff(Scanner scanner) {
        System.out.print("Enter the ID of the staff to update: ");
        String staffID = scanner.nextLine();

        User staff = findStaffById(staffID);
        if (staff == null) {
            System.out.println("Staff not found.");
            return;
        }

        System.out.print("Enter new name (leave blank for no change): ");
        String newName = scanner.nextLine();
        System.out.print("Enter new gender (leave blank for no change): ");
        String newGender = scanner.nextLine();
        System.out.print("Enter new age (leave blank for no change): ");
        String newAgeInput = scanner.nextLine();
        Integer newAge = newAgeInput.isEmpty() ? null : Integer.parseInt(newAgeInput);

        // Update fields if new values provided
        if (!newName.isEmpty()) {
            staff.setName(newName);
        }
        if (!newGender.isEmpty()) {
            staff.setGender(newGender);
        }
        if (newAge != null) {
            if (staff instanceof Doctor) {
                ((Doctor) staff).setAge(newAge);
            } else if (staff instanceof Administrator) {
                ((Administrator) staff).setAge(newAge);
            } else if (staff instanceof Pharmacist) {
                ((Pharmacist) staff).setAge(newAge);
            }
        }

        try {
            data.rewriteStaff("../data/Staff_List.csv"); // Rewrite staff data to CSV
        } catch (IOException e) {
            System.out.println("Error updating staff: " + e.getMessage());
        }
        System.out.println("Staff updated successfully.");
    }

    private void deleteStaff(Scanner scanner) {
        System.out.print("Enter the ID of the staff to delete: ");
        String staffID = scanner.nextLine();

        User staff = findStaffById(staffID);
        if (staff == null) {
            System.out.println("Staff not found.");
            return;
        }

        if (staff instanceof Doctor) {
            data.getDoctors().remove(staff);
        } else if (staff instanceof Administrator) {
            data.getAdministrators().remove(staff);
        } else if (staff instanceof Pharmacist) {
            data.getPharmacists().remove(staff);
        }

        try {
            data.rewriteStaff("../data/Staff_List.csv"); // Rewrite staff data to CSV
        } catch (IOException e) {
            System.out.println("Error deleting staff: " + e.getMessage());
        }
        System.out.println("Staff deleted successfully.");
    }

    private User findStaffById(String id) {
        for (Doctor doctor : data.getDoctors()) {
            if (doctor.getUserID().equals(id)) {
                return doctor;
            }
        }
        for (Administrator admin : data.getAdministrators()) {
            if (admin.getUserID().equals(id)) {
                return admin;
            }
        }
        for (Pharmacist pharmacist : data.getPharmacists()) {
            if (pharmacist.getUserID().equals(id)) {
                return pharmacist;
            }
        }
        return null; // Not found
    }
}
