import java.util.HashMap;
import java.util.Map;

public class AuthorizationControl {

    private Map<String, String> credentials = new HashMap<>();
    private Map<String, User> userSessions = new HashMap<>();

    public User login(String username, String password) {
        // Check if credentials match
        if (credentials.containsKey(username) && credentials.get(username).equals(password)) {
            User user = userSessions.get(username);
            if (user != null) {
                System.out.println("User already logged in.");
                return user;
            } else {
                // Placeholder for user session creation
                // Assuming we retrieve the user object somehow
                // For now, returning null as we need a user object
                System.out.println("Login successful.");
                return null; // Replace with actual user object
            }
        } else {
            System.out.println("Invalid credentials.");
            return null;
        }
    }


    public boolean authorise(String username, String requiredRole) {
        User user = userSessions.get(username);
        return user != null && user.getRole().toString().equals(requiredRole);
    }

    public void logout(String username) {
        userSessions.remove(username);
        System.out.println("User logged out.");
    }
}
