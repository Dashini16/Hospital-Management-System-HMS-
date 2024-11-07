package medicalrecords;
import medicalrecords.Prescription;

import java.util.List;
import java.util.ArrayList;


public class MedicalRecord {
    private String patientID;
    private List<String> diagnoses;
    private List<String> treatments;
    //private List<Prescription> prescriptions;

    public MedicalRecord(String patientID) {
        this.patientID = patientID;
        this.diagnoses = new ArrayList<>();
        this.treatments = new ArrayList<>();
        //this.prescriptions = new ArrayList<>();
    }

    public void addDiagnosis(String diagnosis) {
        diagnoses.add(diagnosis);
    }

    public void addTreatment(String treatment) {
        treatments.add(treatment);
    }

    //public void addPrescription(Prescription prescription) {
        //prescriptions.add(prescription);
    //}


    public String getPatientID() {
        return patientID;
    }

    public List<String> getDiagnoses() {
        return diagnoses;
    }   

    public List<String> getTreatments() {
        return treatments;
    }

    //setters

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public void setDiagnoses(List<String> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public void setTreatments(List<String> treatments) {
        this.treatments = treatments;
    }
    //public List<Prescription> getPrescriptions() {
        //return prescriptions;
    //}   

    @Override
    public String toString() {
        return "Patient ID: " + patientID + ", Diagnoses: " + getDiagnoses() + ", " + ", Treatments: " + getTreatments();//+ ", Prescriptions: " + getPrescriptions();
    }

}
