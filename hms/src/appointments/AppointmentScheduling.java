package appointments;

import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataAppointments;
import filereaders.InitialDataStaff;
import medicalrecords.OutcomeRecord;
import medicalrecords.Prescription;
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

public class AppointmentScheduling {
    private InitialDataAppointments initialDataAppointments;
    private InitialDataAppointmentSlots initialDataAppointmentSlots;
    private InitialDataStaff initialDataStaff;


    public AppointmentScheduling(InitialDataStaff initialDataStaff, InitialDataAppointments initialDataAppointments,InitialDataAppointmentSlots initialDataAppointmentSlots) {
        this.initialDataAppointments = initialDataAppointments; // Link with InitialData class to access data
        this.initialDataAppointmentSlots = initialDataAppointmentSlots;
        this.initialDataStaff = initialDataStaff;

        initialDataAppointments.reloadData();
        initialDataAppointmentSlots.reloadData();
        initialDataStaff.reloadData();
    }

    private String generateAppointmentID() {
        // Implement your ID generation logic here (e.g., incrementing number or UUID)
        return "APPT" + System.currentTimeMillis(); // Simple unique ID based on current time
    }

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
        Prescription prescription = new Prescription("-", 0);
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
