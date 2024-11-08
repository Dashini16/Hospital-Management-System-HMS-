package appointments;

import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataMedicalRecord;
import filereaders.InitialDataPatient;
import lookups.UserLookup;
import medicalrecords.MedicalRecord;
import users.Patient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import appointmentslots.AppointmentSlot;
import authorization.AuthorizationControl;
import enums.WorkingDay;

public class MedicalManagement {

    // private InitialDataStaff initialData;
    private InitialDataMedicalRecord medicalData;
    private InitialDataAppointmentSlots appointmentSlotsData;
    private InitialDataPatient patientData; // import patient data

    public MedicalManagement() {
        // initialData = new InitialDataStaff(); // Initialize InitialData instance
        medicalData = new InitialDataMedicalRecord();
        medicalData.reloadData();

        //
        patientData = new InitialDataPatient();
        patientData.reloadData();
        //
    }

    public void createMedicalRecord() {
        medicalData.importData();

        // Prompt user for patient ID with validation
        Scanner scanner = new Scanner(System.in);

        // Collect patient ID with validation
        String patientID = "";
        while (true) {
            patientID = AuthorizationControl.getCurrentUserId();

            if (patientID.isEmpty()) {
                System.out.println("Patient ID cannot be empty. Please try again.");
            } else {
                break; // Valid ID provided, exit the loop
            }
        }
        System.out.println("Patient ID: " + patientID);
        // Check if a medical record already exists for the patient
        // MedicalRecord existingRecord = findPatientById(patientID);
        UserLookup userLookup = new UserLookup();
        final String findPatient = patientID;
        MedicalRecord existingRecord = userLookup.findByID(findPatient, medicalData.getLists(),
                rec -> rec.getPatientID().equals(findPatient));
        if (existingRecord != null) {
            System.out.println("Error: A medical record already exists for patient ID " + patientID
                    + ". You cannot add another record.");
            return; // Exit the method if a record already exists
        }

        // Create a new MedicalRecord object
        MedicalRecord record = new MedicalRecord(patientID);

        // Collect diagnoses with validation
        System.out.println("Enter Diagnosis (or type 'done' to finish): ");
        while (true) {
            String diagnosis = scanner.nextLine().trim();

            if (diagnosis.equalsIgnoreCase("done")) {
                break; // Exit the loop when done
            }

            // Validate diagnosis input
            if (diagnosis.isEmpty()) {
                System.out
                        .println("Diagnosis cannot be empty. Please enter a valid diagnosis or type 'done' to finish.");
            } else {
                record.addDiagnosis(diagnosis); // Add diagnosis to record
            }
        }

        // Collect treatments with validation
        System.out.println("Enter Treatment (or type 'done' to finish): ");
        while (true) {
            String treatment = scanner.nextLine().trim();

            if (treatment.equalsIgnoreCase("done")) {
                break; // Exit the loop when done
            }

            // Validate treatment input
            if (treatment.isEmpty()) {
                System.out
                        .println("Treatment cannot be empty. Please enter a valid treatment or type 'done' to finish.");
            } else {
                record.addTreatment(treatment); // Add treatment to record
            }
        }

        // Save the medical record to the CSV file
        try {
            medicalData.appendData("hms/src/data/Medical_Records.csv", record);
            System.out.println("Medical Record created and saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving Medical Record: " + e.getMessage());
        }
    }

