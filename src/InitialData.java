import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InitialData {
    private List<Patient> patients;
    private List<Doctor> doctors;
    private List<Administrator> administrators;
    private List<Pharmacist> pharmacists; // Add list for pharmacists
    private List<Medicine> medicines; // Add this line

    public InitialData() {
        patients = new ArrayList<>();
        doctors = new ArrayList<>();
        administrators = new ArrayList<>();
        pharmacists = new ArrayList<>(); // Initialize pharmacists list
        medicines = new ArrayList<>(); // Initialize the medicines list
    }

    public void importData() {
        try {
            importPatients("../data/Patient_List.csv");
            importStaff("../data/Staff_List.csv");
            importMedicines("../data/Medicine_List.csv"); // Import medicine list
        } catch (IOException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
    }

    private void importPatients(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    Patient patient = new Patient(data[0], data[1], data[5]); // Using contact info
                    patients.add(patient);
                }
            }
        }
    }

    private void importStaff(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    String userID = data[0];
                    String name = data[1];
                    Role role = Role.valueOf(data[2].toUpperCase()); // Convert role string to Role enum
                    String contactInfo = ""; // Assume no contact info provided

                    User user;
                    switch (role) {
                        case PATIENT:
                            user = new Patient(userID, name, contactInfo);
                            patients.add((Patient) user);
                            break;
                        case DOCTOR:
                            user = new Doctor(userID, name, contactInfo);
                            doctors.add((Doctor) user);
                            break;
                        case ADMINISTRATOR:
                            user = new Administrator(userID, name, contactInfo);
                            administrators.add((Administrator) user);
                            break;
                        case PHARMACIST: // Add this case for pharmacists
                            user = new Pharmacist(userID, name, contactInfo);
                            pharmacists.add((Pharmacist) user);
                            break;
                        default:
                            System.out.println("Unknown role: " + role);
                    }
                }
            }
        }
    }


    private void importMedicines(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String name = data[0];
                    int initialStock = Integer.parseInt(data[1].trim()); // Convert to integer
                    int lowStockLevel = Integer.parseInt(data[2].trim()); // Convert to integer
                    Medicine medication = new Medicine(name, initialStock, lowStockLevel);
                    medicines.add(medication); // Add to medicines list
                }
            }
        }
    }
    public List<Patient> getPatients() {
        return patients;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public List<Administrator> getAdministrators() {
        return administrators;
    }

    public List<Pharmacist> getPharmacists() { // Add getter for pharmacists
        return pharmacists;
    }

    public List<Medicine> getMedicines() {  // Add this method
        return medicines;
    }

    
}
