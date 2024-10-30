package usermanagement;

import filereaders.InitialData;
import users.*;

import java.io.IOException;
import java.util.Scanner;

public class StaffManagementControl {
    private InitialData data;

    public StaffManagementControl(InitialData data) {
        this.data = data;
    }

    public void displayStaff() {
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
            String doctorID = scanner.nextLine();
            
            System.out.print("Enter doctor's name: ");
            String doctorName = scanner.nextLine();
            
            System.out.print("Enter doctor's gender: ");
            String doctorGender = scanner.nextLine();
            
            int doctorAge;
            while (true) {
                System.out.print("Enter doctor's age: ");
                try {
                    doctorAge = Integer.parseInt(scanner.nextLine());
                    if (doctorAge <= 0) {
                        System.out.println("Error: Age must be a positive integer.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for age.");
                }
            }
            
            System.out.print("Enter default password: ");
            String doctorPassword = scanner.nextLine();
            
            // Validate essential fields
            if (doctorID.isEmpty() || doctorName.isEmpty() || doctorGender.isEmpty() || doctorPassword.isEmpty()) {
                System.out.println("Error: All fields are required.");
                return;
            }
            
            Doctor newDoctor = new Doctor(doctorID, doctorName, doctorGender, doctorAge, doctorPassword);
            
            // Attempt to save the doctor record
            try {
                data.appendStaff("hms\\src\\data\\Staff_List.csv", newDoctor);
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
            String pharmacistID = scanner.nextLine();
    
            System.out.print("Enter pharmacist's name: ");
            String pharmacistName = scanner.nextLine();
    
            System.out.print("Enter pharmacist's gender: ");
            String pharmacistGender = scanner.nextLine();
    
            int pharmacistAge;
            while (true) {
                System.out.print("Enter pharmacist's age: ");
                try {
                    pharmacistAge = Integer.parseInt(scanner.nextLine());
                    if (pharmacistAge <= 0) {
                        System.out.println("Error: Age must be a positive integer.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for age.");
                }
            }
    
            System.out.print("Enter default password: ");
            String pharmacistPassword = scanner.nextLine();
    
            // Validate essential fields
            if (pharmacistID.isEmpty() || pharmacistName.isEmpty() || pharmacistGender.isEmpty() || pharmacistPassword.isEmpty()) {
                System.out.println("Error: All fields are required.");
                return;
            }
    
            Pharmacist newPharmacist = new Pharmacist(pharmacistID, pharmacistName, pharmacistGender, pharmacistAge, pharmacistPassword);
    
            try {
                data.appendStaff("hms\\src\\data\\Staff_List.csv", newPharmacist);
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
            String adminID = scanner.nextLine();
    
            System.out.print("Enter administrator's name: ");
            String adminName = scanner.nextLine();
    
            System.out.print("Enter administrator's gender: ");
            String adminGender = scanner.nextLine();
    
            int adminAge;
            while (true) {
                System.out.print("Enter administrator's age: ");
                try {
                    adminAge = Integer.parseInt(scanner.nextLine());
                    if (adminAge <= 0) {
                        System.out.println("Error: Age must be a positive integer.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid integer for age.");
                }
            }
    
            System.out.print("Enter default password: ");
            String adminPassword = scanner.nextLine();
    
            // Validate essential fields
            if (adminID.isEmpty() || adminName.isEmpty() || adminGender.isEmpty() || adminPassword.isEmpty()) {
                System.out.println("Error: All fields are required.");
                return;
            }
    
            Administrator newAdmin = new Administrator(adminID, adminName, adminGender, adminAge, adminPassword);
    
            try {
                data.appendStaff("hms\\src\\data\\Staff_List.csv", newAdmin);
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
        System.out.print("Enter new gender (leave blank for no change): ");
        String newGender = scanner.nextLine();
    
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
            data.rewriteStaff("hms\\src\\data\\Staff_List.csv"); // Rewrite staff data to CSV
        } catch (IOException e) {
            System.out.println("Error updating staff: " + e.getMessage());
        }
        System.out.println("Staff updated successfully.");
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
        String staffID = scanner.nextLine();
    
        Users staff = findStaffById(staffID);
        if (staff == null) {
            System.out.println("Staff not found.");
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
    
        if (removed) {
            try {
                data.rewriteStaff("hms\\src\\data\\Staff_List.csv"); // Rewrite staff data to CSV
                System.out.println("Staff deleted successfully.");
            } catch (IOException e) {
                System.out.println("Error deleting staff: " + e.getMessage());
            }
        } 
        else
         {
            System.out.println("Error: Staff could not be removed from the list.");
        }
    }
    
    // Method to locate staff by ID within available lists
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

