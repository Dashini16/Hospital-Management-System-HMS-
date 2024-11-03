package medicalrecords;
import appointments.Prescription;

import java.util.List;
import java.util.ArrayList;


public class MedicalRecord {
    private String patientID;
    private List<Diagnosis> diagnoses;
    private List<Treatment> treatments;
    private List<Prescription> prescriptions;
    

    public MedicalRecord(String patientID) {
        this.patientID = patientID;
        this.diagnoses = new ArrayList<>();
        this.treatments = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
    }

    public void addDiagnosis(Diagnosis diagnosis) {
        diagnoses.add(diagnosis);
    }

    public void addTreatment(Treatment treatment) {
        treatments.add(treatment);
    }

    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
    }

    public String getPatientID() {
        return patientID;
    }

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }   

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }   


    @Override
    public String toString() {
        return "Medical Record for Patient ID: " + patientID + "\nDiagnoses: " + diagnoses + "\nTreatments: " + treatments + "\nPrescriptions: " + prescriptions;
    }
}
