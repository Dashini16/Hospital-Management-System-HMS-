package appointments;
import enums.AppointmentStatus;

public class Appointment {
    private String appointmentID;
    private String patientID;
    private String doctorID;
    private String date;
    private String time;
    private AppointmentStatus status;
    private AppointmentOutcome outcomeRecord;

    public Appointment(String appointmentID, String patientID, String doctorID, String date, String time) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.date = date;
        this.time = time;
        this.status = AppointmentStatus.PENDING ;
        this.outcomeRecord = new AppointmentOutcome(appointmentID);
    }

    public String getDate(){
        return this.date;
    }
    public String getTime(){
        return this.time;
    }

    public void updateSlot(String d, String t){
        this.date = d;
        this.time = t;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void updateStatus(AppointmentStatus newStatus) {
        this.status = newStatus;
    }

    public AppointmentOutcome getOutcomeRecord() {
        return outcomeRecord;
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentID + ", Patient ID: " + patientID + ", Doctor ID: " + doctorID +
                ", Date: " + date + ", Time: " + time + ", Status: " + status;
    }
}
