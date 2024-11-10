package appointmentslots;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import appointments.Appointment;
import authorization.AuthorizationControl;
import enums.AppointmentStatus;
import enums.LeaveRequestStatus;
import enums.WorkingDay;
import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataAppointments;
import filereaders.InitialDataStaff;
import filereaders.InitialDataLeaveRequest;
import leave.LeaveRequest;
import lookups.UserLookup;
import users.Doctor;

public class AppointmentSlotManagementControl {
    private InitialDataStaff initialData;
    private InitialDataAppointmentSlots initialDataAppointmentSlots;
    private InitialDataAppointments initialDataAppointments;
    private InitialDataLeaveRequest initialDataLeaveRequest;

    public AppointmentSlotManagementControl(InitialDataStaff initialData,
            InitialDataAppointmentSlots initialDataAppointmentSlots, InitialDataAppointments initialDataAppointments,
            InitialDataLeaveRequest initialDataLeaveRequest) {
        this.initialData = initialData;
        this.initialDataAppointmentSlots = initialDataAppointmentSlots;
        this.initialDataAppointments = initialDataAppointments;
        this.initialDataLeaveRequest = initialDataLeaveRequest;

        initialData.reloadData();
        initialDataAppointmentSlots.reloadData();
        initialDataAppointments.reloadData();
        initialDataLeaveRequest.reloadData();

    }

    // VIEW APPOINTMENT SLOT
    public void viewAppointmentSlots() {
        // Import data only once when the menu option is chosen
        // initialData.importData();

        // Prompt the user to enter a doctor ID
        System.out.print("Enter doctor ID:");
        Scanner scanner = new Scanner(System.in);
        String doctorID = scanner.nextLine().trim(); // Trim any leading/trailing whitespace

        boolean slotFound = false; // Flag to check if any slots were found

        // Iterate through the appointment slots and display those that match the
        // entered doctor ID
        for (AppointmentSlot slot : initialDataAppointmentSlots.getLists()) {
            if (slot.getDoctorID().equalsIgnoreCase(doctorID)) { // Use case-insensitive comparison
                System.out.println(slot.displayData());
                slotFound = true; // Set the flag to true if a slot is found
            }
        }

        // If no slots were found, inform the user
        if (!slotFound) {
            System.out.println("No appointment slots available for Doctor ID: " + doctorID);
        }
    }
    
    public void viewAvailableTimeSlots() {
        initialData.importData();
        Scanner scanner = new Scanner(System.in);
    
        Set<String> doctorIDsWithSlots = initialDataAppointmentSlots.getLists().stream()
                .map(AppointmentSlot::getDoctorID)
                .collect(Collectors.toSet());
    
        Map<String, Doctor> uniqueDoctors = new HashMap<>();
        for (Doctor doctor : initialData.getDoctors()) {
            if (doctorIDsWithSlots.contains(doctor.getUserID())) {
                uniqueDoctors.putIfAbsent(doctor.getUserID(), doctor);
            }
        }
    
        List<Doctor> availableDoctors = new ArrayList<>(uniqueDoctors.values());
    
        if (availableDoctors.isEmpty()) {
            System.out.println("No doctors with available time slots found.");
            return;
        }
    
        System.out.println("Select a doctor by entering the corresponding number (or type 'exit' to cancel):");
        for (int i = 0; i < availableDoctors.size(); i++) {
            System.out.println((i + 1) + ". " + availableDoctors.get(i).getName());
        }
    
        int doctorIndex = -1;
        while (doctorIndex < 0 || doctorIndex >= availableDoctors.size()) {
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                return;
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
    
        List<LocalDate> approvedLeaveDates = initialDataLeaveRequest.getAllRequests().stream()
                .filter(request -> request.getRequesterID().equals(doctorID) &&
                        request.getStatus() == LeaveRequestStatus.APPROVED)
                .map(LeaveRequest::getLeaveDate)
                .collect(Collectors.toList());
    
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(14);
    
        List<LocalDate> availableDates = initialDataAppointmentSlots.getLists().stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID))
                .flatMap(slot -> today.datesUntil(limitDate.plusDays(1))
                        .filter(date -> slot.getWorkingDays().stream()
                                .anyMatch(day -> day.name().equalsIgnoreCase(date.getDayOfWeek().name()))
                                && !approvedLeaveDates.contains(date)))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    
        if (availableDates.isEmpty()) {
            System.out.println("No available dates within the next 14 days for Doctor " + selectedDoctor.getName());
            return;
        }
    
