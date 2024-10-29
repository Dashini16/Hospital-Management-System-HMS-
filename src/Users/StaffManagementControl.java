import java.io.IOException;
import java.util.Scanner;

public class StaffManagementControl {
    private InitialData data;

    public StaffManagementControl(InitialData data) {
        this.data = data;
    }

    protected void displayStaff() {
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

    protected void addDoctor(Scanner scanner) {
        System.out.print("Enter doctor's ID: ");
        String doctorID = scanner.nextLine();
        System.out.print("Enter doctor's name: ");
        String doctorName = scanner.nextLine();
        System.out.print("Enter doctor's gender: ");
        String doctorGender = scanner.nextLine();
        System.out.print("Enter doctor's age: ");
        int doctorAge = scanner.nextInt();

        scanner.nextLine();
        System.out.print("Enter default password:");
        String doctorPassword = scanner.nextLine();

        scanner.nextLine();

        Doctor newDoctor = new Doctor(doctorID, doctorName, doctorGender, doctorAge, doctorPassword);
        try {
            data.appendStaff("./data/Staff_List.csv", newDoctor); // Append to CSV
        } catch (IOException e) {
            System.out.println("Error saving doctor: " + e.getMessage());
        }
        System.out.println("Doctor added successfully: " + doctorName);
    }

    protected void addPharmacist(Scanner scanner) {
        System.out.print("Enter pharmacist's ID: ");
        String pharmacistID = scanner.nextLine();
        System.out.print("Enter pharmacist's name: ");
        String pharmacistName = scanner.nextLine();
        System.out.print("Enter pharmacist's gender: ");
        String pharmacistGender = scanner.nextLine();
        System.out.print("Enter pharmacist's age: ");
        int pharmacistAge = scanner.nextInt();

        scanner.nextLine();
        System.out.print("Enter default password:");
        String pharmacistPassword = scanner.nextLine();
        scanner.nextLine();

        Pharmacist newPharmacist = new Pharmacist(pharmacistID, pharmacistName, pharmacistGender, pharmacistAge, pharmacistPassword);
        try {
            data.appendStaff("./data/Staff_List.csv", newPharmacist); // Append to CSV
        } catch (IOException e) {
            System.out.println("Error saving pharmacist: " + e.getMessage());
        }
        System.out.println("Pharmacist added successfully: " + pharmacistName);
    }

    protected void addAdministrator(Scanner scanner) {
        System.out.print("Enter administrator's ID: ");
        String adminID = scanner.nextLine();
        System.out.print("Enter administrator's name: ");
        String adminName = scanner.nextLine();
        System.out.print("Enter administrator's gender: ");
        String adminGender = scanner.nextLine();
        System.out.print("Enter administrator's age: ");
        int adminAge = scanner.nextInt();
        System.out.print("Enter default password:");
        String adminPassword = scanner.nextLine();

        scanner.nextLine();

        Administrator newAdmin = new Administrator(adminID, adminName, adminGender, adminAge, adminPassword);
        try {
            data.appendStaff("./data/Staff_List.csv", newAdmin); // Append to CSV
        } catch (IOException e) {
            System.out.println("Error saving administrator: " + e.getMessage());
        }
        System.out.println("Administrator added successfully: " + adminName);
    }

    protected void updateStaff(Scanner scanner) {
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
            data.rewriteStaff("./data/Staff_List.csv"); // Rewrite staff data to CSV
        } catch (IOException e) {
            System.out.println("Error updating staff: " + e.getMessage());
        }
        System.out.println("Staff updated successfully.");
    }

    protected void deleteStaff(Scanner scanner) {
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
            data.rewriteStaff("./data/Staff_List.csv"); // Rewrite staff data to CSV
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
