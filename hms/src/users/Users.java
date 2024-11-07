package users;

import enums.Roles;

public abstract class Users {
    private String userID;
    private String name;
    private Roles role;
    private String gender;
    private String password;
    public Users(String userID, String name, Roles role, String gender, String password) {
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
    
    public Roles getRole() {
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

    public void setRole(Roles role) {
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
        //System.out.println("Password changed.");
    }

    
    public abstract void displayMenu();
    public abstract void toStrings();
}
