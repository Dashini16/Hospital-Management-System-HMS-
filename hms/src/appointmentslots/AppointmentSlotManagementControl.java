package appointmentslots;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import appointments.Appointment;
import authorization.AuthorizationControl;
import enums.AppointmentStatus;
import enums.WorkingDay;
import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataAppointments;
import filereaders.InitialDataStaff;
import lookups.UserLookup;
import users.Doctor;

public class AppointmentSlotManagementControl {
        private InitialDataStaff initialData;
        private InitialDataAppointmentSlots initialDataAppointmentSlots;
        private InitialDataAppointments initialDataAppointments;

    public AppointmentSlotManagementControl(InitialDataStaff initialData, InitialDataAppointmentSlots initialDataAppointmentSlots, InitialDataAppointments initialDataAppointments) {
        this.initialData = initialData;
        this.initialDataAppointmentSlots = initialDataAppointmentSlots;
        this.initialDataAppointments = initialDataAppointments;
        initialData.reloadData();
        initialDataAppointmentSlots.reloadData();
        initialDataAppointments.reloadData();
        
    }
     //VIEW APPOINTMENT SLOT
     public void viewAppointmentSlots() {
        // Import data only once when the menu option is chosen
        //initialData.importData();
    
        // Prompt the user to enter a doctor ID
        System.out.print("Enter doctor ID:");
        Scanner scanner = new Scanner(System.in);
        String doctorID = scanner.nextLine().trim(); // Trim any leading/trailing whitespace
    
        boolean slotFound = false; // Flag to check if any slots were found
    
        // Iterate through the appointment slots and display those that match the entered doctor ID
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

        // Prompt for doctor ID     
        System.out.print("Enter doctor ID: ");
        String doctorID = scanner.nextLine().trim();
    
        // Validate if the doctor exists
        //Doctor doctor = findDoctorByID(doctorID);
        UserLookup userLookup = new UserLookup();
        Doctor doctor = userLookup.findByID(doctorID, initialData.getDoctors(), doc -> doc.getUserID().equals(doctorID));
        if (doctor == null) {
            System.out.println("Doctor ID not found. Please try again.");
            return;
        }
    
        // Prompt for appointment date
        LocalDate appointmentDate = promptForDate(scanner);
    
        // Check if the doctor is available for the selected date
        boolean isAvailable = isDoctorAvailable(doctorID, appointmentDate);
        if (!isAvailable) {
            System.out.println("Doctor is not available on this date.");
        } else {
            System.out.println("Doctor is available for the requested appointment, "+ appointmentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            // Print available times
            printforpatientAvailableTimes(doctorID, appointmentDate);
        }
    }

























    


  // Method to print available times for a specific doctor and date
private void printforpatientAvailableTimes(String doctorID, LocalDate date) {
    System.out.println("Available times on " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ":");
    
    // Get the doctor's working slots
    List<AppointmentSlot> slots = initialDataAppointmentSlots.getLists().stream()
            .filter(slot -> slot.getDoctorID().equals(doctorID))
            .collect(Collectors.toList());
    
    // Map to store booked times and their corresponding appointment IDs
    Map<LocalTime, String> bookedTimes = new HashMap<>();
    
    // Gather booked times on the specified date along with their end times
    for (Appointment appointment : initialDataAppointments.getLists()) {
        if (appointment.getDoctorID().equals(doctorID) && 
            appointment.getStatus() != AppointmentStatus.CANCELLED &&
            LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).isEqual(date)) {
            
            LocalTime appointmentTime = LocalTime.parse(appointment.getTime()); // Ensure appointment.getTime() returns a String time
            
            // Store the appointment time along with its ID
            bookedTimes.put(appointmentTime, appointment.getAppointmentID()); // Assuming getAppointmentID() returns the ID
            
            // Calculate the end time of the appointment (assuming a default duration, e.g., 30 minutes)
            LocalTime endTime = appointmentTime.plusMinutes(30); // Adjust duration as needed
            
            // Mark all times from appointmentTime to endTime as booked
            LocalTime currentTime = appointmentTime;
            while (currentTime.isBefore(endTime)) {
                bookedTimes.put(currentTime, appointment.getAppointmentID()); // Map the current time to its appointment ID
                currentTime = currentTime.plusMinutes(30); // Increment by your desired time slot
            }
        }
    }

    // Print booked times for debugging purposes
    //System.out.println("Booked times with IDs: " + bookedTimes);

    // Set to collect available times
    Set<LocalTime> availableTimes = new HashSet<>();

    // Iterate through the working slots and determine available times
    for (AppointmentSlot slot : slots) {
        LocalTime startTime = slot.getStartTime().toLocalTime();
        LocalTime endTime = slot.getEndTime().toLocalTime();

        // Loop through the time slots (e.g., every half hour) between start and end times
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            if (!bookedTimes.containsKey(currentTime)) {
                availableTimes.add(currentTime); // Collect available time
            }
            currentTime = currentTime.plusMinutes(30); // Increment by your desired time slot
        }
    }
    // Print the available times without duplicates
    for (LocalTime time : availableTimes) {
        System.out.println(time);
    }


}  


    public void viewPersonalSchedule() {
        initialData.importData();
        Scanner scanner = new Scanner(System.in);
    
        // Use current user as doctor ID
        String doctorID = AuthorizationControl.getCurrentUserId();
    
        // Validate if the doctor exists
        //Doctor doctor = findDoctorByID(doctorID);
        UserLookup userLookup = new UserLookup();
        Doctor doctor = userLookup.findByID(doctorID, initialData.getDoctors(), doc -> doc.getUserID().equals(doctorID));
        if (doctor == null) {
            System.out.println("Doctor ID not found. Please try again.");
            return;
        }
    
        // Prompt for appointment date
        LocalDate appointmentDate = promptForDate(scanner);
    
        // Check if the doctor is available for the selected date
        boolean isAvailable = isDoctorAvailable(doctorID, appointmentDate);
        if (!isAvailable) {
            System.out.println("You have no appointments booked on this day.");
        } else {
            //System.out.println("You have no appointments booked on this day.");
            // Print available times
            printAvailableTimes(doctorID, appointmentDate);
        }
    }
    
    
