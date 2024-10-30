package users;

import ui.AdministratorUI;
import enums.Roles;

public class Administrator extends Users {

    private int age;
    private AdministratorUI ui; // Reference to the UI class

    public Administrator(String userID, String name, String gender, int age, String password) {
        super(userID, name, Roles.ADMINISTRATOR, gender, password);
        this.age = age;
        this.ui = new AdministratorUI(this); // Initialize UI with current Administrator instance
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {   
        this.age = age;
    }

    public void displayMenu() {
        ui.displayMenu(); // Delegate menu display to AdministratorUI
    }

    @Override
    public void toStrings() {
        System.out.println("Staff ID: " + super.getUserID() + ", Name: " + super.getName() + 
                           ", Role: " + super.getRole() + ", Gender: " + super.getGender() + 
                           ", Age: " + age);
    }
}
