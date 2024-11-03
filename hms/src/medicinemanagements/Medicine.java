package medicinemanagements;
public class Medicine {
    private String name;
    private int initialStock;
    private int lowStockLevelAlert;
    private int curStock;

    public Medicine(String name, int initialStock, int lowStockLevelAlert) {
        this.name = name;
        this.initialStock = initialStock;
        this.curStock = initialStock;
        this.lowStockLevelAlert = lowStockLevelAlert;
    }

    // Getters and setters can be added here
    public String getName() {
        return name;
    }

    public int getInitialStock() {
        return initialStock;
    }

    public int getLowStockLevelAlert() {
        return lowStockLevelAlert;
    }

    public void setName(String name) {
        this.name = name;
    }   
    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }

    public void setLowStockLevelAlert(int lowStockLevelAlert) {
        this.lowStockLevelAlert = lowStockLevelAlert;
    }

    // to print alert
    public void alertLowStock(){
        System.out.println(this.name + " stock is low!");
    }
    public void reduceStock(int n){
        if (curStock > 0) {
            curStock -= n; // Reduce current stock
            if (curStock <= getLowStockLevelAlert()) { //print alert if stock is low
                alertLowStock();
            }
        } else {
            System.out.println(this.name + " is out of stock!");
        }
    }
    
}
