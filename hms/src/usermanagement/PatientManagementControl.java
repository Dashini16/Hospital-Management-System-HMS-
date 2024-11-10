package usermanagement;

import filereaders.*;
import lookups.UserLookup;
import medicinemanagements.Medicine;
import users.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

            // Display headers with column alignment
            System.out.printf("%-15s %-20s %-10s %-15s %-20s%n", "Patient ID", "Name", "Gender", "Age",
                    "Contact Information");
            System.out.println("-------------------------------------------------------------------------------");

            // Display each patient's information in a structured format
            for (Patient patient : patients) {
                try {
                    System.out.printf("%-15s %-20s %-10s %-15d %-20s%n",
                            patient.getUserID(),
                            patient.getName(),
                            patient.getGender(),
                            patient.getAge(),
                            patient.getContactInfo()); // assuming getContactInfo() gives contact details
                } catch (Exception e) {
                    System.out.println("Error displaying patient information: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving patient list: " + e.getMessage());
        }
    }

    // public void addPatient(Scanner scanner) {
    // System.out.print("Enter patient's ID: ");
    // String patientID = scanner.nextLine().trim();

    // System.out.print("Enter patient's name: ");
    // String patientName = scanner.nextLine().trim();

    // LocalDate dateOfBirth = null;
    // while (dateOfBirth == null) {
    // System.out.print("Enter patient's date of birth (YYYY-MM-DD): ");
    // String dobInput = scanner.nextLine().trim();
    // try {
    // dateOfBirth = LocalDate.parse(dobInput);
    // } catch (Exception e) {
    // System.out.println("Error: Please enter a valid date in the format
    // YYYY-MM-DD.");
    // }
    // }

    // System.out.println("Select patient's gender:");
    // System.out.println("1. Male");
    // System.out.println("2. Female");
    // String patientGender = "";
    // while (patientGender.isEmpty()) {
    // System.out.print("Choose 1 or 2: ");
    // String choice = scanner.nextLine().trim();
    // if (choice.equals("1")) {
    // patientGender = "Male";
    // } else if (choice.equals("2")) {
    // patientGender = "Female";
    // } else {
    // System.out.println("Error: Invalid choice. Please select 1 or 2.");
    // }
    // }

    // System.out.print("Enter patient's blood type: ");
    // String patientBloodType = scanner.nextLine().trim();

    // System.out.print("Enter patient's contact information: ");
    // String patientContactInfo = scanner.nextLine().trim();

    // String patientPassword = PasswordUtils.hashPassword("defaultPasswords");

    // Patient newPatient = new Patient(patientID, patientName, dateOfBirth,
    // patientGender, patientBloodType, patientContactInfo, patientPassword);
    // try {
    // data.appendData("hms/src/data/Patient_List.csv", newPatient); // Append to
    // CSV
    // System.out.println("Patient added successfully: " + patientName);
    // } catch (IOException e) {
    // System.out.println("Error saving patient: " + e.getMessage());
    // }
    // }

    public void addPatient(Scanner scanner) {
        // Generate a new unique patient ID based on existing patients
        String patientID = generateUniquePatientID();
        System.out.println("Generated Patient ID: " + patientID);
    
        System.out.print("Enter patient's name (or type 'exit' to cancel): ");
        String patientName = scanner.nextLine().trim();
        if (patientName.equalsIgnoreCase("exit"))
            return;
    
        LocalDate dateOfBirth = null;
        while (dateOfBirth == null) {
            System.out.print("Enter patient's date of birth (YYYY-MM-DD) (or type 'exit' to cancel): ");
            String dobInput = scanner.nextLine().trim();
            if (dobInput.equalsIgnoreCase("exit"))
                return;
            try {
                dateOfBirth = LocalDate.parse(dobInput);
                if (dateOfBirth.isAfter(LocalDate.now())) {
                    System.out.println("Error: Date of birth cannot be in the future. Please enter a valid date.");
                    dateOfBirth = null; // Reset to null to prompt for re-entry
                }
            } catch (DateTimeParseException e) {
                System.out.println("Error: Please enter a valid date in the format YYYY-MM-DD.");
            }
        }
    
        System.out.println("Select patient's gender:");
        System.out.println("1. Male");
        System.out.println("2. Female");
        String patientGender = "";
        while (patientGender.isEmpty()) {
            System.out.print("Choose 1 or 2 (or type 'exit' to cancel): ");
            String choice = scanner.nextLine().trim();
            if (choice.equalsIgnoreCase("exit"))
                return;
            if (choice.equals("1")) {
                patientGender = "Male";
            } else if (choice.equals("2")) {
                patientGender = "Female";
            } else {
                System.out.println("Error: Invalid choice. Please select 1 or 2.");
            }
        }
    
        // Select blood type from a standardized list
        String[] bloodTypes = { "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-" };
        String patientBloodType = "";
        while (patientBloodType.isEmpty()) {
            System.out.println("Select patient's blood type:");
            for (int i = 0; i < bloodTypes.length; i++) {
                System.out.println((i + 1) + ". " + bloodTypes[i]);
            }
            System.out.print("Choose a number from the list above (or type 'exit' to cancel): ");
            String bloodChoiceInput = scanner.nextLine().trim();
            if (bloodChoiceInput.equalsIgnoreCase("exit"))
                return;
            try {
                int bloodChoice = Integer.parseInt(bloodChoiceInput);
                if (bloodChoice > 0 && bloodChoice <= bloodTypes.length) {
                    patientBloodType = bloodTypes[bloodChoice - 1];
                } else {
                    System.out.println("Error: Invalid choice. Please select a valid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    
        String patientContactInfo = "";
        while (patientContactInfo.isEmpty()) {
            System.out.print("Enter patient's contact information (email) (or type 'exit' to cancel): ");
            patientContactInfo = scanner.nextLine().trim();
            if (patientContactInfo.equalsIgnoreCase("exit"))
                return;
    
            // Validate email format
            if (!isValidEmail(patientContactInfo)) {
                System.out.println("Error: Invalid email format. Please enter a valid email address.");
                patientContactInfo = ""; // Reset to prompt for re-entry
                continue;
            }
    
            // Check for duplicate contact info
            if (isContactInfoDuplicate(patientContactInfo)) {
                System.out.println("Error: This contact information is already associated with another patient.");
                patientContactInfo = ""; // Reset to prompt for re-entry
            }
        }
    
        String patientPassword = PasswordUtils.hashPassword("defaultPasswords");
    
        Patient newPatient = new Patient(patientID, patientName, dateOfBirth, patientGender, patientBloodType,
                patientContactInfo, patientPassword);
        try {
            data.appendData("hms/src/data/Patient_List.csv", newPatient); // Append to CSV
            System.out.println("Patient added successfully: " + patientName);
        } catch (IOException e) {
            System.out.println("Error saving patient: " + e.getMessage());
        }
    }

    // helper method
    private boolean isContactInfoDuplicate(String contactInfo) {
        return data.getLists().stream()
                .anyMatch(patient -> patient.getContactInfo().equalsIgnoreCase(contactInfo));
    }

    // Generates a unique patient ID by finding the last used ID and incrementing it
    private String generateUniquePatientID() {
        List<Patient> existingPatients = data.getLists();
        if (existingPatients.isEmpty()) {
            return "P1001"; // Start with a default ID if no patients exist
        }

        // Find the maximum ID by sorting or parsing the numeric part of each ID
        int maxId = existingPatients.stream()
                .map(patient -> Integer.parseInt(patient.getUserID().substring(1))) // Remove the "P" prefix
                .max(Integer::compareTo)
                .orElse(1000);

        return "P" + (maxId + 1); // Increment the ID number and prefix with "P"
    }

    public void updatePatient(Scanner scanner) {
        // Get the current login user ID
        String patientID = AuthorizationControl.getCurrentUserId();
    
        // Find the patient using the UserLookup utility
        UserLookup userLookup = new UserLookup();
        Patient patient = userLookup.findByID(patientID, data.getLists(),
                med -> med.getUserID().equalsIgnoreCase(patientID));
        if (patient == null) {
            System.out.println("Error: Patient not found.");
            return;
        }
    
        // Display the menu for the user to choose which action they want to perform
        boolean updating = true;
        while (updating) { // Keep looping until the user exits this menu
            System.out.println("\nSelect the field you want to update:");
            System.out.println("1. Update Name");
            System.out.println("2. Update Date of Birth");
            System.out.println("3. Update Contact Info (Email)");
            System.out.println("4. Exit");
    
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim(); // Ask for user input
    
            switch (choice) {
                // Update name
                case "1":
                    System.out.print("Enter new name (leave blank for no change): ");
                    String newName = scanner.nextLine().trim();
                    if (!newName.isEmpty()) {
                        patient.setName(newName);
                        System.out.println("Name updated successfully.");
                    } else {
                        System.out.println("Name not changed.");
                    }
                    break;
    
                // Update date of birth
                case "2":
                    LocalDate newDateOfBirth = null;
                    while (newDateOfBirth == null) {
                        System.out.print("Enter new date of birth (leave blank for no change, or enter YYYY-MM-DD): ");
                        String newDateOfBirthInput = scanner.nextLine().trim();
                        if (newDateOfBirthInput.isEmpty()) {
                            break; // Leave date of birth unchanged
                        }
                        try {
                            newDateOfBirth = LocalDate.parse(newDateOfBirthInput);
                            patient.setDateOfBirth(newDateOfBirth);
                            System.out.println("Date of birth updated successfully.");
                        } catch (Exception e) {
                            System.out.println("Error: Please enter a valid date in the format YYYY-MM-DD.");
                        }
                    }
                    break;
    
                // Update contact info (email)
                case "3":
                    while (true) {
                        System.out.print("Enter new contact info (email, or type 'exit' to cancel): ");
                        String newContactInfo = scanner.nextLine().trim();
                        if (newContactInfo.equalsIgnoreCase("exit")) {
                            System.out.println("Email update canceled.");
                            break;
                        }
                        // Validate email format
                        if (isValidEmail(newContactInfo)) {
                            patient.setContactInfo(newContactInfo);
                            System.out.println("Contact info (email) updated successfully.");
                            break;
                        } else {
                            System.out.println("Invalid email format. Please enter a valid email address.");
                        }
                    }
                    break;
    
                // Exit the update menu
                case "4":
                    updating = false; // Exit the while loop
                    System.out.println("Exiting update info menu...");
                    break;
    
                // Default case for invalid choice
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
                    break;
            }
        }
    
        // Save the changes to the CSV
        try {
            data.rewritePatients("hms/src/data/Patient_List.csv"); // Rewrite patient data to CSV
            System.out.println("Patient updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating patient: " + e.getMessage());
        }
    }
    
    // Helper function to validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    

    public void deletePatient(Scanner scanner) {
        List<Patient> patients = data.getLists();

        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }

        // Display list of patients with numbering
        System.out.println("Select a patient to delete:");
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            System.out.printf("%d. ID: %s, Name: %s%n", i + 1, patient.getUserID(), patient.getName());
        }

        // Prompt the user to select a patient to delete
        System.out.print("Enter the number corresponding to the patient to delete (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit")) {
            System.out.println("Deletion process canceled.");
            return;
        }

        int patientIndex;
        try {
            patientIndex = Integer.parseInt(input) - 1; // Convert input to zero-based index
            if (patientIndex < 0 || patientIndex >= patients.size()) {
                System.out.println("Error: Invalid selection. Please enter a valid number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number.");
            return;
        }

        Patient patient = patients.get(patientIndex);

        // Confirm deletion
        System.out.printf("Are you sure you want to delete patient '%s' (ID: %s)? (yes/no): ",
                patient.getName(), patient.getUserID());
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("yes")) {
            System.out.println("Deletion canceled.");
            return;
        }

        // Remove patient and rewrite the CSV file
        boolean removed = patients.remove(patient);
        if (!removed) {
            System.out.println("Error: Could not remove patient from the list.");
            return;
        }

        try {
            data.rewritePatients("hms/src/data/Patient_List.csv"); // Rewrite patient data to CSV
            System.out.println("Patient deleted successfully.");
        } catch (IOException e) {
            System.out.println("Error deleting patient: " + e.getMessage());
        }
    }

}
