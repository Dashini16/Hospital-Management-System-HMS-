public class Administrator extends User {
    public Administrator(String userID, String name, String contactInfo) {
        super(userID, name, Role.ADMINISTRATOR, contactInfo);
    }


    @Override
    public void displayMenu() {
        // Print the administrator menu to the console
        System.out.println("Displaying administrator menu.");
    }
}
