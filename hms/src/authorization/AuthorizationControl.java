package authorization;

import users.*;
import filereaders.InitialDataPatient;
import filereaders.InitialDataStaff;
import usermanagement.PasswordUtils;

import java.util.HashMap;
import java.util.Map;
//import Users.*;

public class AuthorizationControl {
    private Map<String, Users> activeSessions; // Stores active user sessions
    private Map<String, Users> users; // Stores all loaded users for authentication
    private static String currentUserId; 

    public AuthorizationControl() {
        activeSessions = new HashMap<>();
        users = new HashMap<>(); // Initialize users map
    }

    // Log in a user
    public Users login(String userID, String password) {
        System.out.println("Attempting login for user: " + userID);
        Users user = users.get(userID); // Check the users map for the user
        if (user != null) {
            String hashedInputPassword = PasswordUtils.hashPassword(password);
            //System.out.println("User found: " + user.getName());
            if (user.getPassword().equals(hashedInputPassword)) {
                createSession(user);
                //currentUserId = user.getUserID();
                //System.out.println("Login successful for user: " + currentUserId);
                return user;
            } else {
                //System.out.println("Password mismatch for user: " + userID);
            }
        } else {
            System.out.println("User not found: " + userID);
        }
        return null; // Return null if login fails
    }

    // Create a session for the user
    public void createSession(Users user) {
        activeSessions.put(user.getUserID(), user);
        currentUserId = user.getUserID();
    }

    // Log out a user
    public void logout(String userID) {
        activeSessions.remove(userID);
        if (currentUserId != null && currentUserId.equals(userID)) {
            currentUserId = null; // Clear the current user ID on logout
        }
    }

    // Authorize user role
    public boolean authorize(String userID, String expectedRole) {
        Users user = activeSessions.get(userID);
        if (user != null) {
            return user.getRole().name().equals(expectedRole);
        }
        return false;
    }

    // Load credentials from the staff data
    
    public void loadCredentialsFromStaff(InitialDataStaff initialData,InitialDataPatient initialDataPatient) {
        for (Patient patient : initialDataPatient.getLists()) {
            users.put(patient.getUserID(), patient);
        }
        for (Doctor doctor : initialData.getDoctors()) {
            users.put(doctor.getUserID(), doctor);
        }
        for (Administrator admin : initialData.getAdministrators()) {
            users.put(admin.getUserID(), admin);
        }
        for (Pharmacist pharmacist : initialData.getPharmacists()) {
            users.put(pharmacist.getUserID(), pharmacist);
        }
        //System.out.println("Staff credentials loaded successfully.");
    }


    // Static method to get the current user ID
    public static String getCurrentUserId() {
        //System.out.println("Current user ID:ytuylu,tyluj,ptyuj " + currentUserId);
        return currentUserId; // Return the current user ID
    }
}
