import java.util.HashMap;
import java.util.Map;

public class AuthorizationControl {
    private Map<String, String> credentials; // Stores userID and password
    private Map<String, User> activeSessions; // Stores active user sessions

    public AuthorizationControl() {
        credentials = new HashMap<>();
        activeSessions = new HashMap<>();
    }

    // Add a user credential
    public void addCredential(String userID, String password) {
        credentials.put(userID, password);
    }

    // Log in a user
    public User login(String userID, String password) {
        if (credentials.containsKey(userID) && credentials.get(userID).equals(password)) {
            User user = findUserById(userID);
            if (user != null) {
                return user;
            } else {
                System.out.println("User not found: " + userID);
            }
        }
        System.out.println("Login failed for " + userID);
        return null; // Return null if login fails
    }

    // Create a session for the user
    public void createSession(User user) {
        activeSessions.put(user.getUserID(), user);
    }

    // Log out a user
    public void logout(String userID) {
        activeSessions.remove(userID);
    }

    // Authorize user role
    public boolean authorize(String userID, String expectedRole) {
        User user = activeSessions.get(userID);
        if (user != null) {
            return user.getRole().name().equals(expectedRole);
        }
        return false;
    }

    // Helper method to find a user by ID (from all loaded data)
    private User findUserById(String userID) {
        // Assuming there is an InitialData instance available
        // In practice, you might pass this as a parameter to the constructor or another method
        InitialData data = new InitialData();
        data.importData(); // Load data (in real usage, avoid re-importing)
        
        for (Patient patient : data.getPatients()) {
            if (patient.getUserID().equals(userID)) {
                return patient;
            }
        }
        for (Doctor doctor : data.getDoctors()) {
            if (doctor.getUserID().equals(userID)) {
                return doctor;
            }
        }
        for (Administrator admin : data.getAdministrators()) {
            if (admin.getUserID().equals(userID)) {
                return admin;
            }
        }
        for (Pharmacist pharmacist : data.getPharmacists()) {
            if (pharmacist.getUserID().equals(userID)) {
                return pharmacist;
            }
        }
        return null; // User not found
    }
}
