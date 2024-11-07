package medicalrecords;

import java.util.ArrayList;
import java.util.List;

import enums.PrescriptionStatus;

public class OutcomeRecord {
    private String dateOfAppointment;
    private String serviceType;
    private List<Prescription> prescriptions;
    private String consultationNotes;
    //private String patientID;

    public OutcomeRecord(String dateOfAppointment, String serviceType, String consultationNotes) {

        this.dateOfAppointment = dateOfAppointment;
        this.serviceType = serviceType;
        this.consultationNotes = consultationNotes;
        this.prescriptions = new ArrayList<>();

    }

    // Method to update prescription status
    public void updatePrescriptionStatus(String medicationName, PrescriptionStatus newStatus) {
        for (Prescription prescription : prescriptions) {
            if (prescription.getMedicationName().equals(medicationName)) {
                prescription.updateStatus(newStatus);
                break;
            }
        }
    }

    //public String getPatientID() {
      //  return patientID;
    //}
    //public void setPatientID(String patientID) {
        //this.patientID = patientID;
    //}
    public void setDateOfAppointment(String date) {
        this.dateOfAppointment = date;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public String getDateOfAppointment() {  
        return dateOfAppointment;
    }

    public String getServiceType() {
        return serviceType;
    }   

    public void setConsultationNotes(String notes) {
        this.consultationNotes = notes;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    @Override
    public String toString() {
        StringBuilder prescriptionDetails = new StringBuilder();
        for (Prescription prescription : prescriptions) {
            prescriptionDetails.append("\n  - ").append(prescription.toString());
        }
        return "Date: " + dateOfAppointment + ", Service: " + serviceType +
                "\nPrescriptions:" + prescriptionDetails +
                "\nConsultation Notes: " + consultationNotes;
    }
}
