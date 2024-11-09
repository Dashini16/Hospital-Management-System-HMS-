package usermanagement;

import filereaders.InitialDataStaff;
import users.*;

import java.io.IOException;
import java.util.Scanner;

public class StaffManagementControl {
    private InitialDataStaff data;

    public StaffManagementControl(InitialDataStaff data) {
        this.data = data;
        data.reloadData();
    }

    public void displayStaff() {
        if (data.getDoctors().isEmpty() || data.getPharmacists().isEmpty() || data.getAdministrators().isEmpty()) 
        {
            System.out.println("No staff members found.");
            return;
        }
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

    public void addDoctor(Scanner scanner) {
        try {
            System.out.print("Enter doctor's ID: ");
            String doctorID = scanner.nextLine().trim(); // Trim input

            Users staff = findStaffById(doctorID);
            // Check if ID exists
            if (staff != null) {
                System.out.println("Error: Doctor with ID " + doctorID + " already exists.");
                return;
            }
            
            // Validate ID
            if (doctorID.isEmpty()) {
                System.out.println("Error: Doctor ID cannot be empty.");
                return;
            }
    
            System.out.print("Enter doctor's name: ");
            String doctorName = scanner.nextLine().trim(); // Trim input
    
            // Validate name
            if (doctorName.isEmpty()) {
                System.out.println("Error: Doctor name cannot be empty.");
                return;
            }
    
            String doctorGender = "";

            while (true) {
                System.out.println("Select doctor's gender:");
                System.out.println("1. Male");
                System.out.println("2. Female");
                System.out.print("Enter the number corresponding to the gender: ");
                String genderChoice = scanner.nextLine().trim(); // Trim input
    
                if (genderChoice.equals("1")) {
                    doctorGender = "Male";
                    break;
                } else if (genderChoice.equals("2")) {
                    doctorGender = "Female";
                    break;
                } else {
                    System.out.println("Error: Invalid choice. Please enter 1 or 2.");
                }
            }
    
            int doctorAge;
            while (true) {
                System.out.print("Enter doctor's age: ");
                try {
                    doctorAge = Integer.parseInt(scanner.nextLine().trim()); // Trim input
                    if (doctorAge <= 0) {
                        System.out.println("Error: Age must be a positive integer.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for age.");
                }
            }
    
            String doctorPassword = PasswordUtils.hashPassword("defaultPasswords");
    
            Doctor newDoctor = new Doctor(doctorID, doctorName, doctorGender, doctorAge, doctorPassword);
            try {
                data.appendData("hms/src/data/Staff_List.csv", newDoctor);
                System.out.println("Doctor added successfully: " + doctorName);
            } catch (IOException e) {
                System.out.println("Error saving doctor: " + e.getMessage());
            }
    
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public void addPharmacist(Scanner scanner) {
        try {
            System.out.print("Enter pharmacist's ID: ");
            String pharmacistID = scanner.nextLine().trim(); // Trim input

            Users staff = findStaffById(pharmacistID);
            // Check if ID exists
            if (staff != null) {
                System.out.println("Error: Pharmacist with ID " + pharmacistID + " already exists.");
                return;
            }

            // Validate ID
            if (pharmacistID.isEmpty()) {
                System.out.println("Error: Pharmacist ID cannot be empty.");
                return;
            }
    
            System.out.print("Enter pharmacist's name: ");
            String pharmacistName = scanner.nextLine().trim(); // Trim input
    
            // Validate name
            if (pharmacistName.isEmpty()) {
                System.out.println("Error: Pharmacist name cannot be empty.");
                return;
            }
    
            String pharmacistGender = "";
            while (true) {
                System.out.println("Select pharmacist's gender:");
                System.out.println("1. Male");
                System.out.println("2. Female");
                System.out.print("Enter the number corresponding to the gender: ");
                String genderChoice = scanner.nextLine().trim(); // Trim input
    
                if (genderChoice.equals("1")) {
                    pharmacistGender = "Male";
                    break;
                } else if (genderChoice.equals("2")) {
                    pharmacistGender = "Female";
                    break;
                } else {
                    System.out.println("Error: Invalid choice. Please enter 1 or 2.");
                }
            }
    
            int pharmacistAge;
            while (true) {
                System.out.print("Enter pharmacist's age: ");
                try {
                    pharmacistAge = Integer.parseInt(scanner.nextLine().trim()); // Trim input
                    if (pharmacistAge <= 0) {
                        System.out.println("Error: Age must be a positive integer.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for age.");
                }
            }
    
            String pharmacistPassword = PasswordUtils.hashPassword("defaultPasswords"); // Set default password
    
            Pharmacist newPharmacist = new Pharmacist(pharmacistID, pharmacistName, pharmacistGender, pharmacistAge, pharmacistPassword);
            try {
                data.appendData("hms/src/data/Staff_List.csv", newPharmacist);
                System.out.println("Pharmacist added successfully: " + pharmacistName);
            } catch (IOException e) {
                System.out.println("Error saving pharmacist: " + e.getMessage());
            }
    
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public void addAdministrator(Scanner scanner) {
        try {
            System.out.print("Enter administrator's ID: ");
            String adminID = scanner.nextLine().trim(); // Trim input

            Users staff = findStaffById(adminID);
            // Check if ID exists
            if (staff != null) {
                System.out.println("Error: Administrator with ID " + adminID + " already exists.");
                return;
            }

            // Validate ID
            if (adminID.isEmpty()) {
                System.out.println("Error: Administrator ID cannot be empty.");
                return;
            }
    
            System.out.print("Enter administrator's name: ");
            String adminName = scanner.nextLine().trim(); // Trim input
    
            // Validate name
            if (adminName.isEmpty()) {
                System.out.println("Error: Administrator name cannot be empty.");
                return;
            }
    
            String adminGender = "";
            while (true) {
                System.out.println("Select administrator's gender:");
                System.out.println("1. Male");
                System.out.println("2. Female");
                System.out.print("Enter the number corresponding to the gender: ");
                String genderChoice = scanner.nextLine().trim(); // Trim input
    
                if (genderChoice.equals("1")) {
                    adminGender = "Male";
                    break;
                } else if (genderChoice.equals("2")) {
                    adminGender = "Female";
                    break;
                } else {
                    System.out.println("Error: Invalid choice. Please enter 1 or 2.");
                }
            }
    
            int adminAge;
            while (true) {
                System.out.print("Enter administrator's age: ");
                try {
                    adminAge = Integer.parseInt(scanner.nextLine().trim()); // Trim input
                    if (adminAge <= 0) {
                        System.out.println("Error: Age must be a positive integer.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for age.");
                }
            }
    
            String adminPassword = PasswordUtils.hashPassword("defaultPasswords"); // Set default password
    
            Administrator newAdmin = new Administrator(adminID, adminName, adminGender, adminAge, adminPassword);
            try {
                data.appendData("hms/src/data/Staff_List.csv", newAdmin);
                System.out.println("Administrator added successfully: " + adminName);
            } catch (IOException e) {
                System.out.println("Error saving administrator: " + e.getMessage());
            }
    
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    
    public void updateStaff(Scanner scanner) {
        System.out.print("Enter the ID of the staff to update: ");
        String staffID = scanner.nextLine();
    
        Users staff = findStaffById(staffID);
        if (staff == null) {
            System.out.println("Staff not found.");
            return;
        }
    
        System.out.print("Enter new name (leave blank for no change): ");
        String newName = scanner.nextLine();
    
        String newGender = "";
        while (true) {
            System.out.println("Select new gender (leave blank for no change):");
            System.out.println("1. Male");
            System.out.println("2. Female");
            System.out.print("Enter the number corresponding to the gender: ");
            String genderChoice = scanner.nextLine();
            
            if (genderChoice.isEmpty()) {
                break; // No change in gender
            } else if (genderChoice.equals("1")) {
                newGender = "Male";
                break;
            } else if (genderChoice.equals("2")) {
                newGender = "Female";
                break;
            } else {
                System.out.println("Error: Invalid choice. Please enter 1 or 2.");
            }
        }
    
        System.out.print("Enter new age (leave blank for no change): ");
        String newAgeInput = scanner.nextLine();
        Integer newAge = null;
    
        if (!newAgeInput.isEmpty()) {
            try {
                newAge = Integer.parseInt(newAgeInput);
                if (newAge <= 0) {
                    System.out.println("Error: Age must be a positive integer.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid integer for age.");
                return;
            }
        }
    
        // Update fields if new values provided
        if (!newName.isEmpty()) {
            staff.setName(newName);
        }
        if (!newGender.isEmpty()) {
            staff.setGender(newGender);
        }
        if (newAge != null) {
            updateStaffAge(staff, newAge);
        }
    
        try {
            data.rewriteStaff("hms/src/data/Staff_List.csv"); // Rewrite staff data to CSV
            System.out.println("Staff updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating staff: " + e.getMessage());
        }
    }
    
    
    // Helper method to update age based on staff type
    private void updateStaffAge(Users staff, int newAge) {
        if (staff instanceof Doctor) {
            ((Doctor) staff).setAge(newAge);
        } else if (staff instanceof Administrator) {
            ((Administrator) staff).setAge(newAge);
        } else if (staff instanceof Pharmacist) {
            ((Pharmacist) staff).setAge(newAge);
        }
    }

    public void deleteStaff(Scanner scanner) {
        System.out.print("Enter the ID of the staff to delete: ");
        String staffID = scanner.nextLine().trim();
    
        // Check if the staff ID input is empty
        if (staffID.isEmpty()) {
            System.out.println("Error: Staff ID cannot be empty.");
            return;
        }
    
        Users staff = findStaffById(staffID);
        if (staff == null) {
            System.out.println("Error: Staff not found with ID: " + staffID);
            return;
        }
    
        boolean removed = false;
        if (staff instanceof Doctor) {
            removed = data.getDoctors().remove(staff);
        } else if (staff instanceof Administrator) {
            removed = data.getAdministrators().remove(staff);
        } else if (staff instanceof Pharmacist) {
            removed = data.getPharmacists().remove(staff);
        }
    
        // Check if the staff was successfully removed
        if (removed) {
            try {
                data.rewriteStaff("hms/src/data/Staff_List.csv"); // Rewrite staff data to CSV
                System.out.println("Staff deleted successfully: " + staff.getName());
            } catch (IOException e) {
                System.out.println("Error deleting staff: " + e.getMessage());
            }
        } else {
            System.out.println("Error: Staff could not be removed from the list. Please try again.");
        }
    }
    
    private Users findStaffById(String id) {
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