    public void setDoctorAvailability() {
        Scanner scanner = new Scanner(System.in);
        String doctorID;

        // Input for Doctor ID
        while (true) {
            doctorID = AuthorizationControl.getCurrentUserId();
            if (!doctorID.isEmpty()) {
                break; // Exit loop if input is valid
            }
            System.out.println("Error: Doctor ID cannot be empty.");
        }

        LocalTime startTime;
        LocalTime endTime;

        // Input for Start Time with validation
        while (true) {
            try {
                System.out.print("Enter Start Time (yyyy-MM-dd'T'HH:mm): ");
                startTime = LocalTime.parse(scanner.nextLine().trim());

                if (startTime.isBefore(LocalTime.now())) {
                    System.out.println("Error: Start time must be in the future.");
                    continue;
                }
                break; // Exit loop if input is valid
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid date/time format. Please use 'yyyy-MM-dd'T'HH:mm'.");
            }
        }

        // Input for End Time with validation
        while (true) {
            try {
                System.out.print("Enter End Time (yyyy-MM-dd'T'HH:mm): ");
                endTime = LocalTime.parse(scanner.nextLine().trim());

                if (endTime.isBefore(startTime)) {
                    System.out.println("Error: End time must be after start time.");
                    continue;
                }
                if (endTime.isBefore(LocalTime.now())) {
                    System.out.println("Error: End time must be in the future.");
                    continue;
                }
                break; // Exit loop if input is valid
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid date/time format. Please use 'yyyy-MM-dd'T'HH:mm'.");
            }
        }

        // Check for overlapping appointment slots
        // List<AppointmentSlot> existingSlots = appointmentSlotsData.getLists();
        // for (AppointmentSlot slot : existingSlots) {
        // if (slot.getDoctorID().equals(doctorID) &&
        // !(endTime.isBefore(slot.getStartTime()) ||
        // startTime.isAfter(slot.getEndTime()))) {
        // System.out.println("Error: The new time slot overlaps with an existing
        // appointment slot.");
        // return; // Exit method or prompt for new input
        // }
        // }

        List<AppointmentSlot> existingSlots = appointmentSlotsData.getLists();
        for (AppointmentSlot slot : existingSlots) {
            if (slot.getDoctorID().equals(doctorID) &&
                    !(endTime.isBefore(slot.getStartTime()) || startTime.isAfter(slot.getEndTime()))) {
                System.out.println("Error: The new time slot overlaps with an existing appointment slot.");
                return; // Exit method or prompt for new input
            }
        }

        // Prompt for working days with validation
        List<WorkingDay> workingDays = new ArrayList<>();
        while (true) {
            System.out.print("Enter working days (e.g., MONDAY,TUESDAY, etc.), separated by commas: ");
            String[] daysInput = scanner.nextLine().split(",");

            try {
                for (String day : daysInput) {
                    workingDays.add(WorkingDay.valueOf(day.trim().toUpperCase()));
                }
                break; // Exit loop if all inputs are valid
            } catch (IllegalArgumentException e) {
                System.out.println("Error: One or more entered days are invalid. Please enter valid working days.");
            }
        }

        // Create and save the appointment slot
        AppointmentSlot newSlot = new AppointmentSlot(doctorID, startTime, endTime, workingDays);
        appointmentSlotsData.getLists().add(newSlot);

        // Save to CSV after adding
        try {
            appointmentSlotsData.saveAppointmentSlots("hms/src/data/Appointment_Slots.csv");
            System.out.println("Appointment slot saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving appointment slots: " + e.getMessage());
        }
    }

    public void viewMedicalRecords() {
        // InitialDataStaff initialDataStaff = new InitialDataStaff();
        // medicalData.importData();

        // Access and print medical records
        List<MedicalRecord> records = medicalData.getLists();
        for (MedicalRecord record : records) {
            // Display only if te record matches the current userID
            if (record.getPatientID().equals(AuthorizationControl.getCurrentUserId())) {

                // Retrieve the corresponding patient details
                Patient matchingPatient = null;
                for (Patient patient : patientData.getLists()) { // access the list of patients
                    if (patient.getUserID().equals(record.getPatientID())) {
                        matchingPatient = patient;
                        break; // exit the loop once the patient is found
                    }
                }

                // if the patient record is found
                if (matchingPatient != null) {

                    // Extract all the detail from the patient object
                    String name = matchingPatient.getName();
                    LocalDate dob = matchingPatient.getDateOfBirth();
                    String gender = matchingPatient.getGender();
                    String contactInfo = matchingPatient.getContactInfo();

                    // display all the medical record
                    System.out.println("### Medical Record View for Patient ID: " + record.getPatientID());

                    // display patient personal info
                    System.out.println("\nName: " + name);
                    System.out.println("Date of Birth: " + dob);
                    System.out.println("Gender: " + gender);
                    System.out.println("Contact Info: " + contactInfo);

                    // display diagnoses
                    System.out.println("\n**Diagnoses:**");
                    int diagnosisCount = 1;
                    for (String diagnosis : record.getDiagnoses()) {
                        if (!diagnosis.isEmpty()) {
                            System.out.println(diagnosisCount++ + ". " + diagnosis.trim());
                        }
                    }

                    // diaply treatments
                    System.out.println("\n**Treatments:**");
                    int treatmentCount = 1;
                    for (String treatment : record.getTreatments()) {
                        if (!treatment.isEmpty()) {
                            System.out.println(treatmentCount++ + ". " + treatment.trim());
                        }
                    }
                }

                System.out.println("\n");

            }
            // System.out.println("Prescriptions: " + record.getPrescriptions().toString());
        }
    }

