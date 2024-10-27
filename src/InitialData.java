import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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
                    Patient patient = new Patient(data[0], data[1],LocalDate.parse(data[2]), data[3], data[4], data[5]); 
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
                    String gender = data[3];
                    int age = Integer.parseInt(data[4]);

                    User user;
                    switch (role) {
                        //case PATIENT:
                            //user = new Patient(userID, name, gender, age);
                            //patients.add((Patient) user);
                            //break;
                        case DOCTOR:
                            user = new Doctor(userID, name, gender, age);
                            doctors.add((Doctor) user);
                            break;
                        case ADMINISTRATOR:
                            user = new Administrator(userID, name, gender, age);
                            administrators.add((Administrator) user);
                            break;
                        case PHARMACIST: // Add this case for pharmacists
                            user = new Pharmacist(userID, name, gender, age);
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

    // Method to append a new patient to the file
    public void appendPatient(String filename, Patient patient) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(patient.getUserID() + "," + patient.getName() + "," + patient.getDateOfBirth() + "," +
                     patient.getGender() + "," + patient.getBloodType() + "," + patient.getContactInfo() + "\n");
            patients.add(patient); // Update the list in memory
        }
    }

    // Method to append a new staff member to the file
    public void appendStaff(String filename, User staff) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            // Write basic information
            bw.write(staff.getUserID() + "," + staff.getName() + "," + staff.getRole() + "," + staff.getGender());
    
            // Only add age if it's available (for Doctor, Administrator, and Pharmacist)
            if (staff instanceof Doctor) {
                bw.write("," + ((Doctor) staff).getAge());
                doctors.add((Doctor) staff);
            } else if (staff instanceof Administrator) {
                bw.write("," + ((Administrator) staff).getAge());
                administrators.add((Administrator) staff);
            } else if (staff instanceof Pharmacist) {
                bw.write("," + ((Pharmacist) staff).getAge());
                pharmacists.add((Pharmacist) staff);
            }
            
            bw.write("\n"); // End of line for this entry
        }
    }
    

    // Method to append a new medicine to the file
    public void appendMedicine(String filename, Medicine medicine) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(medicine.getName() + "," + medicine.getInitialStock() + "," + medicine.getLowStockLevelAlert() + "\n");
            medicines.add(medicine); // Update the list in memory
        }
    }

    public void rewriteStaff(String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write the header
            bw.write("UserID,Name,Role,Gender,Age\n");
            
            // Write each staff member's details
            for (Doctor doctor : doctors) {
                bw.write(doctor.getUserID() + "," + doctor.getName() + "," + doctor.getRole() + "," + doctor.getGender() + "," + doctor.getAge() + "\n");
            }
            for (Administrator admin : administrators) {
                bw.write(admin.getUserID() + "," + admin.getName() + "," + admin.getRole() + "," + admin.getGender() + "," + admin.getAge() + "\n");
            }
            for (Pharmacist pharmacist : pharmacists) {
                bw.write(pharmacist.getUserID() + "," + pharmacist.getName() + "," + pharmacist.getRole() + "," + pharmacist.getGender() + "," + pharmacist.getAge() + "\n");
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
