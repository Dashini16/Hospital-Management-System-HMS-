public class Doctor extends User {
    public Doctor(String userID, String name, String contactInfo) {
        super(userID, name, Role.DOCTOR, contactInfo);
    }

    @Override
    public void displayMenu() {
        System.out.println("Displaying doctor menu.");
    }
}
