public abstract class User {

    protected String userID;
    protected String name;
    protected Role role;
    protected String contactInfo;

    public User(String userID, String name, Role role, String contactInfo) {
        this.userID = userID;
        this.name = name;
        this.role = role;
        this.contactInfo = contactInfo;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void changePassword(String newPassword) {
        System.out.println("Password changed.");
    }

    public abstract void displayMenu();

    public void login(String username, String password) {
        System.out.println("User logged in.");
    }

    public void logout() {
        System.out.println("User logged out.");
    }
}
