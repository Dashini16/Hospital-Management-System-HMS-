package appointments;

import enums.AppointmentStatus;
import medicalrecords.OutcomeRecord;


public class Appointment {
    private String appointmentID;
    private String patientID;
    private String doctorID;
    private String date;
    private String time;
    private AppointmentStatus status;
    private OutcomeRecord outcomeRecord;

    public Appointment(String appointmentID, String patientID, String doctorID, String date, String time) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.date = date;
        this.time = time;
        this.status = status.PENDING;
        this.outcomeRecord = null;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getPatientID() {
        return patientID;
    }   

    public String getDoctorID() {

        return doctorID;
    }   

    public String getDate() {
        return date;
    }   
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;           
    }
    public AppointmentStatus getStatus() {
        return status;
    }



    public void updateStatus(AppointmentStatus newStatus) {
        this.status = newStatus;
    }

    public OutcomeRecord getOutcomeRecord() {
        return outcomeRecord;
    }

    public void setOutcomeRecord(OutcomeRecord outcomeRecord) {
        this.outcomeRecord = outcomeRecord;
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentID + 
               ", Patient ID: " + patientID + 
               ", Doctor ID: " + doctorID + 
               ", Date: " + date + 
               ", Time: " + time + 
               ", Status: " + status +
               "\nOutcome Record:\n" + (outcomeRecord != null ? outcomeRecord.toString() : "No outcome recorded yet.");
    }
}
