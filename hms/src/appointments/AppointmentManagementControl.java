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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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

    public AppointmentManagementControl(InitialDataMedicine initialDataMedicine, InitialDataStaff initialDataStaff,
            InitialDataAppointments initialDataAppointments) {
        this.initialDataAppointments = initialDataAppointments; // Link with InitialData class to access data
        this.initialDataStaff = initialDataStaff;
        this.initialDataMedicine = initialDataMedicine;

        initialDataAppointments.reloadData();
        initialDataMedicine.reloadData();
        initialDataStaff.reloadData();
    }

    public void viewAllAppointments(boolean showDetails) {
        initialDataAppointments.reloadData();

        // Sort appointments by date (oldest to newest)
        List<Appointment> sortedAppointments = initialDataAppointments.getLists().stream()
                .sorted(Comparator
                        .comparing(a -> LocalDate.parse(a.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                .collect(Collectors.toList());

        System.out.println("\n===== All Appointments (Oldest to Newest) =====");
        int i = 1;
        for (Appointment appointment : sortedAppointments) {
            System.out.printf("\n========== Appointment %d ==========%n", i++);
            System.out.printf("Appointment ID   : %s%n", appointment.getAppointmentID());
            System.out.printf("Patient ID       : %s%n", appointment.getPatientID());
            System.out.printf("Doctor ID        : %s%n", appointment.getDoctorID());
            System.out.printf("Date             : %s%n", appointment.getDate());
            System.out.printf("Time             : %s%n", appointment.getTime());
            System.out.printf("Status           : %s%n", appointment.getStatus());

            if (showDetails) {
                System.out.println("Details          : " + appointment);
            }
        }
        System.out.println("=====================================");
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

    public void viewOutcomeRecords(boolean showPendingPrescriptionsOnly) {
        initialDataAppointments.reloadData();
        int recordCount = 1;

        // Sort appointments by appointment date in ascending order (furthest in the
        // past first)
        List<Appointment> sortedAppointments = initialDataAppointments.getLists().stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.COMPLETED
                        && appointment.getOutcomeRecord() != null)
                .sorted((a1, a2) -> {
                    LocalDate date1 = LocalDate.parse(a1.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalDate date2 = LocalDate.parse(a2.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return date1.compareTo(date2); // Sort in ascending order by appointment date (oldest to newest)
                })
                .collect(Collectors.toList());

        System.out.println("===== Outcome Records with Pending Prescriptions =====\n");

        for (Appointment appointment : sortedAppointments) {
            OutcomeRecord outcomeRecord = appointment.getOutcomeRecord();

            // Check for prescriptions with "PENDING" status
            List<Prescription> pendingPrescriptions = outcomeRecord.getPrescriptions().stream()
                    .filter(prescription -> prescription.getStatus() == PrescriptionStatus.PENDING)
                    .collect(Collectors.toList());

            if (!pendingPrescriptions.isEmpty()) {
                System.out.printf("========== Outcome Record #%d ==========%n", recordCount++);
                System.out.printf("Appointment ID       : %s%n", appointment.getAppointmentID());
                System.out.printf("Patient ID           : %s%n", appointment.getPatientID());
                System.out.printf("Doctor ID            : %s%n", appointment.getDoctorID());
                System.out.printf("Appointment Date     : %s%n", appointment.getDate());
                System.out.printf("Service Type         : %s%n", outcomeRecord.getServiceType());
                System.out.printf("Consultation Notes   : %s%n", outcomeRecord.getConsultationNotes());

                System.out.println("\nPending Prescriptions:");
                System.out.println("----------------------------------------------------");
                System.out.printf("%-20s %-10s %-10s%n", "Medication Name", "Quantity", "Status");
                System.out.println("----------------------------------------------------");

                for (Prescription prescription : pendingPrescriptions) {
                    System.out.printf("%-20s %-10d %-10s%n",
                            prescription.getMedicationName(),
                            prescription.getQuantity(),
                            prescription.getStatus());
                }
                System.out.println("====================================================\n");
            }
        }

        if (recordCount == 1) {
            System.out.println("No completed appointments with pending prescriptions found.");
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
                                        && LocalDate
                                                .parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
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
        Scanner scanner = new Scanner(System.in);

        // Display only outcome records with pending prescriptions
        System.out.println("===== Outcome Records with Pending Prescriptions =====\n");
        List<Appointment> eligibleAppointments = initialDataAppointments.getLists().stream()
                .filter(app -> app.getStatus() == AppointmentStatus.COMPLETED
                        && app.getOutcomeRecord() != null
                        && app.getOutcomeRecord().getPrescriptions().stream()
                                .anyMatch(prescription -> prescription.getStatus() == PrescriptionStatus.PENDING))
                .sorted((a1, a2) -> {
                    LocalDate date1 = LocalDate.parse(a1.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    LocalDate date2 = LocalDate.parse(a2.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return date1.compareTo(date2); // Sort by appointment date
                })
                .collect(Collectors.toList());

        // Display each eligible appointment with a consistent index
        for (int i = 0; i < eligibleAppointments.size(); i++) {
            Appointment appointment = eligibleAppointments.get(i);
            OutcomeRecord outcomeRecord = appointment.getOutcomeRecord();

            System.out.printf("========== Outcome Record #%d ==========%n", i + 1);
            System.out.printf("Appointment ID       : %s%n", appointment.getAppointmentID());
            System.out.printf("Patient ID           : %s%n", appointment.getPatientID());
            System.out.printf("Doctor ID            : %s%n", appointment.getDoctorID());
            System.out.printf("Appointment Date     : %s%n", appointment.getDate());
            System.out.printf("Service Type         : %s%n", outcomeRecord.getServiceType());
            System.out.printf("Consultation Notes   : %s%n", outcomeRecord.getConsultationNotes());

            System.out.println("\nPending Prescriptions:");
            System.out.println("----------------------------------------------------");
            System.out.printf("%-20s %-10s %-10s%n", "Medication Name", "Quantity", "Status");
            System.out.println("----------------------------------------------------");

            for (Prescription prescription : outcomeRecord.getPrescriptions()) {
                if (prescription.getStatus() == PrescriptionStatus.PENDING) {
                    System.out.printf("%-20s %-10d %-10s%n",
                            prescription.getMedicationName(),
                            prescription.getQuantity(),
                            prescription.getStatus());
                }
            }
            System.out.println("====================================================\n");
        }

        if (eligibleAppointments.isEmpty()) {
            System.out.println("No completed appointments with pending prescriptions found.");
            return;
        }

        System.out.print("\nEnter the number of the outcome record to update (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit")) {
            System.out.println("Exiting update process.");
            return;
        }

        int appointmentIndex;
        try {
            appointmentIndex = Integer.parseInt(input) - 1;
            if (appointmentIndex < 0 || appointmentIndex >= eligibleAppointments.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            // Access the correct appointment from eligibleAppointments
            Appointment selectedAppointment = eligibleAppointments.get(appointmentIndex);
            List<Prescription> prescriptions = selectedAppointment.getOutcomeRecord().getPrescriptions();

            // Loop for dispensing or rejecting prescriptions
            while (true) {
                List<Prescription> pendingPrescriptions = prescriptions.stream()
                        .filter(prescription -> prescription.getStatus() == PrescriptionStatus.PENDING)
                        .collect(Collectors.toList());

                if (pendingPrescriptions.isEmpty()) {
                    System.out.println("No more pending prescriptions for this appointment.");
                    break;
                }

                System.out.println("\nPending Prescriptions:");
                for (int i = 0; i < pendingPrescriptions.size(); i++) {
                    Prescription prescription = pendingPrescriptions.get(i);
                    System.out.printf("%d. Medication: %s, Quantity: %d, Status: %s%n",
                            i + 1,
                            prescription.getMedicationName(),
                            prescription.getQuantity(),
                            prescription.getStatus());
                }

                System.out.print(
                        "\nEnter the number of the prescription to dispense or reject (or type 'exit' to finish): ");
                String prescriptionInput = scanner.nextLine().trim();
                if (prescriptionInput.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting dispensing process for this appointment.");
                    break;
                }

                int prescriptionIndex;
                try {
                    prescriptionIndex = Integer.parseInt(prescriptionInput) - 1;
                    if (prescriptionIndex < 0 || prescriptionIndex >= pendingPrescriptions.size()) {
                        System.out.println("Invalid selection. Please try again.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    continue;
                }

                Prescription selectedPrescription = pendingPrescriptions.get(prescriptionIndex);
                String medicineName = selectedPrescription.getMedicationName();

                // Ask to dispense or reject the prescription
                System.out.print("Do you want to (1) dispense or (2) reject this prescription? Enter 1 or 2: ");
                String actionChoice = scanner.nextLine().trim();

                if (actionChoice.equals("2")) { // Reject the prescription
                    selectedPrescription.updateStatus(PrescriptionStatus.REJECTED);
                    System.out.println("Prescription for " + medicineName + " has been rejected.");
                } else if (actionChoice.equals("1")) { // Dispense the prescription
                    Medicine selectedMedicine = initialDataMedicine.getLists().stream()
                            .filter(medicine -> medicine.getName().equals(medicineName))
                            .findFirst()
                            .orElse(null);

                    if (selectedMedicine == null) {
                        System.out.println("Medicine not found in inventory.");
                        continue;
                    }

                    // Check if stock is sufficient
                    if (selectedMedicine.getInitialStock() < selectedPrescription.getQuantity()) {
                        System.out.println("Insufficient stock for " + medicineName
                                + ". Current stock: " + selectedMedicine.getInitialStock());
                        continue;
                    }

                    // Update stock and prescription status
                    selectedMedicine
                            .setInitialStock(selectedMedicine.getInitialStock() - selectedPrescription.getQuantity());
                    selectedPrescription.updateStatus(PrescriptionStatus.DISPENSED);
                    System.out.println("Dispensed " + selectedPrescription.getQuantity() + " units of " + medicineName);
                } else {
                    System.out.println("Invalid choice. Please enter 1 to dispense or 2 to reject.");
                    continue;
                }

                // Save updates to files
                try {
                    initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", selectedAppointment);
                    initialDataMedicine.rewriteMedicines("hms/src/data/Medicine_List.csv");
                    System.out.println("Prescription status and inventory updated successfully.");
                } catch (IOException e) {
                    System.out.println("Error saving updates: " + e.getMessage());
                }

                initialDataMedicine.reloadData();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
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

            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();

            if (choice == 1) {
                availableAppointments = initialDataAppointments.getLists().stream()
                        .filter(app -> app.getStatus() == AppointmentStatus.ACCEPTED)
                        .filter(app -> {
                            LocalDate appointmentDate = LocalDate.parse(app.getDate(), dateFormatter);
                            LocalTime appointmentTime = LocalTime.parse(app.getTime(),
                                    DateTimeFormatter.ofPattern("HH:mm"));
                            LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
                            return appointmentDateTime.isBefore(now); // Only include past appointments
                        })
                        .collect(Collectors.toList());
            } else {
                availableAppointments = initialDataAppointments.getLists().stream()
                        .filter(app -> app.getStatus() == AppointmentStatus.COMPLETED)
                        .filter(app -> {
                            LocalDate appointmentDate = LocalDate.parse(app.getDate(), dateFormatter);
                            LocalTime appointmentTime = LocalTime.parse(app.getTime(),
                                    DateTimeFormatter.ofPattern("HH:mm"));
                            LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
                            return appointmentDateTime.isBefore(now); // Only include past appointments
                        })
                        .collect(Collectors.toList());
            }

            if (availableAppointments.isEmpty()) {
                System.out.println("\nNo past appointments found with the selected status.\n");
                return;
            }

            System.out.println("\n== Select an Appointment to Update ==");
            System.out.printf("%-5s %-20s %-15s %-15s %-15s%n", "No.", "Appointment ID", "Patient ID", "Date",
                    "Status");
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
                if (dateOfAppointment.isEmpty())
                    break; // Keep current date if empty
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