// Method to prompt for a valid date
private LocalDate promptForDate(Scanner scanner) {
    while (true) {
        System.out.print("Enter Appointment Date (dd/MM/yyyy): ");
        String dateStr = scanner.nextLine().trim();
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please enter the date in dd/MM/yyyy format.");
        }
    }
}

// Method to print available times for a specific doctor and date
private void printAvailableTimes(String doctorID, LocalDate date) {
    //System.out.println("Hi " + doctorID + ",   Your schedule on " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " as such:");
    
    // Get the doctor's working slots
    List<AppointmentSlot> slots = initialDataAppointmentSlots.getLists().stream()
            .filter(slot -> slot.getDoctorID().equals(doctorID))
            .collect(Collectors.toList());
    
    // Map to store booked times and their corresponding appointment IDs
    Map<LocalTime, String> bookedTimes = new HashMap<>();
    
    // Gather booked times on the specified date along with their end times
    for (Appointment appointment : initialDataAppointments.getLists()) {
        if (appointment.getDoctorID().equals(doctorID) && 
            appointment.getStatus() != AppointmentStatus.CANCELLED &&
            LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).isEqual(date)) {
            
            LocalTime appointmentTime = LocalTime.parse(appointment.getTime()); // Ensure appointment.getTime() returns a String time
            
            // Store the appointment time along with its ID
            bookedTimes.put(appointmentTime, appointment.getAppointmentID()); // Assuming getAppointmentID() returns the ID
            
            // Calculate the end time of the appointment (assuming a default duration, e.g., 30 minutes)
            LocalTime endTime = appointmentTime.plusMinutes(30); // Adjust duration as needed
            
            // Mark all times from appointmentTime to endTime as booked
            LocalTime currentTime = appointmentTime;
            while (currentTime.isBefore(endTime)) {
                bookedTimes.put(currentTime, appointment.getAppointmentID()); // Map the current time to its appointment ID
                currentTime = currentTime.plusMinutes(30); // Increment by your desired time slot
            }
        }
    }

    // Print booked times for debugging purposes
    //System.out.println("Booked times with IDs: " + bookedTimes);

    // Set to collect available times
    Set<LocalTime> availableTimes = new HashSet<>();

    // Iterate through the working slots and determine available times
    for (AppointmentSlot slot : slots) {
        LocalTime startTime = slot.getStartTime().toLocalTime();
        LocalTime endTime = slot.getEndTime().toLocalTime();

        // Loop through the time slots (e.g., every half hour) between start and end times
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            if (!bookedTimes.containsKey(currentTime)) {
                availableTimes.add(currentTime); // Collect available time
            }
            currentTime = currentTime.plusMinutes(30); // Increment by your desired time slot
        }
    }

    // Print the available times without duplicates
    //for (LocalTime time : availableTimes) {
        //System.out.println(time);
    //}

    // Print booked times with their IDs
    for (Map.Entry<LocalTime, String> entry : bookedTimes.entrySet()) {
        System.out.println("Booked Time: " + entry.getKey() + ", Appointment ID: " + entry.getValue());
    }
}






