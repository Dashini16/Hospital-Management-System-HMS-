package medicalrecords;

import appointments.Appointment;
import enums.PrescriptionStatus;

public class Prescription{

    private String medicationName;
    private int quantity;
    private PrescriptionStatus status;

    // Constructor to initialize with medication name, setting status to "Pending" by default
    public Prescription(String medicationName, int quantity) {

        this.medicationName = medicationName;
        this.quantity = quantity;
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

     // Getter and setter for quantity
     public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
    // public String toString() {
    //     return "Prescription: " + medicationName + ", Status: " + status;
    // }@Override
    public String toString() {
        return medicationName + " (Quantity: " + quantity + ", Status: " + status + ")";
    }

}

