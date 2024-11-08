package appointments;

import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataAppointments;
import filereaders.InitialDataMedicine;
import filereaders.InitialDataPatient;
import filereaders.InitialDataStaff;
import lookups.UserLookup;
import medicalrecords.OutcomeRecord;
import medicalrecords.Prescription;
import medicinemanagements.Medicine;
import users.Doctor;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import appointmentslots.AppointmentSlot;
import authorization.AuthorizationControl;
import enums.AppointmentStatus;
import enums.PrescriptionStatus;
import enums.WorkingDay;

public class AppointmentManagementControl {
    private InitialDataAppointments initialDataAppointments;
    private InitialDataAppointmentSlots initialDataAppointmentSlots;
    private InitialDataStaff initialDataStaff;
    private InitialDataPatient initialDataPatient;
    private InitialDataMedicine initialDataMedicine;

    public AppointmentManagementControl(InitialDataMedicine initialDataMedicine, InitialDataPatient initialDataPatient,
            InitialDataStaff initialDataStaff, InitialDataAppointments initialDataAppointments,
            InitialDataAppointmentSlots initialDataAppointmentSlots) {
        this.initialDataAppointments = initialDataAppointments; // Link with InitialData class to access data
        this.initialDataAppointmentSlots = initialDataAppointmentSlots;
        this.initialDataStaff = initialDataStaff;
        this.initialDataPatient = initialDataPatient;
        this.initialDataMedicine = initialDataMedicine;

        initialDataAppointments.reloadData();
        initialDataAppointmentSlots.reloadData();
        initialDataPatient.reloadData();
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

    private String generateAppointmentID() {
        // Implement your ID generation logic here (e.g., incrementing number or UUID)
        return "APPT" + System.currentTimeMillis(); // Simple unique ID based on current time
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

    // public void cancelAppointment() {
    //     // PROMPT USER FOR APPOINTMENT ID
    //     initialDataAppointments.reloadData();
    //     Scanner scanner = new Scanner(System.in);
    //     System.out.print("Enter Appointment ID to cancel: ");
    //     String appointmentID = scanner.nextLine();

    //     // FIND IF APPOINTMENT EXISTS
    //     Appointment existingAppointment = null;
    //     try {
    //         existingAppointment = initialDataAppointments.findAppointment("hms/src/data/Appointments_List.csv",
    //                 appointmentID);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     if (existingAppointment == null) {
    //         System.out.println("Appointment ID not found.");
    //         return;
    //     }

    //     // GET CURRENT LOGGED IN PATIENT ID
    //     String currentPatientID = AuthorizationControl.getCurrentUserId(); 

    //     // Assuming AuthorizationControl is managing the current user's session
    //     // CHECK IF THE LOGGED-IN PATIENT IS THE SAME AS THE APPOINTMENT'S PATIENT
    //     if (!existingAppointment.getPatientID().equals(currentPatientID)) {
    //         System.out.println("You are not authorized to cancel this appointment. This is not your appointment.");
    //         return;
    //     }

    //     // CHECK STATUS BEFORE CANCELLING
    //     if (existingAppointment.getStatus() != AppointmentStatus.PENDING
    //             && existingAppointment.getStatus() != AppointmentStatus.ACCEPTED) {
    //         System.out.println("Cannot cancel appointment. Only pending or accepted appointments can be canceled.");
    //         return;
    //     }

    //     // CANCEL APPOINTMENT
    //     existingAppointment.updateStatus(AppointmentStatus.CANCELLED);

    //     // WRITE UPDATED APPOINTMENT TO FILE
    //     try {
    //         initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", existingAppointment);
    //         System.out.println("Appointment canceled successfully.");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

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

    // public void rescheduleAppointment() {
    // Scanner scanner = new Scanner(System.in);
    // String currentUserID = AuthorizationControl.getCurrentUserId();

    // // Retrieve all ACCEPTED appointments for the current user
    // List<Appointment> userAcceptedAppointments =
    // initialDataAppointments.getLists().stream()
    // .filter(appointment -> appointment.getPatientID().equals(currentUserID)
    // && appointment.getStatus() == AppointmentStatus.ACCEPTED)
    // .collect(Collectors.toList());

    // if (userAcceptedAppointments.isEmpty()) {
    // System.out.println("No accepted appointments available for rescheduling.");
    // return;
    // }

    // // Display available appointments for rescheduling with Doctor's name
    // System.out.println("Select an appointment to reschedule:");
    // System.out.println("==============================================================");

    // for (int i = 0; i < userAcceptedAppointments.size(); i++) {
    // Appointment appointment = userAcceptedAppointments.get(i);
    // String doctorID = appointment.getDoctorID();

    // // Find the doctor's name directly within the loop
    // String doctorName = initialDataStaff.getDoctors().stream()
    // .filter(doc -> doc.getUserID().equals(doctorID))
    // .map(Doctor::getName)
    // .findFirst()
    // .orElse("Unknown Doctor");

    // // Display appointment details with cleaner formatting
    // System.out.printf("Option %d:\n", (i + 1));
    // System.out.printf(" Appointment ID: %s\n", appointment.getAppointmentID());
    // System.out.printf(" Date : %s\n", appointment.getDate());
    // System.out.printf(" Time : %s\n", appointment.getTime());
    // System.out.printf(" Doctor : %s (ID: %s)\n", doctorName, doctorID);
    // System.out.println("--------------------------------------------------------------");
    // }

    // // Prompt user to select an appointment
    // int appointmentIndex = -1;
    // while (appointmentIndex < 0 || appointmentIndex >=
    // userAcceptedAppointments.size()) {
    // System.out.print("Enter the number of the appointment to reschedule: ");
    // try {
    // appointmentIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
    // if (appointmentIndex < 0 || appointmentIndex >=
    // userAcceptedAppointments.size()) {
    // System.out.println("Invalid choice. Please select a valid number from the
    // list.");
    // }
    // } catch (NumberFormatException e) {
    // System.out.println("Invalid input. Please enter a number.");
    // }
    // }

    // // Proceed with the rest of the rescheduling process as before
    // Appointment existingAppointment =
    // userAcceptedAppointments.get(appointmentIndex);
    // String doctorID = existingAppointment.getDoctorID();

    // // Get the next 14 days the doctor is available
    // List<LocalDate> availableDates = getDoctorAvailabilityNext14Days(doctorID);
    // if (availableDates.isEmpty()) {
    // System.out.println("Doctor is not available in the next 14 days.");
    // return;
    // }

    // // Display available dates for rescheduling
    // System.out.println("Available dates for rescheduling:");
    // for (int i = 0; i < availableDates.size(); i++) {
    // System.out
    // .println((i + 1) + ". " +
    // availableDates.get(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    // }

    // // Prompt user to select a date
    // int dateChoice = promptForDateChoice(scanner, availableDates);
    // if (dateChoice == -1) {
    // System.out.println("Invalid choice. Exiting.");
    // return;
    // }
    // LocalDate selectedDate = availableDates.get(dateChoice - 1);

    // // Show available times for the selected date
    // List<LocalTime> availableTimes = printAvailableTimes(doctorID, selectedDate);

    // // Prompt user to select a time
    // int timeChoice = promptForTimeChoice(scanner, availableTimes);
    // if (timeChoice == -1) {
    // System.out.println("Invalid choice. Exiting.");
    // return;
    // }
    // LocalTime selectedTime = availableTimes.get(timeChoice - 1);

    // // Format the date and time
    // String formattedDate =
    // selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    // String formattedTime =
    // selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"));

    // // Update appointment date and time
    // existingAppointment.setDate(formattedDate);
    // existingAppointment.setTime(formattedTime);

    // // Write updated appointment to file
    // try {
    // initialDataAppointments.writeData("hms/src/data/Appointments_List.csv",
    // existingAppointment);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

    // System.out.println("Appointment rescheduled successfully to " + formattedDate
    // + " at " + formattedTime);
    // initialDataAppointments.reloadData();

    // }

    public void rescheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
        String currentUserID = AuthorizationControl.getCurrentUserId();

        // Retrieve all ACCEPTED appointments for the current user
        List<Appointment> userAcceptedAppointments = initialDataAppointments.getLists().stream()
                .filter(appointment -> appointment.getPatientID().equals(currentUserID)
                        && appointment.getStatus() == AppointmentStatus.ACCEPTED)
                .collect(Collectors.toList());

        if (userAcceptedAppointments.isEmpty()) {
            System.out.println("No accepted appointments available for rescheduling.");
            return;
        }

        // Display available appointments for rescheduling with Doctor's name
        System.out.println("Select an appointment to reschedule:");
        System.out.println("==============================================================");

        for (int i = 0; i < userAcceptedAppointments.size(); i++) {
            Appointment appointment = userAcceptedAppointments.get(i);
            String doctorID = appointment.getDoctorID();

            // Find the doctor's name directly within the loop
            String doctorName = initialDataStaff.getDoctors().stream()
                    .filter(doc -> doc.getUserID().equals(doctorID))
                    .map(Doctor::getName)
                    .findFirst()
                    .orElse("Unknown Doctor");

            // Display appointment details with cleaner formatting
            System.out.printf("Option %d:\n", (i + 1));
            System.out.printf("   Appointment ID: %s\n", appointment.getAppointmentID());
            System.out.printf("   Date          : %s\n", appointment.getDate());
            System.out.printf("   Time          : %s\n", appointment.getTime());
            System.out.printf("   Doctor        : %s (ID: %s)\n", doctorName, doctorID);
            System.out.println("--------------------------------------------------------------");
        }

        // Prompt user to select an appointment
        int appointmentIndex = -1;
        while (appointmentIndex < 0 || appointmentIndex >= userAcceptedAppointments.size()) {
            System.out.print("Enter the number of the appointment to reschedule: ");
            try {
                appointmentIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (appointmentIndex < 0 || appointmentIndex >= userAcceptedAppointments.size()) {
                    System.out.println("Invalid choice. Please select a valid number from the list.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        // Proceed with the rest of the rescheduling process as before
        Appointment existingAppointment = userAcceptedAppointments.get(appointmentIndex);
        String doctorID = existingAppointment.getDoctorID();

        // Get the next 14 days the doctor is available
        List<LocalDate> availableDates = getDoctorAvailabilityNext14Days(doctorID);
        if (availableDates.isEmpty()) {
            System.out.println("Doctor is not available in the next 14 days.");
            return;
        }

        // Display available dates for rescheduling
        System.out.println("Available dates for rescheduling:");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out
                    .println((i + 1) + ". " + availableDates.get(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        // Prompt user to select a date
        int dateChoice = promptForDateChoice(scanner, availableDates);
        if (dateChoice == -1) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }
        LocalDate selectedDate = availableDates.get(dateChoice - 1);

        // Show available times for the selected date
        List<LocalTime> availableTimes = printAvailableTimes(doctorID, selectedDate);

        // Prompt user to select a time
        int timeChoice = promptForTimeChoice(scanner, availableTimes);
        if (timeChoice == -1) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }
        LocalTime selectedTime = availableTimes.get(timeChoice - 1);

        // Format the date and time
        String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        // Update appointment date and time
        existingAppointment.setDate(formattedDate);
        existingAppointment.setTime(formattedTime);
        existingAppointment.updateStatus(AppointmentStatus.PENDING);

        // Write updated appointment to file
        try {
            initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", existingAppointment);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Appointment rescheduled successfully to " + formattedDate + " at " + formattedTime);
        initialDataAppointments.reloadData();

    }

    // Method to prompt for valid date choice
    private int promptForDateChoice(Scanner scanner, List<LocalDate> availableDates) {
        System.out.print("Choose a date by entering the corresponding number: ");
        int dateChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        if (dateChoice < 1 || dateChoice > availableDates.size()) {
            return -1;
        }
        return dateChoice;
    }

    // Method to prompt for valid time choice
    private int promptForTimeChoice(Scanner scanner, List<LocalTime> availableTimes) {
        System.out.print("Choose a time by entering the corresponding number: ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        if (timeChoice < 1 || timeChoice > availableTimes.size()) {
            return -1;
        }
        return timeChoice;
    }

    public void scheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
        String patientUserID = AuthorizationControl.getCurrentUserId();

        // Retrieve available doctor IDs with slots in the next 14 days
        Set<String> doctorIDsWithSlots = initialDataAppointmentSlots.getLists().stream()
                .map(AppointmentSlot::getDoctorID)
                .collect(Collectors.toSet());

        // Filter doctors with available slots
        List<Doctor> availableDoctors = initialDataStaff.getDoctors().stream()
                .filter(doc -> doctorIDsWithSlots.contains(doc.getUserID()))
                .collect(Collectors.toList());

        if (availableDoctors.isEmpty()) {
            System.out.println("No doctors with available slots in the next 14 days.");
            return;
        }

        // Display doctors to the user
        System.out.println("Select a doctor by entering the corresponding number:");
        for (int i = 0; i < availableDoctors.size(); i++) {
            System.out.println((i + 1) + ". " + availableDoctors.get(i).getName());
        }

        int doctorIndex = -1;
        while (doctorIndex < 0 || doctorIndex >= availableDoctors.size()) {
            System.out.print("Enter your choice: ");
            try {
                doctorIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (doctorIndex < 0 || doctorIndex >= availableDoctors.size()) {
                    System.out.println("Invalid choice. Please select a valid number from the list.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        Doctor selectedDoctor = availableDoctors.get(doctorIndex);
        String doctorID = selectedDoctor.getUserID();

        // Use getDoctorAvailabilityNext14Days to get available dates
        List<LocalDate> availableDates = getDoctorAvailabilityNext14Days(doctorID);
        if (availableDates.isEmpty()) {
            System.out.println("Doctor not available in the next 14 days.");
            return;
        }

        // Display available dates
        System.out.println("Available dates:");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out
                    .println((i + 1) + ". " + availableDates.get(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        // User selects a date
        System.out.print("Choose a date by entering the corresponding number: ");
        int dateChoice = scanner.nextInt();
        scanner.nextLine();
        if (dateChoice < 1 || dateChoice > availableDates.size()) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }
        LocalDate selectedDate = availableDates.get(dateChoice - 1);

        // Generate available times for the selected date at 30-minute intervals
        List<LocalTime> availableTimes = initialDataAppointmentSlots.getLists().stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID) &&
                        slot.getWorkingDays().stream()
                                .anyMatch(day -> day.name().equalsIgnoreCase(selectedDate.getDayOfWeek().name())))
                .flatMap(slot -> {
                    List<LocalTime> times = new ArrayList<>();
                    LocalTime currentTime = slot.getStartTime();
                    while (currentTime.isBefore(slot.getEndTime())) {
                        // Only include times that are not pending or accepted
                        if (isTimeAvailable(doctorID, selectedDate, currentTime)) {
                            times.add(currentTime);
                        }
                        currentTime = currentTime.plusMinutes(30); // Increment by 30 minutes
                    }
                    return times.stream();
                })
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (availableTimes.isEmpty()) {
            System.out.println("No available times for the selected date.");
            return;
        }

        // Display available times
        System.out.println("Available times:");
        for (int i = 0; i < availableTimes.size(); i++) {
            System.out.println((i + 1) + ". " + availableTimes.get(i));
        }

        // User selects a time
        System.out.print("Choose a time by entering the corresponding number: ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine();
        if (timeChoice < 1 || timeChoice > availableTimes.size()) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }
        // timeChoice - 1 becuase it is zero-indexed
        LocalTime selectedTime = availableTimes.get(timeChoice - 1);

        // Generate a new appointment ID and create the appointment
        String appointmentID = generateAppointmentID();
        Appointment appointment = new Appointment(appointmentID, patientUserID, doctorID, selectedDate.toString(),
                selectedTime.toString());

        // Set default outcome record and prescription
        OutcomeRecord outcomeRecord = new OutcomeRecord("00/00/0000", "-", "-");
        Prescription prescription = new Prescription("-");
        prescription.updateStatus(PrescriptionStatus.PENDING);
        outcomeRecord.addPrescription(prescription);
        appointment.setOutcomeRecord(outcomeRecord);
        appointment.updateStatus(AppointmentStatus.PENDING);

        // Write appointment data to csv
        try {
            initialDataAppointments.writeAppointmentToFilewithdefaultOutcome("hms/src/data/Appointments_List.csv",
                    appointment);
            System.out.println("Appointment scheduled successfully for " + patientUserID + " with Doctor "
                    + selectedDoctor.getName() +
                    " on " + selectedDate + " at " + selectedTime);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

        initialDataAppointments.reloadData();
    }

    // Method to print available times for a specific date
    private List<LocalTime> printAvailableTimes(String doctorID, LocalDate date) {
        System.out.println("Available times on " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ":");

        List<AppointmentSlot> slots = initialDataAppointmentSlots.getLists().stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID))
                .collect(Collectors.toList());

        // This will store booked times
        Set<LocalTime> bookedTimes = new HashSet<>(); // Changed to a Set to prevent duplicates
        List<LocalTime> availableTimes = new ArrayList<>();

        // Collect booked times that match the chosen date
        for (Appointment appointment : initialDataAppointments.getLists()) {
            LocalDate appointmentDate = LocalDate.parse(appointment.getDate(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if (appointment.getDoctorID().equals(doctorID) && appointmentDate.isEqual(date)) {
                LocalTime appointmentTime = LocalTime.parse(appointment.getTime());
                bookedTimes.add(appointmentTime);
            }
        }

        // Generate available times based on the doctor's working hours for the specific
        // date
        for (AppointmentSlot slot : slots) {
            LocalTime currentTime = slot.getStartTime();
            LocalTime endTime = slot.getEndTime();

            while (currentTime.isBefore(endTime)) {
                // Only add the time if it's not booked
                if (!bookedTimes.contains(currentTime)) {
                    availableTimes.add(currentTime);
                }
                currentTime = currentTime.plusMinutes(30); // Increment by 30 minutes
            }
        }

        // Display available times with numbering
        if (availableTimes.isEmpty()) {
            System.out.println("No available times.");
        } else {
            for (int i = 0; i < availableTimes.size(); i++) {
                System.out.println((i + 1) + ". " + availableTimes.get(i).format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }

        return availableTimes;
    }

    // Method to get the next 14 days when the doctor is available
    private List<LocalDate> getDoctorAvailabilityNext14Days(String doctorID) {
        List<LocalDate> availableDates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 14; i++) {
            LocalDate date = today.plusDays(i);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // Check if the doctor is working on this specific day
            if (isDoctorWorkingOnDay(doctorID, dayOfWeek)) {
                availableDates.add(date);
            }
        }

        return availableDates;
    }

    // Method to check if doctor is working on a specific day
    private boolean isDoctorWorkingOnDay(String doctorID, DayOfWeek dayOfWeek) {
        for (AppointmentSlot slot : initialDataAppointmentSlots.getLists()) {
            if (slot.getDoctorID().equals(doctorID)) {
                // Check if the dayOfWeek is in the doctor's working days
                List<WorkingDay> workingDays = slot.getWorkingDays();
                return workingDays.stream().anyMatch(day -> day.name().equalsIgnoreCase(dayOfWeek.name()));
            }
        }
        return false;
    }

    private boolean isTimeAvailable(String doctorID, LocalDate date, LocalTime time) {
        // Get all slots for the specified doctor
        List<AppointmentSlot> slots = initialDataAppointmentSlots.getLists().stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID))
                .collect(Collectors.toList());

        // Check each slot to see if the desired time falls within the available hours
        // (inclusive)
        for (AppointmentSlot slot : slots) {
            if (!time.isBefore(slot.getStartTime()) && !time.isAfter(slot.getEndTime())) { // Inclusive check
                // Check each appointment to see if it's already booked for the specified date
                // and time
                for (Appointment appointment : initialDataAppointments.getLists()) {
                    if (appointment.getDoctorID().equals(doctorID) &&
                            LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                    .isEqual(date)
                            &&
                            LocalTime.parse(appointment.getTime()).equals(time)) {

                        // Only ignore the slot if the status is CANCELLED
                        AppointmentStatus status = appointment.getStatus();
                        if (status != AppointmentStatus.CANCELLED) {
                            return false; // Time is unavailable if status is not CANCELLED
                        }
                    }
                }
                return true; // Time is available if no booked appointment conflicts
            }
        }
        return false; // Time is outside of the doctor's available slot hours
    }

}
