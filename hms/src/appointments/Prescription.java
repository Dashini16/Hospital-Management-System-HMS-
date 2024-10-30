package appointments;

public class Prescription {
    public enum PrescriptonStatus{
        PENDING, FULFILLED, CANCELLED
    }
    private String medicineNameString;
    private PrescriptonStatus status;
}
