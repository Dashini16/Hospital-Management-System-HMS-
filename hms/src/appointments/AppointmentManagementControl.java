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
import users.Patient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import appointmentslots.AppointmentSlot;
import authorization.AuthorizationControl;
import enums.AppointmentStatus;
import enums.PrescriptionStatus;
import enums.WorkingDay;

public class AppointmentManagementControl 
{
    private InitialDataAppointments initialDataAppointments;
    private InitialDataAppointmentSlots initialDataAppointmentSlots;
    private InitialDataStaff initialDataStaff;
    private InitialDataPatient initialDataPatient;
    private InitialDataMedicine initialDataMedicine;

    public AppointmentManagementControl(InitialDataMedicine initialDataMedicine,InitialDataPatient initialDataPatient,InitialDataStaff initialDataStaff, InitialDataAppointments initialDataAppointments, InitialDataAppointmentSlots initialDataAppointmentSlots) {
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

    
    // Method to check if the doctor is available at the specified date and time
    private boolean isDoctorAvailable(String doctorID, LocalDate date, LocalTime time) {
        LocalDateTime requestedDateTime = LocalDateTime.of(date, time);
        boolean isAvailable = false;
    
        for (AppointmentSlot slot : initialDataAppointmentSlots.getLists()) {
            // Log details of the current appointment slot being checked
            System.out.println("Slot: " + slot.displayData());
            if (slot.getDoctorID().equals(doctorID)) {
                // Print the start and end dates of the current slot
                System.out.println("Doctor ID: " + slot.getDoctorID());
                System.out.println("Start Date: " + slot.getStartTime().toLocalDate());
                System.out.println("End Date: " + slot.getEndTime().toLocalDate());
    
                // Check if the requested date is within the appointment slot date range
                if (!requestedDateTime.toLocalDate().isBefore(slot.getStartTime().toLocalDate()) &&
                    !requestedDateTime.toLocalDate().isAfter(slot.getEndTime().toLocalDate())) {
    
                    System.out.println("Checking day of week...");
    
                    // Get the day of the week for the requested appointment
                    DayOfWeek appointmentDay = requestedDateTime.getDayOfWeek();
                    
                    // Directly access the working days from the slot
                    List<WorkingDay> workingDays = slot.getWorkingDays();
    
                    System.out.println("Requested Day: " + appointmentDay);
                    System.out.println("Working Days: " + workingDays);
    
                    // Check if the appointment day corresponds to any working day
                    if (workingDays.stream().anyMatch(day -> day.name().equalsIgnoreCase(appointmentDay.name()))) {
                        // Check if the requested time is within the start and end time
                        if ((requestedDateTime.isEqual(slot.getStartTime()) || requestedDateTime.isEqual(slot.getEndTime())) ||
                            (requestedDateTime.isAfter(slot.getStartTime()) && requestedDateTime.isBefore(slot.getEndTime()))) {
                            System.out.println("Doctor is available for the requested appointment.");
                            for(Appointment appointment : initialDataAppointments.getLists()) {
                                if(appointment.getStatus() != AppointmentStatus.ACCEPTED ) {
                                    //System.out.println("ALLL GOOOODDDDD");
                                    isAvailable = true; // Set availability to true
                                }
                            }
                            //isAvailable = true; // Set availability to true
                            break; // Exit loop if found available
                        }
                    } else {
                        System.out.println("The appointment day does not match the doctor's working days.");
                    }
                }
            }
        }
        return isAvailable; // Return availability status
    }
    
    




// FIND DOCTOR BY ID
public Doctor findDoctorByID(String id) {
    for (Doctor doctor : initialDataStaff.getDoctors()) {
        if (doctor.getUserID().equals(id)) {
            return doctor;
        }
    }
    return null;
}


public Patient findPatientByID(String id) {
    for (Patient patient : initialDataPatient.getLists()) {
        if (patient.getUserID().equals(id)) {
            return patient;
        }
    }
    return null;
}

    public void viewonlyallAppointments() {
        // Add code to view appointment records
       // System.out.println("Viewing appointment records...");

        int I = 1;
        for(Appointment a : initialDataAppointments.getLists()) {
            System.out.println("\n==========Appointment " + I + "===========\n");
            System.out.println("Appointment ID: " + a.getAppointmentID());
            System.out.println("Patient ID: " + a.getPatientID());
            System.out.println("Doctor ID: " + a.getDoctorID());
            System.out.println("Date: " + a.getDate());
            System.out.println("Time: " + a.getTime());

            System.out.println(a.toString());

            I++;
        }
        initialDataAppointments.reloadData();;
    }


    public void viewallAppointments() {
        // Add code to view appointment records
       // System.out.println("Viewing appointment records...");

        int I = 1;
        for(Appointment a : initialDataAppointments.getLists()) {
            System.out.println("\n==========Appointment " + I + "===========\n");
            System.out.println("Appointment ID: " + a.getAppointmentID());
            System.out.println("Patient ID: " + a.getPatientID());
            System.out.println("Doctor ID: " + a.getDoctorID());
            System.out.println("Date: " + a.getDate());
            System.out.println("Time: " + a.getTime());

            System.out.println(a.toString());

            I++;
        }
        initialDataAppointments.reloadData();;
    }

    public void viewAppointments() {
        initialDataAppointments.reloadData();
        String doctorID = AuthorizationControl.getCurrentUserId();
        int i = 1;

        for (Appointment a : initialDataAppointments.getLists()) {
            boolean isAccepted = a.getStatus().equals(AppointmentStatus.ACCEPTED);
            boolean isCurrentDoctor = a.getDoctorID().equals(doctorID);

            
    
            if (isAccepted && isCurrentDoctor) {
                System.out.println("\n========== Appointment " + i + " ===========");
                System.out.println("Appointment ID: " + a.getAppointmentID());
                System.out.println("Patient ID: " + a.getPatientID());
                System.out.println("Doctor ID: " + a.getDoctorID());
                System.out.println("Date: " + a.getDate());
                System.out.println("Time: " + a.getTime());
                System.out.println("Status: " + a.getStatus());
    
                OutcomeRecord outcomeRecord = a.getOutcomeRecord();
                if (outcomeRecord != null) {
                    // Display specific details of the outcome record separately
                    System.out.println("Outcome Record Date: " + outcomeRecord.getDateOfAppointment());
                    System.out.println("Service: " + outcomeRecord.getServiceType());
                    
                    if (outcomeRecord.getPrescriptions() != null) {
                        System.out.println("Prescriptions: ");
                        for (Prescription p : outcomeRecord.getPrescriptions()) {
                            System.out.println("  - Prescription: " + p.getMedicationName() + ", Status: " + p.getStatus());
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
            System.out.println("No accepted appointments found for the current doctor.");
        }
    }
    
    
    public void viewAppointmentsPatient() {
        initialDataAppointments.reloadData();
        String patientID = AuthorizationControl.getCurrentUserId();
        int i = 1;

        for (Appointment a : initialDataAppointments.getLists()) {
            boolean isAccepted = a.getStatus().equals(AppointmentStatus.ACCEPTED);
            boolean isCurrentPatient = a.getPatientID().equals(patientID);

            
    
            if (isAccepted && isCurrentPatient) {
                System.out.println("\n========== Appointment " + i + " ===========");
                System.out.println("Appointment ID: " + a.getAppointmentID());
                System.out.println("Patient ID: " + a.getPatientID());
                System.out.println("Doctor ID: " + a.getDoctorID());
                System.out.println("Date: " + a.getDate());
                System.out.println("Time: " + a.getTime());
                System.out.println("Status: " + a.getStatus());
    
                OutcomeRecord outcomeRecord = a.getOutcomeRecord();
                if (outcomeRecord != null) {
                    // Display specific details of the outcome record separately
                    System.out.println("Outcome Record Date: " + outcomeRecord.getDateOfAppointment());
                    System.out.println("Service: " + outcomeRecord.getServiceType());
                    
                    if (outcomeRecord.getPrescriptions() != null) {
                        System.out.println("Prescriptions: ");
                        for (Prescription p : outcomeRecord.getPrescriptions()) {
                            System.out.println("  - Prescription: " + p.getMedicationName() + ", Status: " + p.getStatus());
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
            System.out.println("No accepted appointments found for the current doctor.");
        }
    }    
    
    
    private String generateAppointmentID() {
        // Implement your ID generation logic here (e.g., incrementing number or UUID)
        return "APPT" + System.currentTimeMillis(); // Simple unique ID based on current time
    }


    public void viewOutcomeRecords() {
        // Add code to view outcome records
        System.out.println("Viewing outcome records...");
        int i =1;
        for(Appointment a : initialDataAppointments.getLists()) {
            if (a.getOutcomeRecord() != null) {
                System.out.println("==========Outcome Record " + i + "===========");
                System.out.println("Appointment ID: " + a.getAppointmentID());
                System.out.println("Outcome Record: " + a.getOutcomeRecord().toString());
                i++;
            }
        }
        initialDataAppointments.reloadData();;
    }
    public void viewPastOutcomeRecords() {
        // Add code to view outcome records
        //System.out.println("Viewing outcome records...");
        int i =1;
        for(Appointment a : initialDataAppointments.getLists()) {
            if (a.getOutcomeRecord() != null) {
                if(a.getStatus().equals(AppointmentStatus.COMPLETED))
                {
                    System.out.println("==========Outcome Record " + i + "===========");
                    System.out.println("Appointment ID: " + a.getAppointmentID());
                    System.out.println("Outcome Record: " + a.getOutcomeRecord().toString());
                    i++;
                }
            }
        }
        initialDataAppointments.reloadData();;
    }



    public void rescheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Appointment ID to reschedule: ");
        String appointmentID = scanner.nextLine();
        
        // FIND IF APPOINTMENT EXISTS
        Appointment existingAppointment = null;
        try {
            existingAppointment = initialDataAppointments.findAppointment("hms/src/data/Appointments_List.csv", appointmentID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        if (existingAppointment == null) {
            System.out.println("Appointment ID not found.");
            return;
        }
    
        // GET DOCTOR ID FROM EXISTING APPOINTMENT
        String doctorID = existingAppointment.getDoctorID();
    
        // GET THE NEXT 7 DAYS THE DOCTOR IS AVAILABLE
        List<LocalDate> availableDates = getDoctorAvailabilityNext7Days(doctorID);
        if (availableDates.isEmpty()) {
            System.out.println("Doctor is not available in the next 7 days.");
            return;
        }
    
        // DISPLAY AVAILABLE DATES FOR THE NEXT 7 DAYS
        System.out.println("Available dates for rescheduling:");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out.println((i + 1) + ". " + availableDates.get(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    
        // PROMPT USER TO SELECT A DATE
        int dateChoice = promptForDateChoice(scanner, availableDates);
        if (dateChoice == -1) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }
        LocalDate selectedDate = availableDates.get(dateChoice - 1);
    
        // SHOW AVAILABLE TIMES FOR THE SELECTED DATE
        List<LocalTime> availableTimes = printAvailableTimes(doctorID, selectedDate);
        
        // PROMPT USER TO SELECT A TIME
        int timeChoice = promptForTimeChoice(scanner, availableTimes);
        if (timeChoice == -1) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }
        LocalTime selectedTime = availableTimes.get(timeChoice - 1);
    
        // FORMAT THE DATE IN dd/MM/yyyy
        String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    
        // UPDATE APPOINTMENT DATE AND TIME
        existingAppointment.setDate(formattedDate);
        existingAppointment.setTime(formattedTime);
    
        // WRITE APPOINTMENT TO FILE
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
    
    



// CANCEL APPOINTMENT AND MAKE THE UPDATE TO THE FILE
public void cancelAppointment() {
    // PROMPT USER FOR APPOINTMENT ID
    initialDataAppointments.reloadData();;
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter Appointment ID to cancel: ");
    String appointmentID = scanner.nextLine();

    // FIND IF APPOINTMENT EXISTS        
    Appointment existingAppointment = null;
    try {
        existingAppointment = initialDataAppointments.findAppointment("hms/src/data/Appointments_List.csv", appointmentID);
    } catch (IOException e) {
        e.printStackTrace();
    }

    if (existingAppointment == null) {
        System.out.println("Appointment ID not found.");
        return;
    }

    // CHECK STATUS BEFORE CANCELLING
    if (existingAppointment.getStatus() != AppointmentStatus.PENDING) {
        System.out.println("Cannot cancel appointment. Only pending appointments can be canceled.");
        return;
    }

    System.out.println(existingAppointment.getAppointmentID());
    // CANCEL APPOINTMENT   
    existingAppointment.updateStatus(AppointmentStatus.CANCELLED);

    // WRITE UPDATED APPOINTMENT TO FILE
    try {
        initialDataAppointments.writeData("hms/src/data/Appointments_List.csv", existingAppointment);
        System.out.println("Appointment canceled successfully.");
    } catch (IOException e) {    
        e.printStackTrace();
    }
}


    //UPDATE APPOINTMENT STATUS
    public void acceptOrDeclineAppointment() {
        // PROMPT USER FOR APPOINTMENT ID
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Appointment ID to update status: ");
        String appointmentID = scanner.nextLine();

        // FIND IF APPOINTMENT EXISTS
        Appointment existingAppointment = null;
        try {        
            existingAppointment = initialDataAppointments.findAppointment("hms/src/data/Appointments_List.csv", appointmentID);
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


    public void scheduleAppointment() {
        Scanner scanner = new Scanner(System.in);
    
        // Get the patient ID
        String patientUserID = AuthorizationControl.getCurrentUserId();
    
        // Prompt for doctor ID and validate existence
        System.out.print("Enter Doctor ID: ");
        String doctorID = scanner.nextLine();
        Doctor doctor = findDoctorByID(doctorID);
    
        if (doctor == null) {
            System.out.println("Error: Doctor not found.");
            return;
        }
    
        // Display available dates within the next 7 days
        List<LocalDate> availableDates = getDoctorAvailabilityNext7Days(doctorID);
        if (availableDates.isEmpty()) {
            System.out.println("Doctor not available in the next 7 days.");
            return;
        }
    
        System.out.println("Available dates:");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out.println((i + 1) + ". " + availableDates.get(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    
        // Ask the user to select a date
        System.out.print("Choose a date by entering the corresponding number: ");
        int dateChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (dateChoice < 1 || dateChoice > availableDates.size()) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }
        LocalDate selectedDate = availableDates.get(dateChoice - 1);
    
        // Show available times for the selected date
        List<LocalTime> availableTimes = printAvailableTimes(doctorID, selectedDate);
    
        // Ask the user to select a time
        System.out.print("Choose a time by entering the corresponding number: ");
        int timeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (timeChoice < 1 || timeChoice > availableTimes.size()) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }
        LocalTime selectedTime = availableTimes.get(timeChoice - 1); // Get the selected time
    
        // Confirm the selected time is available
        if (!isTimeAvailable(doctorID, selectedDate, selectedTime)) {
            System.out.println("Error: Selected time is not available. Please try again.");
            return;
        }
    
        // Generate a new appointment ID and create the appointment
        String appointmentID = generateAppointmentID();
        Appointment appointment = new Appointment(appointmentID, patientUserID, doctor.getUserID(), selectedDate.toString(), selectedTime.toString());
    
        // Set default outcome record and prescription
        OutcomeRecord outcomeRecord = new OutcomeRecord("00/00/0000", "-", "-");
        Prescription prescription = new Prescription("-");
        prescription.updateStatus(PrescriptionStatus.PENDING);
        outcomeRecord.addPrescription(prescription);
        appointment.setOutcomeRecord(outcomeRecord);
        appointment.updateStatus(AppointmentStatus.PENDING);
    
        // Write appointment data to file
        try {
            initialDataAppointments.writeAppointmentToFilewithdefaultOutcome("hms/src/data/Appointments_List.csv", appointment);
            System.out.println("Appointment scheduled successfully for " + patientUserID + " with Doctor " + doctor.getUserID() +
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
        LocalDate appointmentDate = LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (appointment.getDoctorID().equals(doctorID) && appointmentDate.isEqual(date)) {
            LocalTime appointmentTime = LocalTime.parse(appointment.getTime());
            bookedTimes.add(appointmentTime);
        }
    }

    // Generate available times based on the doctor's working hours for the specific date
    for (AppointmentSlot slot : slots) {
        LocalTime currentTime = slot.getStartTime().toLocalTime();
        LocalTime endTime = slot.getEndTime().toLocalTime();

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


    // Method to get the next 7 days when the doctor is available
    private List<LocalDate> getDoctorAvailabilityNext7Days(String doctorID) {
        List<LocalDate> availableDates = new ArrayList<>();
        LocalDate today = LocalDate.now();
    
        for (int i = 0; i < 7; i++) {
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
    
    // Method to confirm selected time availability
    private boolean isTimeAvailable(String doctorID, LocalDate date, LocalTime time) {
        // Get all slots for the specified doctor
        List<AppointmentSlot> slots = initialDataAppointmentSlots.getLists().stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID))
                .collect(Collectors.toList());
    
        // Check each slot to see if the desired time falls within the available hours
        for (AppointmentSlot slot : slots) {
            if (time.isAfter(slot.getStartTime().toLocalTime()) && time.isBefore(slot.getEndTime().toLocalTime())) {
                // Check each appointment to see if it's already booked for the specified date and time
                for (Appointment appointment : initialDataAppointments.getLists()) {
                    if (appointment.getDoctorID().equals(doctorID) &&
                        LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).isEqual(date) &&
                        LocalTime.parse(appointment.getTime()).equals(time)) {
    
                        // Check if the appointment status is PENDING or CANCELLED, making it available
                        AppointmentStatus status = appointment.getStatus();
                        if (status == AppointmentStatus.PENDING || status == AppointmentStatus.CANCELLED) {
                            continue; // Ignore this appointment, making the time available
                        } else {
                            return false; // Time is unavailable if status is not PENDING or CANCELLED
                        }
                    }
                }
                return true; // Time is available if no booked appointment conflicts
            }
        }
        return false; // Time is outside of the doctor's available slot hours
    }
    
    
    
    

    public void updatePrescriptionStatus() {
        // PROMPT USER FOR APPOINTMENT ID
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Appointment ID to update prescription status: ");
        String appointmentID = scanner.nextLine();
    
        // FIND IF APPOINTMENT EXISTS
        Appointment existingAppointment = null;
        try {
            existingAppointment =  initialDataAppointments.findAppointment("hms/src/data/Appointments_List.csv", appointmentID);
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
            System.out.println((i + 1) + ". " + prescriptions.get(i).getMedicationName() + " - Current Status: " + prescriptions.get(i).getStatus());
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
        Medicine selectedMedicine =  initialDataMedicine.getLists().stream()
                .filter(medicine -> medicine.getName().equals(medicineName))
                .findFirst()
                .orElse(null);
    
        if (selectedMedicine == null) {
            System.out.println("Medicine not found.");
            return;
        }
    
        // Check stock levels
        if (selectedMedicine.getInitialStock() < selectedMedicine.getLowStockLevelAlert()) {
            System.out.println("Cannot update the prescription status. Low stock level for " + medicineName + ". Current stock: " + selectedMedicine.getInitialStock());
            
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
            Appointment existingAppointment = initialDataAppointments.findAppointment("hms/src/data/Appointments_List.csv", appointmentID);
    
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
            List<Medicine> medicines = initialDataMedicine.getLists(); // Assuming you have a method to get the list of medicines
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
                Prescription prescription = new Prescription(medicationName); // Create prescription with medication name
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
        }catch (IOException e) {
            System.err.println("Error while updating the appointment: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
        }
    }




    
}



