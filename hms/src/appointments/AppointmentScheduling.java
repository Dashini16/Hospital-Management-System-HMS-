package appointments;

import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataAppointments;
import filereaders.InitialDataLeaveRequest;
import filereaders.InitialDataStaff;
import filereaders.InitialDataLeaveRequest;

import leave.LeaveRequest;
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
import enums.LeaveRequestStatus;
import enums.PrescriptionStatus;
import enums.WorkingDay;

public class AppointmentScheduling {
    private InitialDataAppointments initialDataAppointments;
    private InitialDataAppointmentSlots initialDataAppointmentSlots;
    private InitialDataStaff initialDataStaff;
    private InitialDataLeaveRequest initialDataLeaveRequest;


    public AppointmentScheduling(InitialDataStaff initialDataStaff, InitialDataAppointments initialDataAppointments,InitialDataAppointmentSlots initialDataAppointmentSlots, InitialDataLeaveRequest initialDataLeaveRequest) {
        this.initialDataAppointments = initialDataAppointments; // Link with InitialData class to access data
        this.initialDataAppointmentSlots = initialDataAppointmentSlots;
        this.initialDataStaff = initialDataStaff;
        this.initialDataLeaveRequest = initialDataLeaveRequest;

        initialDataAppointments.reloadData();
        initialDataAppointmentSlots.reloadData();
        initialDataStaff.reloadData();
        initialDataLeaveRequest.reloadData();
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
                    && (appointment.getStatus() == AppointmentStatus.ACCEPTED || appointment.getStatus() == AppointmentStatus.PENDING))
            .collect(Collectors.toList());
    
        if (userAcceptedAppointments.isEmpty()) {
            System.out.println("No accepted appointments available for rescheduling.");
            return;
        }
    
        // Display available appointments for rescheduling with Doctor's name
        System.out.println("Select an appointment to reschedule (or type 'exit' to cancel):");
        System.out.println("==============================================================");
    
        for (int i = 0; i < userAcceptedAppointments.size(); i++) {
            Appointment appointment = userAcceptedAppointments.get(i);
            String doctorID = appointment.getDoctorID();
    
            String doctorName = initialDataStaff.getDoctors().stream()
                    .filter(doc -> doc.getUserID().equals(doctorID))
                    .map(Doctor::getName)
                    .findFirst()
                    .orElse("Unknown Doctor");
    
            System.out.printf("Option %d:\n", (i + 1));
            System.out.printf("   Date          : %s\n", appointment.getDate());
            System.out.printf("   Time          : %s\n", appointment.getTime());
            System.out.printf("   Doctor        : %s \n", doctorName);
            System.out.println("--------------------------------------------------------------");
        }
    
