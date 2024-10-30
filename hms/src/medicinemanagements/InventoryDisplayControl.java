package medicinemanagements;
import filereaders.InitialData;

public class InventoryDisplayControl {
    private InitialData data;
    //private List<ReplenishmentRequest> replenishmentRequests; // List to store replenishment requests

    public InventoryDisplayControl(InitialData data) {
        this.data = data;
        //this.replenishmentRequests = new ArrayList<>(); // Initialize the request list
    }
    
    
    public void displayInventory() {
        System.out.println("Current Medication Inventory:");
        for (Medicine medicine : data.getMedicines()) {
            System.out.println("Medicine: " + medicine.getName() + ", Stock: " + medicine.getInitialStock()+ ", Low Stock Alert: " + medicine.getLowStockLevelAlert());
        }
    }
}