
package usermanagement;

import authorization.*;
import filereaders.*;
import lookups.UserLookup;
import medicinemanagements.Medicine;
import users.*;
import java.io.IOException;
import java.util.Scanner;
import usermanagement.*;

public class PasswordManagement {

    private InitialDataStaff data;
    private InitialDataPatient patientData;
    public PasswordManagement(InitialDataStaff data, InitialDataPatient patientData) {
        this.data = data;
        this.patientData = patientData;

        //data.reloadData();
        //patientData.reloadData();
        
    }

    // Main method to handle password changes for both patients and staff
    public void changePassword(Scanner scanner) {
        // Check for active user session
        String currentUserID = AuthorizationControl.getCurrentUserId();
        if (currentUserID == null) {
            System.out.println("No active user session found.");
            return;
        }
    
        // Identify the user (patient or staff) based on ID
        Users user = findUserById(currentUserID);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
    
        System.out.println("Current user ID: " + user.getUserID());
    
        String newPassword;
        // Keep prompting for a new password until it meets the security requirements
        while (true) {
            // Prompt for new password
            System.out.print("Enter new password: ");
            newPassword = scanner.nextLine();
    
            // Check if password meets security requirements
            if (!isPasswordValid(newPassword)) {
                System.out.println("Invalid password. Ensure it meets the following requirements:");
                System.out.println("- At least 8 characters long");
                System.out.println("- Contains at least one uppercase letter");
                System.out.println("- Contains at least one lowercase letter");
                System.out.println("- Contains at least one digit");
                System.out.println("- Contains at least one special character (!@#$%^&*())");
            } else {
                break; // Exit the loop if the password is valid
            }
        }
    
        // Hash the new password
        String newHashedPassword = PasswordUtils.hashPassword(newPassword);
    
        // Check if new password is the same as the current password
        if (user.getPassword().equals(newHashedPassword)) {
            System.out.println("New password cannot be the same as the current password.");
            return;
        }
    
        // Update password
        user.setPassword(newHashedPassword);
        try {
            // Save changes based on user type
            if (user instanceof Patient) {
                patientData.rewritePatients("hms/src/data/Patient_List.csv");
            } else {
                data.rewriteStaff("hms/src/data/Staff_List.csv");
            }
            System.out.println("Password changed successfully.");
        } catch (IOException e) {
            System.out.println("Error saving password change: " + e.getMessage());
        }
    }
    

    // Helper method to validate password strength
    private boolean isPasswordValid(String password) {
        return password.length() >= 8 &&
            password.matches(".*[A-Z].*") &&         // At least one uppercase letter
            password.matches(".*[a-z].*") &&         // At least one lowercase letter
            password.matches(".*[0-9].*") &&         // At least one digit
            password.matches(".*[!@#$%^&*()].*");    // At least one special character
    }

    // Unified method to find a user (either Patient or Staff) by ID
    private Users findUserById(String id) {
        // Check patients
        for (Patient patient : patientData.getLists()) {
            if (patient.getUserID().equals(id)) {
                return patient;
            }
        }

        // Check doctors, administrators, and pharmacists
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


 