package usermanagement;

import filereaders.*;
import users.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import authorization.AuthorizationControl;

public class PatientManagementControl {
    private InitialDataPatient data;

    public PatientManagementControl(InitialDataPatient data) {
        this.data = data;
        data.reloadData();
    }

    // Method to display all patients
    public void displayPatients() {
        try {
            List<Patient> patients = data.getLists();

            if (patients.isEmpty()) {
                System.out.println("No patients found.");
                return;
            }

            for (Patient patient : patients) {
                try {
                    patient.toStrings(); // Call the toStrings method for each patient
                } catch (Exception e) {
                    System.out.println("Error displaying patient information: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving patient list: " + e.getMessage());
        }
    }

    
    
    public void addPatient(Scanner scanner) {
        System.out.print("Enter patient's ID: ");
        String patientID = scanner.nextLine().trim();
    
        System.out.print("Enter patient's name: ");
        String patientName = scanner.nextLine().trim();
    
        LocalDate dateOfBirth = null;
        while (dateOfBirth == null) {
            System.out.print("Enter patient's date of birth (YYYY-MM-DD): ");
            String dobInput = scanner.nextLine().trim();
            try {
                dateOfBirth = LocalDate.parse(dobInput);
            } catch (Exception e) {
                System.out.println("Error: Please enter a valid date in the format YYYY-MM-DD.");
            }
        }
    
        System.out.println("Select patient's gender:");
        System.out.println("1. Male");
        System.out.println("2. Female");
        String patientGender = "";
        while (patientGender.isEmpty()) {
            System.out.print("Choose 1 or 2: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                patientGender = "Male";
            } else if (choice.equals("2")) {
                patientGender = "Female";
            } else {
                System.out.println("Error: Invalid choice. Please select 1 or 2.");
            }
        }
    
        System.out.print("Enter patient's blood type: ");
        String patientBloodType = scanner.nextLine().trim();
    
        System.out.print("Enter patient's contact information: ");
        String patientContactInfo = scanner.nextLine().trim();
    
        String patientPassword = "defaultPasswords";
    
        Patient newPatient = new Patient(patientID, patientName, dateOfBirth, patientGender, patientBloodType, patientContactInfo, patientPassword);
        try {
            data.appendData("hms\\src\\data\\Patient_List.csv", newPatient); // Append to CSV
            System.out.println("Patient added successfully: " + patientName);
        } catch (IOException e) {
            System.out.println("Error saving patient: " + e.getMessage());
        }
    }
    
    public void updatePatient(Scanner scanner) {
        //System.out.print("Enter the ID of the patient to update: ");
        //String patientID = scanner.nextLine().trim();
        String patientID = AuthorizationControl.getCurrentUserId();
    
        Patient patient = findPatientById(patientID);
        if (patient == null) {
            System.out.println("Error: Patient not found.");
            return;
        }
    
        System.out.print("Enter new name (leave blank for no change): ");
        String newName = scanner.nextLine().trim();
    
        LocalDate newDateOfBirth = null;
        while (newDateOfBirth == null) {
            System.out.print("Enter new date of birth (leave blank for no change, or enter YYYY-MM-DD): ");
            String newDateOfBirthInput = scanner.nextLine().trim();
            if (newDateOfBirthInput.isEmpty()) {
                break; // Leave date of birth unchanged
            }
            try {
                newDateOfBirth = LocalDate.parse(newDateOfBirthInput);
            } catch (Exception e) {
                System.out.println("Error: Please enter a valid date in the format YYYY-MM-DD.");
            }
        }
    
        System.out.print("Enter new contact info (leave blank for no change): ");
        String newContactInfo = scanner.nextLine().trim();
    
        // Update fields if new values provided
        if (!newName.isEmpty()) {
            patient.setName(newName);
        }
        if (newDateOfBirth != null) {
            patient.setDateOfBirth(newDateOfBirth);
        }
        if (!newContactInfo.isEmpty()) {
            patient.setContactInfo(newContactInfo);
        }
    
        try {
            data.rewritePatients("hms\\src\\data\\Patient_List.csv"); // Rewrite patient data to CSV
            System.out.println("Patient updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating patient: " + e.getMessage());
        }
    }
    
    public void deletePatient(Scanner scanner) {
        System.out.print("Enter the ID of the patient to delete: ");
        String patientID = scanner.nextLine().trim();
    
        Patient patient = findPatientById(patientID);
        if (patient == null) {
            System.out.println("Error: Patient not found.");
            return;
        }
    
        boolean removed = data.getLists().remove(patient);
        if (!removed) {
            System.out.println("Error: Could not remove patient from the list.");
            return;
        }
    
        try {
            data.rewritePatients("hms\\src\\data\\Patient_List.csv"); // Rewrite patient data to CSV
            System.out.println("Patient deleted successfully.");
        } catch (IOException e) {
            System.out.println("Error deleting patient: " + e.getMessage());
        }
    }
    

    // Helper method to find a patient by ID
    private Patient findPatientById(String id) {
        for (Patient patient : data.getLists()) {
            if (patient.getUserID().equals(id)) {
                return patient;
            }
        }
        return null; // Not found
    }
}
