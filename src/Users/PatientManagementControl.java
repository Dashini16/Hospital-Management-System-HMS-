import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

public class PatientManagementControl {
    private InitialData data;

    public PatientManagementControl(InitialData data) {
        this.data = data;
    }

    // Method to display all patients
    protected void displayPatients() {
        for (Patient patient : data.getPatients()) {
            patient.toStrings();
        }
    }

    // Method to add a new patient
    protected void addPatient(Scanner scanner) {
        System.out.print("Enter patient's ID: ");
        String patientID = scanner.nextLine();
        System.out.print("Enter patient's name: ");
        String patientName = scanner.nextLine();
        System.out.print("Enter patient's date of birth (YYYY-MM-DD): ");
        LocalDate dateOfBirth = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter patient's gender: ");
        String patientGender = scanner.nextLine();
        System.out.print("Enter patient's blood type: ");
        String patientBloodType = scanner.nextLine();
        System.out.print("Enter patient's contact information: ");
        String patientContactInfo = scanner.nextLine();
        System.out.print("Enter default password: ");
        String patientPassword = scanner.nextLine();

        Patient newPatient = new Patient(patientID, patientName, dateOfBirth, patientGender, patientBloodType, patientContactInfo, patientPassword);
        try {
            data.appendPatient("./data/Patient_List.csv", newPatient); // Append to CSV
        } catch (IOException e) {
            System.out.println("Error saving patient: " + e.getMessage());
        }
        System.out.println("Patient added successfully: " + patientName);
    }

    // Method to update patient information
    protected void updatePatient(Scanner scanner) {
        System.out.print("Enter the ID of the patient to update: ");
        String patientID = scanner.nextLine();

        Patient patient = findPatientById(patientID);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        System.out.print("Enter new name (leave blank for no change): ");
        String newName = scanner.nextLine();
        System.out.print("Enter new date of birth (leave blank for no change): ");
        String newDateOfBirthInput = scanner.nextLine();
        LocalDate newDateOfBirth = newDateOfBirthInput.isEmpty() ? null : LocalDate.parse(newDateOfBirthInput);
        System.out.print("Enter new gender (leave blank for no change): ");
        String newGender = scanner.nextLine();
        System.out.print("Enter new blood type (leave blank for no change): ");
        String newBloodType = scanner.nextLine();
        System.out.print("Enter new contact info (leave blank for no change): ");
        String newContactInfo = scanner.nextLine();

        // Update fields if new values provided
        if (!newName.isEmpty()) {
            patient.setName(newName);
        }
        if (newDateOfBirth != null) {
            patient.setDateOfBirth(newDateOfBirth);
        }
        if (!newGender.isEmpty()) {
            patient.setGender(newGender);
        }
        if (!newBloodType.isEmpty()) {
            patient.setBloodType(newBloodType);
        }
        if (!newContactInfo.isEmpty()) {
            patient.setContactInfo(newContactInfo);
        }

        try {
            data.rewritePatients("./data/Patient_List.csv"); // Rewrite patient data to CSV
        } catch (IOException e) {
            System.out.println("Error updating patient: " + e.getMessage());
        }
        System.out.println("Patient updated successfully.");
    }

    // Method to delete a patient
    protected void deletePatient(Scanner scanner) {
        System.out.print("Enter the ID of the patient to delete: ");
        String patientID = scanner.nextLine();

        Patient patient = findPatientById(patientID);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        data.getPatients().remove(patient);

        try {
            data.rewritePatients("./data/Patient_List.csv"); // Rewrite patient data to CSV
        } catch (IOException e) {
            System.out.println("Error deleting patient: " + e.getMessage());
        }
        System.out.println("Patient deleted successfully.");
    }

    // Helper method to find a patient by ID
    private Patient findPatientById(String id) {
        for (Patient patient : data.getPatients()) {
            if (patient.getUserID().equals(id)) {
                return patient;
            }
        }
        return null; // Not found
    }
}
