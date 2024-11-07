package medicalrecords;

import appointments.Appointment;
import enums.PrescriptionStatus;

public class Prescription{

    private String medicationName;
    private PrescriptionStatus status;

    // Constructor to initialize with medication name, setting status to "Pending" by default
    public Prescription(String medicationName) {

        this.medicationName = medicationName;
        this.status = PrescriptionStatus.PENDING;
    }


    // Getter for medication name
    public String getMedicationName() {
        return medicationName;
    }

// Setter for medication name
    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    // Getter for status
    public PrescriptionStatus getStatus() {
        return status;
    }

    // Method to update the status
    public void updateStatus(PrescriptionStatus newStatus) {
        this.status = newStatus;
    }

    // Method to print the prescription
    public String toString() {
        return "Prescription: " + medicationName + ", Status: " + status;
    }
}

