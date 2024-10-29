public class Pharmacist extends User {
    private int age;
    private PharmacistUI ui; // Reference to the UI class

    public Pharmacist(String userID, String name, String gender, int age, String password) {
        super(userID, name, Role.PHARMACIST, gender, password);
        this.age = age;
        this.ui = new PharmacistUI(this); // Initialize UI with current Pharmacist instance
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Use the PharmacistUI for displaying the menu
    @Override
    public void displayMenu() {
        ui.displayMenu(); // Delegate menu display to PharmacistUI
    }

    @Override
    public void toStrings() {
        System.out.println("Staff ID: " + super.getUserID() + ", Name: " + super.getName() + ", Role: " + super.getRole() + ", Gender: " + super.getGender() + ", Age: " + age);
    }
}
