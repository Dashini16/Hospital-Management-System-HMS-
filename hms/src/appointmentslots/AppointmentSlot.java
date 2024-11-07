package appointmentslots;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import enums.WorkingDay;

public class AppointmentSlot {
    private String doctorID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<WorkingDay> workingDays; // List of working days
    //private static List<AppointmentSlot> appointmentSlots = new ArrayList<>(); // Static list to hold all appointment slots

    public AppointmentSlot(String doctorID, LocalDateTime startTime, LocalDateTime endTime, List<WorkingDay> workingDays) {
        this.doctorID = doctorID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workingDays = workingDays;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<WorkingDay> getWorkingDays() {
        return workingDays;
    }


    @Override
    public String toString() {
        String workingDaysString = workingDays.stream()
    .map(WorkingDay::toString) // Convert each WorkingDay to its string representation
    .collect(Collectors.joining(";")); // Join them with a comma
    return  doctorID + ","+ startTime +  ","+endTime +   ","+workingDaysString;
    //"doctorID + "," + startTime + "," + endTime + "," + workingDaysString.toString();
    }

    public String displayData()
    {
        return "Doctor ID: " + doctorID + ", Start Time: " + startTime + ", End Time: " + endTime + ", Working Days: " + workingDays.toString();
    }
    
}