package authorization;

import users.*;
import filereaders.InitialData;
import java.util.HashMap;
import java.util.Map;
//import Users.*;

public class AuthorizationControl {
    private Map<String, Users> activeSessions; // Stores active user sessions
    private Map<String, Users> users; // Stores all loaded users for authentication

    public AuthorizationControl() {
        activeSessions = new HashMap<>();
        users = new HashMap<>(); // Initialize users map
    }

    // Log in a user
    public Users login(String userID, String password) {
        Users user = users.get(userID); // Check the users map for the user
        if (user != null && user.getPassword().equals(password)) {
            createSession(user);
            return user;
        }
        System.out.println("Login failed for " + userID);
        return null; // Return null if login fails
    }

    // Create a session for the user
    public void createSession(Users user) {
        activeSessions.put(user.getUserID(), user);
    }

    // Log out a user
    public void logout(String userID) {
        activeSessions.remove(userID);
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
    public void loadCredentialsFromStaff(InitialData initialData) {
        for (Patient patient : initialData.getPatients()) {
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
        System.out.println("Staff credentials loaded successfully.");
    }
}
