package appointments;
import medicinemanagements.Medicine;

public class Prescription {
    public enum PrescriptionStatus{
        PENDING, FULFILLED, CANCELLED
    }
    private String medicineName;
    private PrescriptionStatus status;
    private Medicine prescribedMedicine;

    public Prescription(String medicineName, PrescriptionStatus status, Medicine pMeds){
        this.status = status;
        this.medicineName = medicineName;
        this.prescribedMedicine = pMeds;
    }

    public void quantity(int noOfMedicine){
        this.prescribedMedicine.reduceStock(noOfMedicine);
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
}
