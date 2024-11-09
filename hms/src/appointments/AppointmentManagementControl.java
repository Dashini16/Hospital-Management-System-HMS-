package appointments;

import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataAppointments;
import filereaders.InitialDataMedicine;
import filereaders.InitialDataPatient;
import filereaders.InitialDataStaff;
import medicalrecords.OutcomeRecord;
import medicalrecords.Prescription;
import medicinemanagements.Medicine;
import users.Doctor;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


import authorization.AuthorizationControl;
import enums.AppointmentStatus;
import enums.PrescriptionStatus;

public class AppointmentManagementControl {
    private InitialDataAppointments initialDataAppointments;
    private InitialDataStaff initialDataStaff;
    private InitialDataMedicine initialDataMedicine;

    public AppointmentManagementControl(InitialDataMedicine initialDataMedicine,InitialDataStaff initialDataStaff, InitialDataAppointments initialDataAppointments) {
        this.initialDataAppointments = initialDataAppointments; // Link with InitialData class to access data
        this.initialDataStaff = initialDataStaff;
        this.initialDataMedicine = initialDataMedicine;

        initialDataAppointments.reloadData();
        initialDataMedicine.reloadData();
        initialDataStaff.reloadData();
    }

    public void viewAllAppointments(boolean showDetails) {
        initialDataAppointments.reloadData();
        int i = 1;

        for (Appointment a : initialDataAppointments.getLists()) {
            System.out.println("\n========== Appointment " + i + " ===========\n");
            System.out.println("Appointment ID: " + a.getAppointmentID());
            System.out.println("Patient ID: " + a.getPatientID());
            System.out.println("Doctor ID: " + a.getDoctorID());
            System.out.println("Date: " + a.getDate());
            System.out.println("Time: " + a.getTime());

            if (showDetails) {
                System.out.println(a.toString());
            }

            i++;
        }
    }

    public void viewAppointments(boolean isDoctor) {
        initialDataAppointments.reloadData();
        String userID = AuthorizationControl.getCurrentUserId();
        int i = 1;

        for (Appointment a : initialDataAppointments.getLists()) {
            boolean isAccepted = a.getStatus().equals(AppointmentStatus.ACCEPTED);
            boolean isCurrentUser = isDoctor ? a.getDoctorID().equals(userID) : a.getPatientID().equals(userID);

            if (isAccepted && isCurrentUser) {
                System.out.println("\n========== Appointment " + i + " ===========");
                System.out.println("Appointment ID: " + a.getAppointmentID());
                System.out.println("Patient ID: " + a.getPatientID());
                System.out.println("Doctor ID: " + a.getDoctorID());
                System.out.println("Date: " + a.getDate());
                System.out.println("Time: " + a.getTime());
                System.out.println("Status: " + a.getStatus());

                OutcomeRecord outcomeRecord = a.getOutcomeRecord();
                if (outcomeRecord != null) {
                    System.out.println("Outcome Record Date: " + outcomeRecord.getDateOfAppointment());
                    System.out.println("Service: " + outcomeRecord.getServiceType());

                    if (outcomeRecord.getPrescriptions() != null) {
                        System.out.println("Prescriptions: ");
                        for (Prescription p : outcomeRecord.getPrescriptions()) {
                            System.out.println(
                                    "  - Prescription: " + p.getMedicationName() + ", Status: " + p.getStatus());
                        }
                    }
                    System.out.println("Consultation Notes: " + outcomeRecord.getConsultationNotes());
                } else {
                    System.out.println("No outcome record found for this appointment.");
                }
                i++;
            }
        }

        if (i == 1) {
            System.out.println("No accepted appointments found for the current user.");
        }
    }

    public void viewOutcomeRecords(boolean showPastOnly) {
        initialDataAppointments.reloadData();
        int i = 1;

        for (Appointment a : initialDataAppointments.getLists()) {
            if (a.getOutcomeRecord() != null) {
                // If showPastOnly is true, filter for completed appointments
                if (showPastOnly && a.getStatus().equals(AppointmentStatus.COMPLETED)) {
                    System.out.println("========== Outcome Record " + i + " ===========");
                    System.out.println("Appointment ID: " + a.getAppointmentID());
                    System.out.println("Outcome Record: " + a.getOutcomeRecord().toString());
                    i++;
                } else if (!showPastOnly) {
                    // If showPastOnly is false, show all outcome records
                    System.out.println("========== Outcome Record " + i + " ===========");
                    System.out.println("Appointment ID: " + a.getAppointmentID());
                    System.out.println("Outcome Record: " + a.getOutcomeRecord().toString());
                    i++;
                }
            }
        }
    }