        System.out.println("Select a date from the available dates (or type 'exit' to cancel):");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out.println((i + 1) + ". " + availableDates.get(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    
        int dateIndex = -1;
        while (dateIndex < 0 || dateIndex >= availableDates.size()) {
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                return;
            }
            try {
                dateIndex = Integer.parseInt(input) - 1;
                if (dateIndex < 0 || dateIndex >= availableDates.size()) {
                    System.out.println("Invalid choice. Please select a valid number from the list.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    
        LocalDate selectedDate = availableDates.get(dateIndex);
        System.out.println("Available times on " + selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " for Dr. " + selectedDoctor.getName() + ":");
        printforpatientAvailableTimes(doctorID, selectedDate);
    }
    

    private void printforpatientAvailableTimes(String doctorID, LocalDate date) {
        List<AppointmentSlot> slots = initialDataAppointmentSlots.getLists().stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID))
                .collect(Collectors.toList());
    
        Set<LocalTime> bookedTimes = new HashSet<>();
        for (Appointment appointment : initialDataAppointments.getLists()) {
            if (appointment.getDoctorID().equals(doctorID) &&
                    appointment.getStatus() != AppointmentStatus.CANCELLED &&
                    LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).isEqual(date)) {
    
                LocalTime appointmentTime = LocalTime.parse(appointment.getTime());
                LocalTime endTime = appointmentTime.plusMinutes(30);
    
                LocalTime currentTime = appointmentTime;
                while (currentTime.isBefore(endTime)) {
                    bookedTimes.add(currentTime);
                    currentTime = currentTime.plusMinutes(30);
                }
            }
        }
    
        TreeSet<LocalTime> availableTimes = new TreeSet<>();
        for (AppointmentSlot slot : slots) {
            LocalTime startTime = slot.getStartTime();
            LocalTime endTime = slot.getEndTime();
    
            LocalTime currentTime = startTime;
            while (currentTime.isBefore(endTime)) {
                if (!bookedTimes.contains(currentTime)) {
                    availableTimes.add(currentTime);
                }
                currentTime = currentTime.plusMinutes(30);
            }
        }
    
        if (availableTimes.isEmpty()) {
            System.out.println("No available time slots for the selected date.");
        } else {
            availableTimes.forEach(time -> System.out.println(time.format(DateTimeFormatter.ofPattern("HH:mm"))));
        }
    }
    

    public void viewPersonalSchedule() {
        initialData.importData(); // Ensure the latest data is loaded
        Scanner scanner = new Scanner(System.in);

        // Use the current user as doctor ID
        String doctorID = AuthorizationControl.getCurrentUserId();

        // Validate if the doctor exists
        UserLookup userLookup = new UserLookup();
        Doctor doctor = userLookup.findByID(doctorID, initialData.getDoctors(),
                doc -> doc.getUserID().equals(doctorID));
        if (doctor == null) {
            System.out.println("Doctor ID not found. Please try again.");
            return;
        }

        // Step 1: Display all upcoming accepted appointments for the doctor, ordered by
        // date
        System.out.println("Your Upcoming Accepted Appointments (Nearest First):");
        List<Appointment> upcomingAppointments = getUpcomingAcceptedAppointmentsForDoctor(doctorID);
        if (upcomingAppointments.isEmpty()) {
            System.out.println("You have no upcoming accepted appointments.");
        } else {
            for (Appointment appointment : upcomingAppointments) {
                System.out.println("Appointment with Patient ID: " + appointment.getPatientID() + " on "
                        + appointment.getDate() + " at " + appointment.getTime());
            }
        }

        System.out.println("\n--------------------------------------");

        // Step 2: Display all pending appointment requests for the doctor, ordered by
        // date
        System.out.println("Pending Appointment Requests (Nearest First):");
        List<Appointment> pendingRequests = getPendingRequestsForDoctor(doctorID);
        if (pendingRequests.isEmpty()) {
            System.out.println("You have no pending appointment requests.");
        } else {
            for (Appointment appointment : pendingRequests) {
                System.out.println("Request from Patient ID: " + appointment.getPatientID() + " on "
                        + appointment.getDate() + " at " + appointment.getTime());
            }
        }

        System.out.println("\n--------------------------------------");

        // Step 3: Display available slots for the next 14 days, ordered by date
        System.out.println("Your Availability for the Next 14 Days (Nearest First):");
        displayAvailableSlotsForNext14Days(doctorID);
    }

    // Helper function to retrieve upcoming accepted appointments for the doctor
    private List<Appointment> getUpcomingAcceptedAppointmentsForDoctor(String doctorID) {
        return initialDataAppointments.getLists().stream()
                .filter(app -> app.getDoctorID().equals(doctorID) &&
                        app.getStatus() == AppointmentStatus.ACCEPTED &&
                        LocalDate.parse(app.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                .isAfter(LocalDate.now()))
                .sorted(Comparator
                        .comparing(app -> LocalDate.parse(app.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))) // Sort
                                                                                                                      // by
                                                                                                                      // date
                .collect(Collectors.toList());
    }

    // Helper function to retrieve pending requests for the doctor
    private List<Appointment> getPendingRequestsForDoctor(String doctorID) {
        return initialDataAppointments.getLists().stream()
                .filter(app -> app.getDoctorID().equals(doctorID) &&
                        app.getStatus() == AppointmentStatus.PENDING &&
                        LocalDate.parse(app.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                .isAfter(LocalDate.now()))
                .sorted(Comparator
                        .comparing(app -> LocalDate.parse(app.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))) // Sort
                                                                                                                      // by
                                                                                                                      // date
                .collect(Collectors.toList());
    }

    // Helper function to display available slots for the next 14 days
    private void displayAvailableSlotsForNext14Days(String doctorID) {
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(14);

        List<LocalDate> availableDates = initialDataAppointmentSlots.getLists().stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID))
                .flatMap(slot -> today.datesUntil(limitDate.plusDays(1))
                        .filter(date -> slot.getWorkingDays().stream()
                                .anyMatch(day -> day.name().equalsIgnoreCase(date.getDayOfWeek().name()))))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (LocalDate date : availableDates) {
            System.out.println("Date: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            printAvailableTimes(doctorID, date);
            System.out.println("--------------------------------------");
        }
    }

    // Helper function to print available times for a given doctor and date
    private void printAvailableTimes(String doctorID, LocalDate date) {
        List<AppointmentSlot> slots = initialDataAppointmentSlots.getLists().stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID))
                .collect(Collectors.toList());

        Set<LocalTime> bookedTimes = new HashSet<>();

        for (Appointment appointment : initialDataAppointments.getLists()) {
            if (appointment.getDoctorID().equals(doctorID) &&
                    appointment.getStatus() != AppointmentStatus.CANCELLED &&
                    LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).isEqual(date)) {

                LocalTime appointmentTime = LocalTime.parse(appointment.getTime());
                LocalTime endTime = appointmentTime.plusMinutes(30); // Assuming a 30-minute duration

                LocalTime currentTime = appointmentTime;
                while (currentTime.isBefore(endTime)) {
                    bookedTimes.add(currentTime);
                    currentTime = currentTime.plusMinutes(30);
                }
            }
        }

