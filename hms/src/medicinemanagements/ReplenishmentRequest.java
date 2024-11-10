package medicinemanagements;
import enums.RequestStatus;
public class ReplenishmentRequest {
    private String medicineName;
    private int requestedStock;
    private RequestStatus status;
    private String requestBy;
    private Boolean isNewMedicine;

    public ReplenishmentRequest(String medicineName, int requestedStock, String requestBy, Boolean isNewMedicine) {
        this.medicineName = medicineName;
        this.requestedStock = requestedStock;
        this.status = RequestStatus.PENDING; // Default status
        this.requestBy = requestBy;
        this.isNewMedicine = isNewMedicine;
    }

    // Getters and Setters
    public String getMedicineName() {
        return medicineName;
    }

    public int getRequestedStock() {
        return requestedStock;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public String getRequestBy() {
        return requestBy;
    }

    public Boolean getIsNewMedicine() {
        return isNewMedicine;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    
    public void setRequestedStock(int requestedStock) {
        this.requestedStock = requestedStock;
    }   

    public void setStatus(RequestStatus status) {
        this.status = status;
   }

   public void setRequestBy(String requestBy) {
    this.requestBy = requestBy;
    }

    public void setIsNewMedicine(Boolean isNewMedicine) {
        this.isNewMedicine = isNewMedicine;
    }

}