package appointments;

public class Medicine {
    private String medicineName;
    private int initialStock;
    private int curStock;
    private int lowAlert;

    Medicine(String name, int initialS, int low){
        this.medicineName = name;
        this.initialStock = initialS;
        this.curStock = initialStock;
        this.lowAlert = low;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getStock(){
        return this.curStock;
    }
    public void alertLowStock(){
        System.out.println(this.medicineName + " stock is low!");
    }
    public void reduceStock(){
        if (curStock > 0) {
            curStock -= 1; // Reduce current stock
            if (curStock <= lowAlert) { //print alert if stock is low
                alertLowStock();
            }
        } else {
            System.out.println(this.medicineName + " is out of stock!");
        }
    }
    
}
