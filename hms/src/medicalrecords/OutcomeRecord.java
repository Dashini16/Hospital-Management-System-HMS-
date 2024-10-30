package medicalrecords;
import medicinemanagements.Medicine;

import java.util.ArrayList;
import java.util.List;

public class OutcomeRecord {
    private String dateOfAppointment;
    private String serviceType;
    private List<Medicine> prescriptions;
    private String consultationNotes;

    public OutcomeRecord() {
        this.prescriptions = new ArrayList<>();
    }

    public void setDateOfAppointment(String date) {
        this.dateOfAppointment = date;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void addPrescription(Medicine medication) {
        this.prescriptions.add(medication);
    }

    public void setConsultationNotes(String notes) {
        this.consultationNotes = notes;
    }

    public List<Medicine> getPrescriptions() {
        return prescriptions;
    }

    @Override
    public String toString() {
        return "Date: " + dateOfAppointment + ", Service: " + serviceType + "\nPrescriptions: " + prescriptions + "\nNotes: " + consultationNotes;
    }
}
