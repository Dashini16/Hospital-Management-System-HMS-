package usermanagement;

import filereaders.InitialDataStaff;
import users.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import enums.Roles;

public class StaffManagementControl {
    private InitialDataStaff data;

    public StaffManagementControl(InitialDataStaff data) {
        this.data = data;
        data.reloadData();
    }

    public void displayStaff() {
        if (data.getDoctors().isEmpty() || data.getPharmacists().isEmpty() || data.getAdministrators().isEmpty()) {
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
            // Generate a new unique doctor ID in the format D001, D002, etc.
            String doctorID = generateUniqueDoctorID();
            System.out.println("Generated Doctor ID: " + doctorID);
    
            System.out.print("Enter doctor's name: ");
            String doctorName = scanner.nextLine().trim(); // Trim input
    
            // Validate name
            if (doctorName.isEmpty()) {
                System.out.println("Error: Doctor name cannot be empty.");
                return;
            }
    
            // Gender selection
            String doctorGender = "";
            while (true) {
                System.out.println("Select doctor's gender:");
                System.out.println("1. Male");
                System.out.println("2. Female");
                System.out.print("Enter the number corresponding to the gender: ");
                String genderChoice = scanner.nextLine().trim();
    
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
    
            // Age input and validation
            int doctorAge;
            while (true) {
                System.out.print("Enter doctor's age: ");
                try {
                    doctorAge = Integer.parseInt(scanner.nextLine().trim());
                    if (doctorAge <= 0) {
                        System.out.println("Error: Age must be a positive integer.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for age.");
                }
            }
    
            // Set default password and create a new doctor
            String doctorPassword = PasswordUtils.hashPassword("defaultPasswords");
            Doctor newDoctor = new Doctor(doctorID, doctorName, doctorGender, doctorAge, doctorPassword);
    
            // Append the doctor to CSV
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
    
    // Generates a unique doctor ID by finding the last used ID and incrementing it
    private String generateUniqueDoctorID() {
        List<Doctor> existingDoctors = data.getDoctors();
        if (existingDoctors.isEmpty()) {
            return "D001"; // Start with D001 if no doctors exist
        }
    
        // Find the maximum numeric part of the existing doctor IDs
        int maxId = existingDoctors.stream()
            .map(doctor -> Integer.parseInt(doctor.getUserID().substring(1))) // Remove the "D" prefix
            .max(Integer::compareTo)
            .orElse(0);
    
        // Increment the max ID and format it with leading zeros
        return String.format("D%03d", maxId + 1); // Ensures a 3-digit format with leading zeros
    }
    


    public void addPharmacist(Scanner scanner) {
        try {
            // Generate a new unique pharmacist ID in the format PH001, PH002, etc.
            String pharmacistID = generateUniquePharmacistID();
            System.out.println("Generated Pharmacist ID: " + pharmacistID);
    
            System.out.print("Enter pharmacist's name: ");
            String pharmacistName = scanner.nextLine().trim();
    
            // Validate name
            if (pharmacistName.isEmpty()) {
                System.out.println("Error: Pharmacist name cannot be empty.");
                return;
            }
    
            // Gender selection
            String pharmacistGender = "";
            while (true) {
                System.out.println("Select pharmacist's gender:");
                System.out.println("1. Male");
                System.out.println("2. Female");
                System.out.print("Enter the number corresponding to the gender: ");
                String genderChoice = scanner.nextLine().trim();
    
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
    
            // Age input and validation
            int pharmacistAge;
            while (true) {
                System.out.print("Enter pharmacist's age: ");
                try {
                    pharmacistAge = Integer.parseInt(scanner.nextLine().trim());
                    if (pharmacistAge <= 0) {
                        System.out.println("Error: Age must be a positive integer.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for age.");
                }
            }
    
            // Set default password and create a new pharmacist
            String pharmacistPassword = PasswordUtils.hashPassword("defaultPasswords");
            Pharmacist newPharmacist = new Pharmacist(pharmacistID, pharmacistName, pharmacistGender, pharmacistAge, pharmacistPassword);
    
            // Append the pharmacist to CSV
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
    
    // Generates a unique pharmacist ID by finding the last used ID and incrementing it
    private String generateUniquePharmacistID() {
        List<Pharmacist> existingPharmacists = data.getPharmacists();
        if (existingPharmacists.isEmpty()) {
            return "PH001"; // Start with PH001 if no pharmacists exist
        }
    
        // Find the maximum numeric part of the existing pharmacist IDs
        int maxId = existingPharmacists.stream()
            .map(pharmacist -> Integer.parseInt(pharmacist.getUserID().substring(2))) // Remove the "PH" prefix
            .max(Integer::compareTo)
            .orElse(0);
    
        // Increment the max ID and format it with leading zeros
        return String.format("PH%03d", maxId + 1); // Ensures a 3-digit format with leading zeros
    }
    

    public void addAdministrator(Scanner scanner) {
        try {
            // Generate or request unique Administrator ID
            String adminID = generateUniqueAdminID();
            System.out.println("Generated Administrator ID: " + adminID);

            System.out.print("Enter administrator's name (or type 'exit' to cancel): ");
            String adminName = scanner.nextLine().trim();
            if (adminName.equalsIgnoreCase("exit"))
                return;

            // Validate gender selection with exit option
            String adminGender = "";
            while (adminGender.isEmpty()) {
                System.out.println("Select administrator's gender:");
                System.out.println("1. Male");
                System.out.println("2. Female");
                System.out.print("Enter the number corresponding to the gender (or type 'exit' to cancel): ");
                String genderChoice = scanner.nextLine().trim();
                if (genderChoice.equalsIgnoreCase("exit"))
                    return;

                if (genderChoice.equals("1")) {
                    adminGender = "Male";
                } else if (genderChoice.equals("2")) {
                    adminGender = "Female";
                } else {
                    System.out.println("Error: Invalid choice. Please enter 1 or 2.");
                }
            }

            // Validate and retrieve administrator's age with exit option
            int adminAge = 0;
            while (adminAge <= 0) {
                System.out.print("Enter administrator's age (or type 'exit' to cancel): ");
                String ageInput = scanner.nextLine().trim();
                if (ageInput.equalsIgnoreCase("exit"))
                    return;

                try {
                    adminAge = Integer.parseInt(ageInput);
                    if (adminAge <= 0) {
                        System.out.println("Error: Age must be a positive integer.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for age.");
                }
            }

            String adminPassword = PasswordUtils.hashPassword("defaultPasswords"); // Default password for new admin

            // Create new Administrator and append data to Staff List CSV
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

    // Generates a unique Administrator ID
    private String generateUniqueAdminID() {
        List<Administrator> existingAdministrators = data.getAdministrators();
        // Find the maximum numeric part of the existing administrator IDs
        int maxId = existingAdministrators.stream()
                .map(admin -> Integer.parseInt(admin.getUserID().substring(1))) // Remove the "A" prefix
                .max(Integer::compareTo)
                .orElse(0);

        // Increment the max ID and format it with leading zeros
        return String.format("A%03d", maxId + 1);
    }

    // public void updateStaff(Scanner scanner) {
    // System.out.print("Enter the ID of the staff to update: ");
    // String staffID = scanner.nextLine();

    // Users staff = findStaffById(staffID);
    // if (staff == null) {
    // System.out.println("Staff not found.");
    // return;
    // }

    // System.out.print("Enter new name (leave blank for no change): ");
    // String newName = scanner.nextLine();

    // String newGender = "";
    // while (true) {
    // System.out.println("Select new gender (leave blank for no change):");
    // System.out.println("1. Male");
    // System.out.println("2. Female");
    // System.out.print("Enter the number corresponding to the gender: ");
    // String genderChoice = scanner.nextLine();

    // if (genderChoice.isEmpty()) {
    // break; // No change in gender
    // } else if (genderChoice.equals("1")) {
    // newGender = "Male";
    // break;
    // } else if (genderChoice.equals("2")) {
    // newGender = "Female";
    // break;
    // } else {
    // System.out.println("Error: Invalid choice. Please enter 1 or 2.");
    // }
    // }

    // System.out.print("Enter new age (leave blank for no change): ");
    // String newAgeInput = scanner.nextLine();
    // Integer newAge = null;

    // if (!newAgeInput.isEmpty()) {
    // try {
    // newAge = Integer.parseInt(newAgeInput);
    // if (newAge <= 0) {
    // System.out.println("Error: Age must be a positive integer.");
    // return;
    // }
    // } catch (NumberFormatException e) {
    // System.out.println("Error: Please enter a valid integer for age.");
    // return;
    // }
    // }

    // // Update fields if new values provided
    // if (!newName.isEmpty()) {
    // staff.setName(newName);
    // }
    // if (!newGender.isEmpty()) {
    // staff.setGender(newGender);
    // }
    // if (newAge != null) {
    // updateStaffAge(staff, newAge);
    // }

    // try {
    // data.rewriteStaff("hms/src/data/Staff_List.csv"); // Rewrite staff data to
    // CSV
    // System.out.println("Staff updated successfully.");
    // } catch (IOException e) {
    // System.out.println("Error updating staff: " + e.getMessage());
    // }
    // }
    
    public void updateStaff(Scanner scanner) {
        // Retrieve and sort the list of all staff members by name
        List<Users> staffList = data.getStaffList();
        staffList.sort(Comparator.comparing(Users::getName));

        // Display the sorted staff list
        System.out.println("\n===== Staff List =====");
        for (int i = 0; i < staffList.size(); i++) {
            Users staff = staffList.get(i);
            System.out.printf("%d. ID: %s, Name: %s%n", i + 1, staff.getUserID(), staff.getName());
        }
        System.out.println("======================");

        // Prompt for staff selection
        System.out.print("Enter the number corresponding to the staff you wish to update: ");
        int staffIndex;
        try {
            staffIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (staffIndex < 0 || staffIndex >= staffList.size()) {
                System.out.println("Invalid selection. Please select a valid number from the list.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }

        // Get the selected staff member
        Users staff = staffList.get(staffIndex);

        // Display the staff member's details (excluding the password)
        System.out.println("\n===== Selected Staff Details =====");
        System.out.println("ID      : " + staff.getUserID());
        System.out.println("Name    : " + staff.getName());
        System.out.println("Role    : " + staff.getRole());
        System.out.println("Gender  : " + staff.getGender());
        System.out.println("Age     : " + (staff instanceof Doctor ? ((Doctor) staff).getAge()
                : staff instanceof Administrator ? ((Administrator) staff).getAge()
                        : staff instanceof Pharmacist ? ((Pharmacist) staff).getAge() : "N/A"));
        System.out.println("===================================");

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
        // Display a list of all staff members with their names and IDs
        System.out.println("\n===== Staff List =====");
        List<Users> staffList = data.getStaffList();
        staffList.sort(Comparator.comparing(Users::getName)); // Sort staff by name

        for (int i = 0; i < staffList.size(); i++) {
            Users staff = staffList.get(i);
            System.out.printf("%d. ID: %s, Name: %s%n", i + 1, staff.getUserID(), staff.getName());
        }
        System.out.println("======================");

        // Prompt user to select a staff member by list number
        System.out.print("\nEnter the number corresponding to the staff you wish to delete: ");
        int staffIndex;
        try {
            staffIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (staffIndex < 0 || staffIndex >= staffList.size()) {
                System.out.println("Invalid selection. Please select a valid number from the list.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }

        // Get the selected staff member for deletion
        Users staff = staffList.get(staffIndex);

        // Confirm deletion
        System.out.print(
                "Are you sure you want to delete " + staff.getName() + " (ID: " + staff.getUserID() + ")? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("yes")) {
            System.out.println("Deletion aborted.");
            return;
        }

        // Remove the staff from their respective list
        boolean removed = false;
        if (staff instanceof Doctor) {
            removed = data.getDoctors().remove(staff);
        } else if (staff instanceof Administrator) {
            removed = data.getAdministrators().remove(staff);
        } else if (staff instanceof Pharmacist) {
            removed = data.getPharmacists().remove(staff);
        }

        // Rewrite the staff data to file if deletion was successful
        if (removed) {
            try {
                data.rewriteStaff("hms/src/data/Staff_List.csv");
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
