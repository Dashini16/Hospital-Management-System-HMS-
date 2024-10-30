import java.util.List;
import java.util.ArrayList;


class Appointment {
    private String appointmentID;
    private String patientID;
    private String doctorID;
    private String date;
    private String time;
    private String status;
    private OutcomeRecord outcomeRecord;

    public Appointment(String appointmentID, String patientID, String doctorID, String date, String time) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.date = date;
        this.time = time;
        this.status = "Pending";
        this.outcomeRecord = new OutcomeRecord();
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getStatus() {
        return status;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    public OutcomeRecord getOutcomeRecord() {
        return outcomeRecord;
    }

    @Override
    public String toString() {
        return "Appointment ID: " + appointmentID + ", Patient ID: " + patientID + ", Doctor ID: " + doctorID +
                ", Date: " + date + ", Time: " + time + ", Status: " + status;
    }
}