        TreeSet<LocalTime> availableTimes = new TreeSet<>();

        for (AppointmentSlot slot : slots) {
            LocalTime startTime = slot.getStartTime();
            LocalTime endTime = slot.getEndTime();

            LocalTime currentTime = startTime;
            while (currentTime.isBefore(endTime)) {
                if (!bookedTimes.contains(currentTime)) {
                    availableTimes.add(currentTime);
                }
                currentTime = currentTime.plusMinutes(30);
            }
        }

        if (availableTimes.isEmpty()) {
            System.out.println("No available time slots for the selected date.");
        } else {
            availableTimes.forEach(time -> System.out.println(time.format(DateTimeFormatter.ofPattern("HH:mm"))));
        }
    }

    private boolean isDoctorAvailable(String doctorID, LocalDate date) {
        boolean isAvailable = true; // Assume the doctor is available unless proven otherwise

        // Loop through appointment slots for the doctor
        for (AppointmentSlot slot : initialDataAppointmentSlots.getLists()) {
            if (slot.getDoctorID().equals(doctorID)) {

                // Define the working days for this slot
                List<WorkingDay> workingDays = slot.getWorkingDays();

                // Check if the requested date is within the next 14 days and matches a working
                // day
                if (date.isAfter(LocalDate.now().minusDays(1)) && date.isBefore(LocalDate.now().plusDays(14)) &&
                        workingDays.stream().anyMatch(day -> day.name().equalsIgnoreCase(date.getDayOfWeek().name()))) {

                    // Convert the slot's LocalTime values to LocalDateTime for the given date
                    LocalDateTime slotStart = LocalDateTime.of(date, slot.getStartTime());
                    LocalDateTime slotEnd = LocalDateTime.of(date, slot.getEndTime());

                    // Check for existing appointments on that date
                    for (Appointment appointment : initialDataAppointments.getLists()) {
                        LocalDate appointmentDate;
                        try {
                            // Parse appointment date
                            appointmentDate = LocalDate.parse(appointment.getDate(),
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")); // Adjust pattern as needed
                        } catch (DateTimeParseException e) {
                            System.out.println("Error parsing appointment date: " + appointment.getDate());
                            continue; // Skip this appointment if there's a parsing error
                        }

                        // Check if an appointment conflicts with this slot
                        if (appointment.getDoctorID().equals(doctorID) &&
                                appointmentDate.isEqual(date) &&
                                appointment.getStatus() != AppointmentStatus.CANCELLED) {
                            isAvailable = false; // Mark as unavailable due to an existing appointment
                            break; // Exit if found an appointment conflict
                        }
                    }
                } else {
                    // If not a working day or out of the date range, set as unavailable
                    isAvailable = false;
                }
            }
        }
        return isAvailable;
    }

    public void setDoctorAvailability() {
        Scanner scanner = new Scanner(System.in);
        String doctorID = AuthorizationControl.getCurrentUserId();

        // Check if the doctor has specified any availability
        List<AppointmentSlot> existingSlots = initialDataAppointmentSlots.getLists();
        AppointmentSlot currentSlot = existingSlots.stream()
                .filter(slot -> slot.getDoctorID().equals(doctorID))
                .findFirst()
                .orElse(null);

        boolean hasAvailability = currentSlot != null;

        if (hasAvailability) {
            // If availability exists, ask if they want to update
            System.out.print("You already have specified availability. Do you want to update it? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("n")) {
                System.out.println("No changes made to your availability.");
                return; // Exit method
            }
            System.out.println("Updating your availability...");
        } else {
            System.out.println("You have not specified any availability. Setting your availability now...");
        }

        // Input for Start Time with validation, show current if exists
        LocalTime startTime = null;
        while (true) {
            System.out.print("Enter Start Time (HH:mm, e.g., 09:00), if no change then press <Enter> [" +
                    (hasAvailability ? currentSlot.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "None")
                    +
                    "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() && hasAvailability) {
                startTime = currentSlot.getStartTime(); // Keep current start time if Enter is pressed
                break;
            }
            try {
                startTime = LocalTime.parse(input, DateTimeFormatter.ofPattern("HH:mm"));
                break; // Exit loop if input is valid
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid time format. Please use 'HH:mm' format.");
            }
        }

        // Input for End Time with validation, show current if exists
        LocalTime endTime = null;
        while (true) {
            System.out.print("Enter End Time (HH:mm, e.g., 17:00), if no change then press <Enter> [" +
                    (hasAvailability ? currentSlot.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "None") +
                    "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() && hasAvailability) {
                endTime = currentSlot.getEndTime(); // Keep current end time if Enter is pressed
                break;
            }
            try {
                endTime = LocalTime.parse(input, DateTimeFormatter.ofPattern("HH:mm"));
                if (endTime.isAfter(startTime)) {
                    break; // Exit loop if input is valid and endTime is after startTime
                } else {
                    System.out.println("Error: End time must be after start time.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid time format. Please use 'HH:mm' format.");
            }
        }

        // Display each day and allow the doctor to mark "not working" days
        List<WorkingDay> workingDays = new ArrayList<>(
                currentSlot != null ? currentSlot.getWorkingDays() : new ArrayList<>());
        System.out.println(
                "Select the days you will be working. For days you're not working, type 'n'. Press Enter to confirm if working.");

        for (WorkingDay day : WorkingDay.values()) {
            System.out.print(day.name() + " (Enter to confirm working, 'n' if not working): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("n")) {
                workingDays.remove(day); // Remove from working days if 'n' is entered
            } else if (!workingDays.contains(day)) {
                workingDays.add(day); // Add to working days if confirmed as working
            }
        }

        if (workingDays.isEmpty()) {
            System.out.println("No working days selected. Availability not updated.");
            return;
        }

        // Create and save the appointment slot
        AppointmentSlot newSlot = new AppointmentSlot(doctorID, startTime, endTime, workingDays);
        if (hasAvailability) {
            // Replace old slot if exists
            existingSlots.removeIf(slot -> slot.getDoctorID().equals(doctorID));
        }
        initialDataAppointmentSlots.getLists().add(newSlot);

        // Save to CSV after adding
        try {
            initialDataAppointmentSlots.saveAppointmentSlots("hms/src/data/Appointment_Slots.csv");
            System.out.println("\nAppointment slot saved successfully.");

            // Display the newly updated availability slot for the current doctor
            System.out.println("\nUpdated Availability Slot for Dr. " + doctorID + ":");
            System.out.println("--------------------------------------------------");
            System.out.println("Start Time     : " + startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            System.out.println("End Time       : " + endTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            System.out.println(
                    "Working Days   : " + workingDays.stream().map(Enum::name).collect(Collectors.joining(", ")));
            System.out.println("--------------------------------------------------");
        } catch (IOException e) {
            System.out.println("Error saving appointment slots: " + e.getMessage());
        }
    }

}