private boolean isDoctorAvailable(String doctorID, LocalDate date) {
    boolean isAvailable = true; // Assume the doctor is available unless proven otherwise

    // Get the working slots of the doctor
    for (AppointmentSlot slot : initialDataAppointmentSlots.getLists()) {
        if (slot.getDoctorID().equals(doctorID)) {
            // Log the details of the current appointment slot being checked
            //System.out.println("Checking availability for slot: " + slot.displayData());

            // Check if the requested date is within the appointment slot date range
            if (date.isEqual(slot.getStartTime().toLocalDate()) || date.isEqual(slot.getEndTime().toLocalDate())) {
                // Get the day of the week for the requested appointment
                DayOfWeek appointmentDay = date.getDayOfWeek();

                // Directly access the working days from the slot
                List<WorkingDay> workingDays = slot.getWorkingDays();
                //System.out.println("Requested Day: " + appointmentDay);
                //System.out.println("Working Days: " + workingDays);

                // Check if the appointment day corresponds to any working day
                if (workingDays.stream().anyMatch(day -> day.name().equalsIgnoreCase(appointmentDay.name()))) {
                    // Check for existing appointments on that date
                    for (Appointment appointment : initialDataAppointments.getLists()) {
                        LocalDate appointmentDate;

                        // Assuming the getDate() method returns a String; convert to LocalDate
                        try {
                            appointmentDate = LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")); // Adjust the pattern as needed
                        } catch (DateTimeParseException e) {
                            System.out.println("Error parsing appointment date: " + appointment.getDate());
                            continue; // Skip this appointment if there's a parsing error
                        }

                        if (appointment.getDoctorID().equals(doctorID) &&
                                appointmentDate.isEqual(date) &&
                                appointment.getStatus() != AppointmentStatus.CANCELLED) {
                            isAvailable = false; // Found an existing appointment, set availability to false
                           // System.out.println("Doctor has an existing appointment on this date.");
                            break; // Exit loop if found an existing appointment
                        }
                    }
                } else {
                    //System.out.println("The appointment day does not match the doctor's working days.");
                    isAvailable = false; // Not a working day, set availability to false
                }
            }
        }
    }
    return isAvailable; // Return availability status
}









public void setDoctorAvailability() {
    Scanner scanner = new Scanner(System.in);
    String doctorID = AuthorizationControl.getCurrentUserId();
    
    // Check if the doctor has specified any availability
    List<AppointmentSlot> existingSlots = initialDataAppointmentSlots.getLists();
    boolean hasAvailability = existingSlots.stream().anyMatch(slot -> slot.getDoctorID().equals(doctorID));

    if (hasAvailability) {
        // If availability exists, ask if they want to update
        System.out.print("You already have specified availability. Do you want to update it? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("n")) {
            System.out.println("No changes made to your availability.");
            return; // Exit method
        }
        // If they choose to update, we proceed to get new times
        System.out.println("Updating your availability...");
    } else {
        System.out.println("You have not specified any availability. Setting your availability now...");
    }

    // Input for Start Time with validation
    LocalDateTime startTime;
    while (true) {
        try {
            System.out.print("Enter Start Time (yyyy-MM-dd'T'HH:mm): ");
            startTime = LocalDateTime.parse(scanner.nextLine().trim());

            // Check if startTime is in the future
            if (startTime.isBefore(LocalDateTime.now())) {
                System.out.println("Error: Start time must be in the future.");
                continue;
            }
            break; // Exit loop if input is valid
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date/time format. Please use 'yyyy-MM-dd'T'HH:mm'.");
        }
    }

    // Input for End Time with validation
    LocalDateTime endTime;
    while (true) {
        try {
            System.out.print("Enter End Time (yyyy-MM-dd'T'HH:mm): ");
            endTime = LocalDateTime.parse(scanner.nextLine().trim());

            // Check if endTime is after startTime
            if (endTime.isBefore(startTime)) {
                System.out.println("Error: End time must be after start time.");
                continue;
            }

            // Check if endTime is in the future
            if (endTime.isBefore(LocalDateTime.now())) {
                System.out.println("Error: End time must be in the future.");
                continue;
            }
            break; // Exit loop if input is valid
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date/time format. Please use 'yyyy-MM-dd'T'HH:mm'.");
        }
    }
/* 
    // Check for overlapping appointment slots
    for (AppointmentSlot slot : existingSlots) {
        if (slot.getDoctorID().equals(doctorID) && 
            !(endTime.isBefore(slot.getStartTime()) || startTime.isAfter(slot.getEndTime()))) {
            System.out.println("Error: The new time slot overlaps with an existing appointment slot.");
            return; // Exit method or prompt for new input
        }
    }*/

    // Prompt for working days
    List<WorkingDay> workingDays = new ArrayList<>();
    System.out.print("Enter working days (e.g., MONDAY,TUESDAY, etc.), separated by commas: ");
    String[] daysInput = scanner.nextLine().split(",");
    for (String day : daysInput) {
        workingDays.add(WorkingDay.valueOf(day.trim().toUpperCase())); // Convert input to WorkingDay enum
    }

    // Create and save the appointment slot
    AppointmentSlot newSlot = new AppointmentSlot(doctorID, startTime, endTime, workingDays);
    if (hasAvailability) {
        // If doctor has existing availability, we can replace the old slot (or modify as needed)
        // Here, simply removing the old slots and adding the new one
        existingSlots.removeIf(slot -> slot.getDoctorID().equals(doctorID));
    }
    initialDataAppointmentSlots.getLists().add(newSlot);

    // Save to CSV after adding
    try {
        initialDataAppointmentSlots.saveAppointmentSlots("hms/src/data/Appointment_Slots.csv");
        System.out.println("Appointment slot saved successfully.");
    } catch (IOException e) {
        System.out.println("Error saving appointment slots: " + e.getMessage());
    }
}

}
