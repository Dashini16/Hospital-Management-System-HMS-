public class Medicine {
    private String name;
    private int initialStock;
    private int lowStockLevelAlert;

    public Medicine(String name, int initialStock, int lowStockLevelAlert) {
        this.name = name;
        this.initialStock = initialStock;
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

    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }
}
