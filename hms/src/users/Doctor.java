package users;

import ui.DoctorUI;
import enums.Roles;

public class Doctor extends Users {
    private int age;
    private DoctorUI ui; // Reference to the UI class

    public Doctor(String userID, String name, String gender, int age, String password) {
        super(userID, name, Roles.DOCTOR, gender, password);
        this.age = age;
        this.ui = new DoctorUI(this); // Initialize UI with current Doctor instance
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Use the DoctorUI for displaying the menu
    @Override
    public void displayMenu() {
        ui.displayMenu(); // Delegate menu display to DoctorUI
    }

    @Override
    public void toStrings() {
        System.out.println("Staff ID: " + super.getUserID() + ", Name: " + super.getName() + ", Role: " + super.getRole() + ", Gender: " + super.getGender() + ", Age: " + age);
    }
}
