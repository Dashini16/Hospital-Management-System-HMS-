class ReplenishmentRequest {
    private String medicineName;
    private int requestedStock;
    private RequestStatus status;

    public ReplenishmentRequest(String medicineName, int requestedStock) {
        this.medicineName = medicineName;
        this.requestedStock = requestedStock;
        this.status = RequestStatus.PENDING; // Default status
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

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    
    public void setRequestedStock(int requestedStock) {
        this.requestedStock = requestedStock;
    }   

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

}