    public void cancelAppointment() {
        initialDataAppointments.reloadData();
        Scanner scanner = new Scanner(System.in);
        String currentPatientID = AuthorizationControl.getCurrentUserId();
        LocalDate today = LocalDate.now();
    
        // Filter appointments for the current user
        List<Appointment> cancelableAppointments = initialDataAppointments.getLists().stream()
                .filter(appointment -> appointment.getPatientID().equals(currentPatientID)
                        && ((appointment.getStatus() == AppointmentStatus.PENDING)
                            || (appointment.getStatus() == AppointmentStatus.ACCEPTED
                                && LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                        .isAfter(today.plusDays(3)))))
                .collect(Collectors.toList());
    
        if (cancelableAppointments.isEmpty()) {
            System.out.println("No appointments are eligible for cancellation.");
            return;
        }
    
        // Display eligible appointments for cancellation
        System.out.println("Appointments eligible for cancellation:");
        System.out.println("==============================================================");
        for (int i = 0; i < cancelableAppointments.size(); i++) {
            Appointment appointment = cancelableAppointments.get(i);
    
            // Find the doctor's name directly within the loop
            String doctorName = initialDataStaff.getDoctors().stream()
                    .filter(doc -> doc.getUserID().equals(appointment.getDoctorID()))
                    .map(Doctor::getName)
                    .findFirst()
                    .orElse("Unknown Doctor");
    
            System.out.printf("Option %d:\n", (i + 1));
            System.out.printf("   Appointment ID: %s\n", appointment.getAppointmentID());
            System.out.printf("   Date          : %s\n", appointment.getDate());
            System.out.printf("   Time          : %s\n", appointment.getTime());
            System.out.printf("   Doctor        : %s (ID: %s)\n", doctorName, appointment.getDoctorID());
            System.out.printf("   Status        : %s\n", appointment.getStatus());
            System.out.println("--------------------------------------------------------------");
        }
    
        // Prompt user to select an appointment to cancel by entering the option number
        int appointmentIndex = -1;
        while (appointmentIndex < 0 || appointmentIndex >= cancelableAppointments.size()) {
            System.out.print("Enter the number of the appointment to cancel: ");
            try {
                appointmentIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (appointmentIndex < 0 || appointmentIndex >= cancelableAppointments.size()) {
                    System.out.println("Invalid choice. Please select a valid number from the list.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    
        // Get the selected appointment for cancellation
        Appointment selectedAppointment = cancelableAppointments.get(appointmentIndex);
    
        // Confirm the cancellation
        System.out.print("Are you sure you want to cancel this appointment? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("yes")) {
            System.out.println("Cancellation aborted.");
            return;
        }
    
        // Cancel the appointment by updating its status to CANCELLED
        selectedAppointment.updateStatus(AppointmentStatus.CANCELLED);
    
        // Write updated appointment to file
        try {
            initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", selectedAppointment);
            System.out.println("Appointment canceled successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    // UPDATE APPOINTMENT STATUS
    public void acceptOrDeclineAppointment() {
        // PROMPT USER FOR APPOINTMENT ID
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Appointment ID to update status: ");
        String appointmentID = scanner.nextLine();

        // FIND IF APPOINTMENT EXISTS
        Appointment existingAppointment = null;
        try {
            existingAppointment = initialDataAppointments.findAppointment("hms/src/data/Appointments_List.csv",
                    appointmentID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (existingAppointment == null) {
            System.out.println("Appointment ID not found.");
            return;
        }
        // CHECK IF STATUS IS PENDING
        if (existingAppointment.getStatus() != AppointmentStatus.PENDING) {
            System.out.println("Appointment cannot be updated as it is not in a PENDING state.");
            return;
        }
        // DISPLAY APPOINTMENT DETAILS
        System.out.println("1. ACCEPT APPOINTMENT");
        System.out.println("2. CANCEL APPOINTMENT");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (choice == 1) {
            existingAppointment.updateStatus(AppointmentStatus.ACCEPTED);
        } else if (choice == 2) {
            existingAppointment.updateStatus(AppointmentStatus.CANCELLED);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        // WRITE UPDATED APPOINTMENT TO FILE
        try {
            initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", existingAppointment);
            System.out.println("Appointment status updated successfully.");
            initialDataAppointments.reloadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePrescriptionStatus() {
        // PROMPT USER FOR APPOINTMENT ID
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Appointment ID to update prescription status: ");
        String appointmentID = scanner.nextLine();

        // FIND IF APPOINTMENT EXISTS
        Appointment existingAppointment = null;
        try {
            existingAppointment = initialDataAppointments.findAppointment("hms/src/data/Appointments_List.csv",
                    appointmentID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (existingAppointment == null) {
            System.out.println("Appointment ID not found.");
            return;
        }

        // DISPLAY PRESCRIPTIONS
        List<Prescription> prescriptions = existingAppointment.getOutcomeRecord().getPrescriptions();
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions found for this appointment.");
            return;
        }

        System.out.println("Select a prescription to update its status:");
        for (int i = 0; i < prescriptions.size(); i++) {
            System.out.println((i + 1) + ". " + prescriptions.get(i).getMedicationName() + " - Current Status: "
                    + prescriptions.get(i).getStatus());
        }

        // PROMPT USER FOR PRESCRIPTION SELECTION
        System.out.print("Enter the number of the prescription to update: ");
        int prescriptionIndex = scanner.nextInt() - 1; // Adjust for zero-based index
        scanner.nextLine(); // Consume the newline character

        if (prescriptionIndex < 0 || prescriptionIndex >= prescriptions.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        // GET SELECTED PRESCRIPTION
        Prescription selectedPrescription = prescriptions.get(prescriptionIndex);
        String medicineName = selectedPrescription.getMedicationName();

        // CHECK INITIAL STOCK FROM initialData
        Medicine selectedMedicine = initialDataMedicine.getLists().stream()
                .filter(medicine -> medicine.getName().equals(medicineName))
                .findFirst()
                .orElse(null);

        if (selectedMedicine == null) {
            System.out.println("Medicine not found.");
            return;
        }

        // Check stock levels
        if (selectedMedicine.getInitialStock() < selectedMedicine.getLowStockLevelAlert()) {
            System.out.println("Cannot update the prescription status. Low stock level for " + medicineName
                    + ". Current stock: " + selectedMedicine.getInitialStock());

            // Ensure the prescription status remains PENDING
            selectedPrescription.updateStatus(PrescriptionStatus.PENDING);
            return; // Return if stock is low
        }

        // SUBTRACT 1 FROM INITIAL STOCK
        selectedMedicine.setInitialStock(selectedMedicine.getInitialStock() - 1);

        // UPDATE PRESCRIPTION STATUS TO ACCEPTED
        selectedPrescription.updateStatus(PrescriptionStatus.ACCEPTED);

        // WRITE UPDATED APPOINTMENT TO FILE
        try {
            initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", existingAppointment);
            System.out.println("Prescription status updated successfully.");

            // WRITE UPDATED MEDICINE LIST TO FILE
            initialDataMedicine.rewriteMedicines("hms/src/data/Medicine_List.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reload data if necessary
        initialDataMedicine.reloadData();
    }

    public void outcomeRecordUpdate() {
        Scanner scanner = new Scanner(System.in);

        // Prompt user for Appointment ID
        System.out.print("Enter the Appointment ID to update: ");
        String appointmentID = scanner.nextLine().trim();

        try {
            // Read the existing appointment
            Appointment existingAppointment = initialDataAppointments
                    .findAppointment("hms/src/data/Appointments_List.csv", appointmentID);

            // If appointment not found, notify the user and exit
            if (existingAppointment == null) {
                System.out.println("Appointment ID not found.");
                return;
            }

            // Prompt user for new outcome details
            System.out.print("Enter the date of appointment (dd/MM/yyyy): ");
            String dateOfAppointment = scanner.nextLine().trim();

            System.out.print("Enter the service type: ");
            String serviceType = scanner.nextLine().trim();

            System.out.print("Enter consultation notes: ");
            String consultationNotes = scanner.nextLine().trim();

            // Gather prescription details
            System.out.print("Enter the number of prescriptions: ");
            int numPrescriptions = Integer.parseInt(scanner.nextLine().trim());
            List<Prescription> prescriptions = new ArrayList<>();

            // Display all available medicines
            List<Medicine> medicines = initialDataMedicine.getLists(); // Assuming you have a method to get the list of
                                                                       // medicines
            System.out.println("Available Medicines:");
            for (int i = 0; i < medicines.size(); i++) {
                System.out.println((i + 1) + ". " + medicines.get(i).getName());
            }

            for (int i = 0; i < numPrescriptions; i++) {
                System.out.print("Enter the number corresponding to the medication for prescription " + (i + 1) + ": ");
                int medicineIndex = Integer.parseInt(scanner.nextLine().trim()) - 1; // Adjust for zero-based index

                if (medicineIndex < 0 || medicineIndex >= medicines.size()) {
                    System.out.println("Invalid selection. Please try again.");
                    i--; // Decrement the counter to repeat this iteration
                    continue;
                }

                // Get the selected medication
                String medicationName = medicines.get(medicineIndex).getName();
                Prescription prescription = new Prescription(medicationName); // Create prescription with medication
                                                                              // name
                prescription.updateStatus(PrescriptionStatus.PENDING); // Update status to PENDING
                prescriptions.add(prescription);
            }

            // Create the new OutcomeRecord
            OutcomeRecord outcomeRecord = new OutcomeRecord(dateOfAppointment, serviceType, consultationNotes);
            for (Prescription prescription : prescriptions) {
                outcomeRecord.addPrescription(prescription);
            }

            // Update the existing appointment with the new outcome record
            existingAppointment.setOutcomeRecord(outcomeRecord); // Assuming setOutcomeRecord method exists
            existingAppointment.updateStatus(AppointmentStatus.COMPLETED);

            // Write the updated appointment back to the file
            initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", existingAppointment);
            System.out.println("Outcome record updated successfully.");

            initialDataAppointments.reloadData();
        } catch (IOException e) {
            System.err.println("Error while updating the appointment: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
        }
    }


}
