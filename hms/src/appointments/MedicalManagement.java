package appointments;

import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataAppointments;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import appointmentslots.AppointmentSlot;
import authorization.AuthorizationControl;
import enums.WorkingDay;
import enums.AppointmentStatus;

public class MedicalManagement {

    // private InitialDataStaff initialData;
    private InitialDataMedicalRecord medicalData;
    private InitialDataAppointmentSlots appointmentSlotsData;
    private InitialDataPatient patientData; // import patient data
    private InitialDataAppointments appointmentData;

    public MedicalManagement() {
        // initialData = new InitialDataStaff(); // Initialize InitialData instance
        medicalData = new InitialDataMedicalRecord();
        medicalData.reloadData();

        //
        patientData = new InitialDataPatient();
        patientData.reloadData();

        appointmentData = new InitialDataAppointments();
        appointmentData.reloadData();
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
                    String bloodType = matchingPatient.getBloodType();

                    // display all the medical record
                    System.out.println("### Medical Record View for Patient ID: " + record.getPatientID());

                    // display patient personal info
                    System.out.println("\nName: " + name);
                    System.out.println("Date of Birth: " + dob);
                    System.out.println("Gender: " + gender);
                    System.out.println("Contact Info: " + contactInfo);
                    System.out.println("Blood Type: " + bloodType);

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

    // new function to view only own patient's medical records
    public void viewOwnPatientMedicalRecords() {
        // Ensure data is loaded once for each relevant data source
        if (medicalData.getLists().isEmpty()) {
            medicalData.importData();
        }
        if (appointmentData.getLists().isEmpty()) {
            appointmentData.importData();
        }

        // Identify the current doctor
        String doctorID = AuthorizationControl.getCurrentUserId();

        // Step 1: Retrieve the list of unique patient IDs with past appointments
        // (ACCEPTED or COMPLETED) with this doctor
        List<String> patientIDs = appointmentData.getLists().stream()
                .filter(appointment -> appointment.getDoctorID().equals(doctorID) &&
                        (appointment.getStatus() == AppointmentStatus.ACCEPTED ||
                                appointment.getStatus() == AppointmentStatus.COMPLETED))
                .map(Appointment::getPatientID)
                .distinct()
                .collect(Collectors.toList());

        if (patientIDs.isEmpty()) {
            System.out.println("No patients with completed or accepted appointments found.");
            return;
        }

        // Step 2: Group the medical records by patient ID, ensuring unique records
        Map<String, List<MedicalRecord>> groupedRecords = medicalData.getLists().stream()
                .filter(record -> patientIDs.contains(record.getPatientID()))
                .collect(Collectors.groupingBy(MedicalRecord::getPatientID));

        if (groupedRecords.isEmpty()) {
            System.out.println("No medical records found for your patients.");
            return;
        }

        // Step 3: Display the medical records grouped by patient
        System.out.println("Viewing Medical Records for Your Patients:");
        System.out.println("===========================================");

        // Step 4: Iterate over each patient’s records and display them in a clear
        // format
        for (String patientID : groupedRecords.keySet()) {
            // Retrieve the patient's basic information
            Patient patient = patientData.getLists().stream()
                    .filter(p -> p.getUserID().equals(patientID))
                    .findFirst()
                    .orElse(null);

            if (patient != null) {
                // Display patient header
                System.out.println("------ Patient ID: " + patient.getUserID() + " ------");
                System.out.println("Name           : " + patient.getName());
                System.out.println("Date of Birth  : " + patient.getDateOfBirth());
                System.out.println("Gender         : " + patient.getGender());
                System.out.println("Contact Info   : " + patient.getContactInfo());

                // Display the medical records for this patient
                List<MedicalRecord> records = groupedRecords.get(patientID);
                System.out.println("\nMedical Records:");
                for (int i = 0; i < records.size(); i++) {
                    MedicalRecord record = records.get(i);
                    System.out.println("Record " + (i + 1) + ":");

                    // Display Diagnoses
                    System.out.println("  **Diagnoses:**");
                    if (record.getDiagnoses().isEmpty()) {
                        System.out.println("    No diagnoses recorded.");
                    } else {
                        for (int j = 0; j < record.getDiagnoses().size(); j++) {
                            System.out.println("    " + (j + 1) + ". " + record.getDiagnoses().get(j));
                        }
                    }

                    // Display Treatments
                    System.out.println("  **Treatments:**");
                    if (record.getTreatments().isEmpty()) {
                        System.out.println("    No treatments recorded.");
                    } else {
                        for (int k = 0; k < record.getTreatments().size(); k++) {
                            System.out.println("    " + (k + 1) + ". " + record.getTreatments().get(k));
                        }
                    }
                    System.out.println("-----------------------------------------------------");
                }
            } else {
                System.out.println("Patient information not found for ID: " + patientID);
            }
            System.out.println("=====================================================\n");
        }
    }

    // view everyone patients medical record
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

    // for doctor to update it own patients medical record
    public void updateMedicalRecord() {
        if (medicalData.getLists().isEmpty()) {
            medicalData.importData();
        }
        if (appointmentData.getLists().isEmpty()) {
            appointmentData.importData();
        }

        // Identify the current doctor
        String doctorID = AuthorizationControl.getCurrentUserId();

        // Retrieve unique patient IDs for the doctor’s accepted or completed
        // appointments
        List<String> patientIDs = appointmentData.getLists().stream()
                .filter(appointment -> appointment.getDoctorID().equals(doctorID) &&
                        (appointment.getStatus() == AppointmentStatus.ACCEPTED ||
                                appointment.getStatus() == AppointmentStatus.COMPLETED))
                .map(Appointment::getPatientID)
                .distinct()
                .collect(Collectors.toList());

        if (patientIDs.isEmpty()) {
            System.out.println("No patients available for updating medical records.");
            return;
        }

        // Display patients for selection
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select a patient to update medical record:");

        List<Patient> doctorPatients = patientData.getLists().stream()
                .filter(patient -> patientIDs.contains(patient.getUserID()))
                .collect(Collectors.toList());

        for (int i = 0; i < doctorPatients.size(); i++) {
            Patient patient = doctorPatients.get(i);
            System.out.printf("%d. Patient ID: %s, Name: %s\n", i + 1, patient.getUserID(), patient.getName());
        }

        // Prompt user to select a patient
        int patientChoice = -1;
        while (patientChoice < 1 || patientChoice > doctorPatients.size()) {
            System.out.print("Enter the number of the patient to update: ");
            try {
                patientChoice = Integer.parseInt(scanner.nextLine().trim());
                if (patientChoice < 1 || patientChoice > doctorPatients.size()) {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        String selectedPatientID = doctorPatients.get(patientChoice - 1).getUserID();

        // Find the medical record by Patient ID
        MedicalRecord medicalRecord = medicalData.getLists().stream()
                .filter(record -> record.getPatientID().equals(selectedPatientID))
                .findFirst()
                .orElse(null);

        if (medicalRecord == null) {
            System.out.println("Medical Record not found for Patient ID: " + selectedPatientID);
            return;
        }

        // Display the selected patient's medical record
        System.out.println("\n------ Patient ID: " + selectedPatientID + " ------");
        System.out.println("Name           : " + doctorPatients.get(patientChoice - 1).getName());
        System.out.println("Date of Birth  : " + doctorPatients.get(patientChoice - 1).getDateOfBirth());
        System.out.println("Gender         : " + doctorPatients.get(patientChoice - 1).getGender());
        System.out.println("Contact Info   : " + doctorPatients.get(patientChoice - 1).getContactInfo());

        System.out.println("\nMedical Records:");
        System.out.println("Record 1:");
        System.out.println("  **Diagnoses:**");
        for (int i = 0; i < medicalRecord.getDiagnoses().size(); i++) {
            System.out.printf("    %d. %s\n", i + 1, medicalRecord.getDiagnoses().get(i));
        }
        System.out.println("  **Treatments:**");
        for (int i = 0; i < medicalRecord.getTreatments().size(); i++) {
            System.out.printf("    %d. %s\n", i + 1, medicalRecord.getTreatments().get(i));
        }

        boolean continueUpdating = true;

        while (continueUpdating) {
            // Prompt for diagnoses or treatments update
            System.out.print("\nEnter 'd' to manage diagnoses, 't' to manage treatments, or 'exit' to finish: ");
            String choice = scanner.nextLine().trim();

            if (choice.equalsIgnoreCase("d")) {
                manageField(scanner, medicalRecord.getDiagnoses(), "Diagnosis");
            } else if (choice.equalsIgnoreCase("t")) {
                manageField(scanner, medicalRecord.getTreatments(), "Treatment");
            } else if (choice.equalsIgnoreCase("exit")) {
                continueUpdating = false;
            } else {
                System.out.println("Invalid input. Please enter 'd', 't', or 'exit'.");
            }
        }

        // Save updated medical records to CSV file
        try {
            // Convert each diagnosis and treatment list into a single String with `|`
            // separator
            List<MedicalRecord> allMedicalRecords = medicalData.getLists();
            for (MedicalRecord record : allMedicalRecords) {
                record.setDiagnoses(Arrays.asList(String.join("|", record.getDiagnoses()).split("\\|")));
                record.setTreatments(Arrays.asList(String.join("|", record.getTreatments()).split("\\|")));
            }
            medicalData.writeData("hms/src/data/Medical_Records.csv", allMedicalRecords);
            System.out.println("Medical Record updated and saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving Medical Record: " + e.getMessage());
        }
    }

    // Helper function to manage diagnoses or treatments
    private void manageField(Scanner scanner, List<String> fieldList, String fieldType) {
        boolean continueManaging = true;

        while (continueManaging) {
            System.out.printf("\nManaging %s\n", fieldType);
            System.out.println("Options: ");
            System.out.println("1. Add New " + fieldType);
            System.out.println("2. Update Existing " + fieldType);
            System.out.println("3. Delete Existing " + fieldType);
            System.out.println("4. Go Back");

            System.out.print("Enter your choice: ");
            int actionChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (actionChoice) {
                case 1:
                    // Add new item
                    System.out.print("Enter new " + fieldType + ": ");
                    String newItem = scanner.nextLine().trim();
                    if (!newItem.isEmpty()) {
                        fieldList.add(newItem);
                        System.out.println(fieldType + " added successfully.");
                    } else {
                        System.out.println(fieldType + " cannot be empty.");
                    }
                    break;
                case 2:
                    // Update existing item
                    System.out.printf("Select the %s to update (1 to %d): ", fieldType, fieldList.size());
                    int updateIndex = scanner.nextInt() - 1;
                    scanner.nextLine(); // Consume newline
                    if (updateIndex >= 0 && updateIndex < fieldList.size()) {
                        System.out.print("Enter updated " + fieldType + ": ");
                        String updatedItem = scanner.nextLine().trim();
                        if (!updatedItem.isEmpty()) {
                            fieldList.set(updateIndex, updatedItem);
                            System.out.println(fieldType + " updated successfully.");
                        } else {
                            System.out.println(fieldType + " cannot be empty.");
                        }
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                case 3:
                    // Delete existing item
                    System.out.printf("Select the %s to delete (1 to %d): ", fieldType, fieldList.size());
                    int deleteIndex = scanner.nextInt() - 1;
                    scanner.nextLine(); // Consume newline
                    if (deleteIndex >= 0 && deleteIndex < fieldList.size()) {
                        fieldList.remove(deleteIndex);
                        System.out.println(fieldType + " deleted successfully.");
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                case 4:
                    continueManaging = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

}
