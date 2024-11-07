package filereaders;


import appointmentslots.*;
import enums.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;




public class InitialDataAppointmentSlots implements ListInterface<AppointmentSlot>, DataImporter,DateFormatterInterface {
    private List<AppointmentSlot> appointmentSlots;


    public InitialDataAppointmentSlots() {
        appointmentSlots = new ArrayList<>();

    }

    @Override
    public void importData() {
        try {


            importDatafromFile("hms/src/data/Appointment_Slots.csv");


        } catch (IOException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
    }

    @Override
    public void reloadData() {
        appointmentSlots.clear();

        // Reload data from files
        importData(); 
        //System.out.println("Data reloaded successfully.");
    }
    @Override
    public DateTimeFormatter createDateFormatter() {
        return new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("d/M/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("d/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .toFormatter();
    }


public void saveAppointmentSlots(String filename) throws IOException {
    File file = new File(filename);
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) { 

        bw.write("DoctorID,StartTime,EndTime,WorkingDays\n"); // Write header only if the file doesn't exist

        for (AppointmentSlot slot : appointmentSlots) {
            System.out.println(slot.toString());
            bw.write(slot.toString()+"\n");  
        }
    }
}

@Override
public void importDatafromFile(String filename) throws IOException {
    // Clear existing slots to prevent duplicates
    

    Set<AppointmentSlot> uniqueSlots = new HashSet<>(); // Temporarily hold unique slots

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
        String line;
        br.readLine(); // Skip header line
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length >= 4) {
                String doctorID = data[0].trim();
                
                // Parse start and end times
                LocalDateTime startTime = LocalDateTime.parse(data[1].trim());
                LocalDateTime endTime = LocalDateTime.parse(data[2].trim());

                // Parse working days
                String[] workingDaysArray = data[3].trim().split(";");
                List<WorkingDay> workingDaysList = new ArrayList<>();
                for (String day : workingDaysArray) {
                    try {
                        workingDaysList.add(WorkingDay.valueOf(day.trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid working day: " + day + ". Skipping...");
                    }
                }

                // Create the AppointmentSlot object
                AppointmentSlot slot = new AppointmentSlot(doctorID, startTime, endTime, workingDaysList);
                //System.out.println(slot.getWorkingDays());

                // Add to the set, which prevents duplicates
                if (!uniqueSlots.add(slot)) {
                    System.out.println("Duplicate slot found for Doctor ID: " + doctorID + ", skipping.");
                }
            }
        }
    }

    // Add all unique slots to the main list
    appointmentSlots.addAll(uniqueSlots);
    //System.out.println("Unique slots imported: " + uniqueSlots.size());
}


    
@Override
    public  List<AppointmentSlot> getLists() {
        return appointmentSlots;
    }
}
