public class Pharmacist extends User {
    public Pharmacist(String userID, String name, String contactInfo) {
        super(userID, name, Role.PHARMACIST, contactInfo);
    }

    @Override
    public void displayMenu() {
        System.out.println("Displaying pharmacist menu.");
    }
}
