public abstract class User {
    private String userID;
    private String name;
    private Role role;
    private String gender;
    private String password;
    public User(String userID, String name, Role role, String gender, String password) {
        this.userID = userID;
        this.name = name;
        this.role = role;
        this.gender = gender;
        this.password = password;
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
    
    public String getGender() {
        return gender;
    }

    public void setUserID(String userID) {  
        this.userID = userID;
    }   

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setGender(String gender) {  
        this.gender = gender;
    }

    public String getPassword() {   
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        System.out.println("Password changed.");
    }

    //public void changePassword(String newPassword) {
        // Placeholder for password change logic
        //System.out.println("Password changed.");
    //}
    
    public abstract void displayMenu();
    public abstract void toStrings();
}