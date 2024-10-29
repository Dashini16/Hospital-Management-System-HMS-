import java.time.LocalDate;

public class Patient extends User {

    private LocalDate dateOfBirth;
    private String bloodType;
    private String contactInfo;
    private PatientUI ui; // Reference to the UI class

    public Patient(String userID, String name, LocalDate dateOfBirth, String gender, String bloodType, String contactInfo, String password) {
        super(userID, name, Role.PATIENT, gender, password);
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
        this.contactInfo = contactInfo;
        this.ui = new PatientUI(this); // Initialize UI with current Patient instance
    }

    // Getters and Setters
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    // Use the PatientUI for displaying the menu
    public void displayMenu() {
        ui.displayMenu(); // Delegate menu display to PatientUI
    }

    @Override
    public void toStrings() {
        System.out.println("Role: " + super.getRole() + ", Patient ID: " + super.getUserID() + ", Name: " + super.getName() + ", Date of Birth: " + dateOfBirth + ", Gender: " + super.getGender() + ", Blood Type: " + bloodType + ", Contact Info: " + contactInfo);
    }
}