    public void viewallMedicalRecords() {
        // InitialDataStaff initialData = new InitialDataStaff();
        medicalData.importData();
        int count = 0;
        // Access and print medical records
        List<MedicalRecord> records = medicalData.getLists();
        for (MedicalRecord record : records) {
            System.out.println("==========Medical Record " + ++count + "===========");
            System.out.println("Patient ID: " + record.getPatientID());
            System.out.println("Diagnoses: " + record.getDiagnoses());
            System.out.println("Treatments: " + record.getTreatments());
            System.out.println("==================================");
            // System.out.println("Prescriptions: " + record.getPrescriptions().toString());
        }
    }

    public void updateMedicalRecord() {
        // medicalData.importData();

        // Prompt user for patient ID with validation
        Scanner scanner = new Scanner(System.in);
        String patientID = "";
        while (true) {
            System.out.print("Enter Patient ID: ");
            patientID = scanner.nextLine().trim();

            if (patientID.isEmpty()) {
                System.out.println("Patient ID cannot be empty. Please enter a valid Patient ID.");
            } else {
                break; // Valid ID provided, exit the loop
            }
        }

        // Find the medical record by ID
        // MedicalRecord medicalRecord = findPatientById(patientID);

        UserLookup userLookup = new UserLookup();
        final String findPatient = patientID;
        MedicalRecord medicalRecord = userLookup.findByID(findPatient, medicalData.getLists(),
                rec -> rec.getPatientID().equals(findPatient));
        if (medicalRecord == null) {
            System.out.println("Medical Record not found for Patient ID: " + patientID);
            return;
        }

        // Display update options to the user
        String field = "";
        while (true) {
            System.out.print("Enter 'd' for diagnosis or 't' for treatments to update: ");
            field = scanner.nextLine().trim();

            if (field.equalsIgnoreCase("d") || field.equalsIgnoreCase("t")) {
                break; // Valid field provided, exit the loop
            } else {
                System.out.println("Invalid input. Please enter 'd' for diagnosis or 't' for prescription.");
            }
        }

        // Update diagnoses or treatments
        if (field.equalsIgnoreCase("d")) {
            System.out.println("Updating diagnoses. Enter Diagnosis (or type 'done' to finish): ");
            while (true) {
                String diagnosis = scanner.nextLine().trim();
                if (diagnosis.equalsIgnoreCase("done")) {
                    break; // Exit the loop when done
                }
                if (diagnosis.isEmpty()) {
                    System.out.println(
                            "Diagnosis cannot be empty. Please enter a valid diagnosis or type 'done' to finish.");
                } else {
                    medicalRecord.addDiagnosis(diagnosis); // Add diagnosis to record
                }
            }
        } else if (field.equalsIgnoreCase("t")) {
            System.out.println("Updating treatments. Enter Treatment (or type 'done' to finish): ");
            while (true) {
                String treatment = scanner.nextLine().trim();
                if (treatment.equalsIgnoreCase("done")) {
                    break; // Exit the loop when done
                }
                if (treatment.isEmpty()) {
                    System.out.println(
                            "Treatment cannot be empty. Please enter a valid treatment or type 'done' to finish.");
                } else {
                    medicalRecord.addTreatment(treatment); // Add treatment to record
                }
            }
        }

        // Save updated medical records to CSV file
        try {
            // Get all medical records and overwrite the file
            List<MedicalRecord> allMedicalRecords = medicalData.getLists();
            medicalData.writeData("hms/src/data/Medical_Records.csv", allMedicalRecords);
            System.out.println("Medical Record updated and saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving Medical Record: " + e.getMessage());
        }
    }

    /*
     * private MedicalRecord findPatientById(String id) {
     * for (MedicalRecord medicalRecord : medicalData.getLists()) {
     * //System.out.println("Patient ID: " + medicalRecord);
     * if (medicalRecord.getPatientID().equals(id)) {
     * //System.out.println("Found Medical Record for Patient ID: " + id);
     * return medicalRecord;
     * }
     * }
     * return null; // Not found
     * }
     */
}