        // Prompt user to select an appointment
        int appointmentIndex = -1;
        while (appointmentIndex < 0 || appointmentIndex >= userAcceptedAppointments.size()) {
            System.out.print("Enter the number of the appointment to reschedule (or type 'exit' to cancel): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting rescheduling process.");
                return; // Exit if the user types 'exit'
            }
            try {
                appointmentIndex = Integer.parseInt(input) - 1;
                if (appointmentIndex < 0 || appointmentIndex >= userAcceptedAppointments.size()) {
                    System.out.println("Invalid choice. Please select a valid number from the list.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    
        Appointment existingAppointment = userAcceptedAppointments.get(appointmentIndex);
        String doctorID = existingAppointment.getDoctorID();
    
        // Get the next 14 days the doctor is available excluding leave dates
        List<LocalDate> availableDates = getDoctorAvailabilityNext14DaysExcludingLeave(doctorID);
        if (availableDates.isEmpty()) {
            System.out.println("Doctor is not available in the next 14 days.");
            return;
        }
    
        System.out.println("Available dates for rescheduling (or type 'exit' to cancel):");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out.println((i + 1) + ". " + availableDates.get(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    
        // Prompt user to select a date
        int dateChoice = promptForDateChoice(scanner, availableDates);
        if (dateChoice == -1) {
            System.out.println("Exiting rescheduling process.");
            return;
        }
        LocalDate selectedDate = availableDates.get(dateChoice - 1);
    
        // Show available times for the selected date
        List<LocalTime> availableTimes = printAvailableTimes(doctorID, selectedDate);
    
        int timeChoice = promptForTimeChoice(scanner, availableTimes);
        if (timeChoice == -1) {
            System.out.println("Exiting rescheduling process.");
            return;
        }
        LocalTime selectedTime = availableTimes.get(timeChoice - 1);
    
        String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    
        existingAppointment.setDate(formattedDate);
        existingAppointment.setTime(formattedTime);
        existingAppointment.updateStatus(AppointmentStatus.PENDING);
    
        try {
            initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", existingAppointment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        System.out.println("Appointment rescheduled successfully to " + formattedDate + " at " + formattedTime);
        initialDataAppointments.reloadData();
    }
    
    // Helper to filter available dates excluding doctor's leave dates
    private List<LocalDate> getDoctorAvailabilityNext14DaysExcludingLeave(String doctorID) {
        List<LocalDate> leaveDates = initialDataLeaveRequest.getAllRequests().stream()
                .filter(request -> request.getRequesterID().equals(doctorID) &&
                        request.getStatus() == LeaveRequestStatus.APPROVED)
                .map(LeaveRequest::getLeaveDate)
                .collect(Collectors.toList());
    
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(14);
    
        return initialDataAppointmentSlots.getLists().stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID))
                .flatMap(slot -> today.datesUntil(limitDate.plusDays(1))
                        .filter(date -> slot.getWorkingDays().stream()
                                .anyMatch(day -> day.name().equalsIgnoreCase(date.getDayOfWeek().name())))
                        .filter(date -> !leaveDates.contains(date))) // Exclude leave dates
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    // Prompts user to select a date with 'exit' option
    private int promptForDateChoice(Scanner scanner, List<LocalDate> availableDates) {
        while (true) {
            System.out.print("Choose a date by entering the corresponding number (or type 'exit' to cancel): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) return -1;
    
            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= availableDates.size()) return choice;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid choice. Please select a valid number from the list.");
        }
    }
    
    // Prompts user to select a time with 'exit' option
    private int promptForTimeChoice(Scanner scanner, List<LocalTime> availableTimes) {
        while (true) {
            System.out.print("Choose a time by entering the corresponding number (or type 'exit' to cancel): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) return -1;
    
            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= availableTimes.size()) return choice;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid choice. Please select a valid number from the list.");
        }
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
        System.out.println("Select a doctor by entering the corresponding number (or type 'exit' to cancel):");
        for (int i = 0; i < availableDoctors.size(); i++) {
            System.out.println((i + 1) + ". " + availableDoctors.get(i).getName());
        }
    
        int doctorIndex = -1;
        while (doctorIndex < 0 || doctorIndex >= availableDoctors.size()) {
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting appointment scheduling.");
                return; // Exit if the user enters "exit"
            }
            try {
                doctorIndex = Integer.parseInt(input) - 1;
                if (doctorIndex < 0 || doctorIndex >= availableDoctors.size()) {
                    System.out.println("Invalid choice. Please select a valid number from the list.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    
        Doctor selectedDoctor = availableDoctors.get(doctorIndex);
        String doctorID = selectedDoctor.getUserID();
    
        // Retrieve available dates excluding leave days
        List<LocalDate> availableDates = getDoctorAvailabilityNext14Days(doctorID);
        if (availableDates.isEmpty()) {
            System.out.println("Doctor not available in the next 14 days.");
            return;
        }
    
        // Display available dates
        System.out.println("Available dates (or type 'exit' to cancel):");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out.println((i + 1) + ". " + availableDates.get(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    
        // User selects a date
        System.out.print("Choose a date by entering the corresponding number: ");
        String dateInput = scanner.nextLine().trim();
        if (dateInput.equalsIgnoreCase("exit")) {
            System.out.println("Exiting appointment scheduling.");
            return; // Exit if the user enters "exit"
        }
        int dateChoice;
        try {
            dateChoice = Integer.parseInt(dateInput);
            if (dateChoice < 1 || dateChoice > availableDates.size()) {
                System.out.println("Invalid choice. Exiting.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Exiting.");
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
        System.out.println("Available times (or type 'exit' to cancel):");
        for (int i = 0; i < availableTimes.size(); i++) {
            System.out.println((i + 1) + ". " + availableTimes.get(i));
        }
    
        // User selects a time
        System.out.print("Choose a time by entering the corresponding number: ");
        String timeInput = scanner.nextLine().trim();
        if (timeInput.equalsIgnoreCase("exit")) {
            System.out.println("Exiting appointment scheduling.");
            return; // Exit if the user enters "exit"
        }
        int timeChoice;
        try {
            timeChoice = Integer.parseInt(timeInput);
            if (timeChoice < 1 || timeChoice > availableTimes.size()) {
                System.out.println("Invalid choice. Exiting.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Exiting.");
            return;
        }
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
