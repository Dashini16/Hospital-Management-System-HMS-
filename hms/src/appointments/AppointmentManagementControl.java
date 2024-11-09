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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


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
        // Ensure data is loaded
        initialDataAppointments.reloadData();
        
        // Get the current doctor ID
        String doctorID = AuthorizationControl.getCurrentUserId();
    
        // Retrieve all pending appointments for this doctor
        List<Appointment> pendingAppointments = initialDataAppointments.getLists().stream()
                .filter(app -> app.getDoctorID().equals(doctorID) && app.getStatus() == AppointmentStatus.PENDING)
                .collect(Collectors.toList());
    
        // Check if there are no pending appointments
        if (pendingAppointments.isEmpty()) {
            System.out.println("You have no pending appointments to update.");
            return;
        }
    
        // Display the list of pending appointments with a neater format
        System.out.println("Pending Appointments:");
        System.out.println("============================================");
        for (int i = 0; i < pendingAppointments.size(); i++) {
            Appointment appointment = pendingAppointments.get(i);
            System.out.printf("%d. Appointment Details:\n", i + 1);
            System.out.println("   Appointment ID : " + appointment.getAppointmentID());
            System.out.println("   Patient ID     : " + appointment.getPatientID());
            System.out.println("   Date           : " + appointment.getDate());
            System.out.println("   Time           : " + appointment.getTime());
            System.out.println("============================================");
        }
    
        // Prompt the doctor to select an appointment or exit
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of the appointment to update status (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
    
        // Exit if the doctor chooses to cancel
        if (input.equalsIgnoreCase("exit")) {
            System.out.println("Exiting update process.");
            return;
        }
    
        int appointmentIndex;
        try {
            appointmentIndex = Integer.parseInt(input) - 1;
            if (appointmentIndex < 0 || appointmentIndex >= pendingAppointments.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
    
        // Retrieve the selected appointment
        Appointment selectedAppointment = pendingAppointments.get(appointmentIndex);
    
        // Display options to update status with an exit option
        System.out.println("\nSelect an action:");
        System.out.println("1. ACCEPT APPOINTMENT");
        System.out.println("2. CANCEL APPOINTMENT");
        System.out.println("3. EXIT WITHOUT UPDATING");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
    
        // Handle the chosen action or exit
        if (choice == 1) {
            selectedAppointment.updateStatus(AppointmentStatus.ACCEPTED);
        } else if (choice == 2) {
            selectedAppointment.updateStatus(AppointmentStatus.CANCELLED);
        } else if (choice == 3) {
            System.out.println("Exiting without updating appointment status.");
            return;
        } else {
            System.out.println("Invalid choice.");
            return;
        }
    
        // Write the updated appointment status to the file
        try {
            initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", selectedAppointment);
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
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
        System.out.println("== Outcome Record Update ==");
        System.out.println("Would you like to update:");
        System.out.println("1. New Appointment (Accepted)");
        System.out.println("2. Old Appointment (Completed)");
        System.out.print("Enter your choice (1 or 2): ");
    
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice != 1 && choice != 2) {
                System.out.println("Invalid choice. Please enter 1 or 2.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter 1 or 2.");
            return;
        }
    
        Appointment existingAppointment = null;
        try {
            List<Appointment> availableAppointments;
    
            if (choice == 1) {
                availableAppointments = initialDataAppointments.getLists().stream()
                        .filter(app -> app.getStatus() == AppointmentStatus.ACCEPTED)
                        .collect(Collectors.toList());
            } else {
                availableAppointments = initialDataAppointments.getLists().stream()
                        .filter(app -> app.getStatus() == AppointmentStatus.COMPLETED)
                        .collect(Collectors.toList());
            }
    
            if (availableAppointments.isEmpty()) {
                System.out.println("\nNo appointments found with the selected status.\n");
                return;
            }
    
            System.out.println("\n== Select an Appointment to Update ==");
            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n", "No.", "Appointment ID", "Patient ID", "Date", "Status");
            for (int i = 0; i < availableAppointments.size(); i++) {
                Appointment appointment = availableAppointments.get(i);
                System.out.printf("%-5d %-20s %-15s %-15s %-15s%n",
                        i + 1,
                        appointment.getAppointmentID(),
                        appointment.getPatientID(),
                        appointment.getDate(),
                        appointment.getStatus());
            }
    
            System.out.print("\nEnter the number of the appointment to update (or type 'exit' to cancel): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting update process.");
                return;
            }
    
            int appointmentIndex;
            try {
                appointmentIndex = Integer.parseInt(input) - 1;
                if (appointmentIndex < 0 || appointmentIndex >= availableAppointments.size()) {
                    System.out.println("Invalid selection. Please try again.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                return;
            }
    
            existingAppointment = availableAppointments.get(appointmentIndex);
    
            OutcomeRecord outcomeRecord = existingAppointment.getOutcomeRecord();
            if (existingAppointment.getStatus() == AppointmentStatus.COMPLETED && outcomeRecord != null) {
                System.out.println("\n== Existing Outcome Record ==");
                System.out.println("Date of Appointment   : " + outcomeRecord.getDateOfAppointment());
                System.out.println("Service Type          : " + outcomeRecord.getServiceType());
                System.out.println("Consultation Notes    : " + outcomeRecord.getConsultationNotes());
                System.out.println("Prescriptions         : ");
                for (Prescription prescription : outcomeRecord.getPrescriptions()) {
                    System.out.println("  - " + prescription.getMedicationName() + " (" + prescription.getQuantity() +
                            ", Status: " + prescription.getStatus() + ")");
                }
                System.out.println("Prescriptions cannot be modified for completed appointments.");
            }
    
            System.out.println("\n== Update Details (Press Enter to keep current value) ==");
    
            // Date input loop
            String dateOfAppointment;
            while (true) {
                System.out.print("Enter the new date of appointment (dd/MM/yyyy): ");
                dateOfAppointment = scanner.nextLine().trim();
                if (dateOfAppointment.isEmpty()) break; // Keep current date if empty
                try {
                    LocalDate.parse(dateOfAppointment, dateFormatter);
                    break; // Exit loop if date is valid
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use dd/MM/yyyy format.");
                }
            }
            if (!dateOfAppointment.isEmpty()) {
                existingAppointment.getOutcomeRecord().setDateOfAppointment(dateOfAppointment);
            }
    
            System.out.print("Enter the new service type: ");
            String serviceType = scanner.nextLine().trim();
            if (!serviceType.isEmpty()) {
                existingAppointment.getOutcomeRecord().setServiceType(serviceType);
            }
    
            System.out.print("Enter updated consultation notes: ");
            String consultationNotes = scanner.nextLine().trim();
            if (!consultationNotes.isEmpty()) {
                existingAppointment.getOutcomeRecord().setConsultationNotes(consultationNotes);
            }
    
            // Prescription entry loop for accepted appointments
            if (existingAppointment.getStatus() == AppointmentStatus.ACCEPTED) {
                int numPrescriptions;
                while (true) {
                    System.out.print("Enter the number of prescriptions: ");
                    try {
                        numPrescriptions = Integer.parseInt(scanner.nextLine().trim());
                        break; // Exit if input is valid
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input for number of prescriptions. Please enter a valid number.");
                    }
                }
    
                List<Prescription> prescriptions = new ArrayList<>();
                List<Medicine> medicines = initialDataMedicine.getLists();
                System.out.println("\n== Available Medicines ==");
                for (int i = 0; i < medicines.size(); i++) {
                    System.out.printf("%d. %s%n", i + 1, medicines.get(i).getName());
                }
    
                for (int i = 0; i < numPrescriptions; i++) {
                    int medicineIndex;
                    while (true) {
                        System.out.printf("Select medicine for prescription %d: ", i + 1);
                        try {
                            medicineIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                            if (medicineIndex >= 0 && medicineIndex < medicines.size()) {
                                break; // Valid selection
                            } else {
                                System.out.println("Invalid selection. Please try again.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid number.");
                        }
                    }
    
                    String medicationName = medicines.get(medicineIndex).getName();
                    int quantity;
                    while (true) {
                        System.out.print("Enter quantity for " + medicationName + ": ");
                        try {
                            quantity = Integer.parseInt(scanner.nextLine().trim());
                            if (quantity > 0) {
                                break; // Valid quantity
                            } else {
                                System.out.println("Quantity must be a positive number.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid quantity. Please enter a valid number.");
                        }
                    }
    
                    Prescription prescription = new Prescription(medicationName, quantity);
                    prescription.updateStatus(PrescriptionStatus.PENDING);
                    prescriptions.add(prescription);
                }
    
                if (outcomeRecord == null) {
                    outcomeRecord = new OutcomeRecord(dateOfAppointment, serviceType, consultationNotes);
                    existingAppointment.setOutcomeRecord(outcomeRecord);
                } else {
                    outcomeRecord.getPrescriptions().clear();
                }
                prescriptions.forEach(outcomeRecord::addPrescription);
            }
    
            if (existingAppointment.getStatus() == AppointmentStatus.ACCEPTED) {
                existingAppointment.updateStatus(AppointmentStatus.COMPLETED);
            }
    
            initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", existingAppointment);
            System.out.println("\nOutcome record updated successfully.");
    
            initialDataAppointments.reloadData();
    
        } catch (IOException e) {
            System.err.println("Error while updating the appointment: " + e.getMessage());
        }
    }
    
    

}
