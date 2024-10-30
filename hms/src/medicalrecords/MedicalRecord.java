package medicalrecords;
import medicinemanagements.Medicine;

import java.util.List;
import java.util.ArrayList;


class MedicalRecord {
    private String patientID;
    private List<String> diagnoses;
    private List<String> treatments;
    private List<Medicine> prescriptions;
    private String prescriptionStatus;

    public MedicalRecord(String patientID) {
        this.patientID = patientID;
        this.diagnoses = new ArrayList<>();
        this.treatments = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        this.prescriptionStatus = "Pending";
    }

    public void addDiagnosis(String diagnosis) {
        diagnoses.add(diagnosis);
    }

    public void addTreatment(String treatment) {
        treatments.add(treatment);
    }

    public void addPrescription(Medicine prescription) {
        prescriptions.add(prescription);
    }

    public void setPrescriptionStatus(String status) {
        this.prescriptionStatus = status;
    }

    public String getPrescriptionStatus() {
        return prescriptionStatus;
    }

    public String getPatientID() {
        return patientID;
    }

    public List<String> getDiagnoses() {
        return diagnoses;
    }   

    public List<String> getTreatments() {
        return treatments;
    }

    public List<Medicine> getPrescriptions() {
        return prescriptions;
    }   


    @Override
    public String toString() {
        return "Medical Record for Patient ID: " + patientID + "\nDiagnoses: " + diagnoses + "\nTreatments: " + treatments + "\nPrescriptions: " + prescriptions;
    }
}
