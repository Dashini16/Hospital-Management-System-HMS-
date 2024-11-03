package appointments;

import medicinemanagements.Medicine;

public class Prescription {
    public enum PrescriptionStatus{
        PENDING, FULFILLED, CANCELLED
    }
    private String medicineName;
    private PrescriptionStatus status;
    private Medicine[] listOfMedicine;

    public Prescription(String medicineName, PrescriptionStatus status, int noOfMedicine){
        this.status = PrescriptionStatus.PENDING;
        this.medicineName = medicineName;
        this.listOfMedicine = new Medicine[noOfMedicine];
    }

    public PrescriptionStatus getStatus(){
        return this.status;
    }
    
    public void setStatus(PrescriptionStatus newStatus){
        this.status = newStatus;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void addMedicine(Medicine medicine) {
        for (int i = 0; i < listOfMedicine.length; i++) {
            if (listOfMedicine[i] == null) {
                listOfMedicine[i] = medicine;
                break;
            }
        }
    }

    

    
}
