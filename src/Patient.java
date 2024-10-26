public class Patient extends User {
    public Patient(String userID, String name, String contactInfo) {
        super(userID, name, Role.PATIENT, contactInfo);
    }

    @Override
    public void displayMenu() {
        System.out.println("Displaying patient menu.");
    }
}

