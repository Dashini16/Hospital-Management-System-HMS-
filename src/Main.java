public class Main {
    public static void main(String[] args) {
        // Initialize the initial data
        InitialData initialData = new InitialData();
        initialData.importData();

        // Display loaded patients
        System.out.println("Patients:");
        for (Patient patient : initialData.getPatients()) {
            System.out.println("ID: " + patient.getUserID() + ", Name: " + patient.getName() + ", Contact: " + patient.getContactInfo());
        }

        // Display loaded medicines
        System.out.println("\nMedicines:");
        for (Medicine medicine : initialData.getMedicines()) {
            System.out.println("Name: " + medicine.getName() + ", Initial Stock: " + medicine.getInitialStock() + ", Low Stock Level: " + medicine.getLowStockLevelAlert());
        }

        // Display loaded doctors
        System.out.println("\nDoctors:");
        for (Doctor doctor : initialData.getDoctors()) {
            System.out.println("ID: " + doctor.getUserID() + ", Name: " + doctor.getName());
        }

        // Create Authorization Control
        AuthorizationControl authControl = new AuthorizationControl();

        // Sample credentials (Assuming these are derived from the staff data)
        authControl.addCredential("D001", "password1");
        authControl.addCredential("D002", "password2");
        authControl.addCredential("P001", "password3");
        authControl.addCredential("A001", "password4");

        // Attempting to log in
        User user1 = authControl.login("D001", "password1");
        if (user1 != null) {
            System.out.println("Login successful for " + user1.getName());

            System.out.println(user1.getName() + " logged in as " + user1.getRole());
            authControl.createSession(user1); // Create session for logged-in user
        }

        User user2 = authControl.login("D002", "password2");
        if (user2 != null) {
            System.out.println(user2.getName() + " logged in as " + user2.getRole());
            authControl.createSession(user2);
        }

        User user3 = authControl.login("P001", "password3");
        if (user3 != null) {
            System.out.println(user3.getName() + " logged in as " + user3.getRole());
            authControl.createSession(user3);
        }

        User user4 = authControl.login("A001", "password4");
        if (user4 != null) {
            System.out.println(user4.getName() + " logged in as " + user4.getRole());
            authControl.createSession(user4);
        }

        // Testing authorization
        boolean isAuthorizedAdmin = authControl.authorize("A001", "ADMINISTRATOR");
        System.out.println("Is A001 authorized? " + isAuthorizedAdmin);

        boolean isAuthorizedDoctor = authControl.authorize("D001", "DOCTOR");
        System.out.println("Is D001 authorized? " + isAuthorizedDoctor);

        // Logging out
        authControl.logout("D001");
        System.out.println("Logged out D001.");
    }
}
