package filereaders;

import users.*;
import enums.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class InitialDataStaff implements DataImporter, DataExporterAppend<Users> {
    private List<Doctor> doctors;
    private List<Administrator> administrators;
    private List<Pharmacist> pharmacists; // Add list for pharmacists

    public InitialDataStaff() {
        doctors = new ArrayList<>();
        administrators = new ArrayList<>();
        pharmacists = new ArrayList<>(); // Initialize pharmacists list

    }

    @Override
    public void importData() {
        try {

            importDatafromFile("hms/src/data/Staff_List.csv");


        } catch (IOException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
    }

    @Override
    public void reloadData() {
        // Clear existing data from memory
        doctors.clear();
        administrators.clear();
        pharmacists.clear();

        // Reload data from files
        importData(); 
        //System.out.println("Data reloaded successfully.");
    }


    @Override
    public void importDatafromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    String userID = data[0];
                    String name = data[1];
                    Roles role = Roles.valueOf(data[2].toUpperCase()); // Convert role string to Role enum
                    String gender = data[3];
                    int age = Integer.parseInt(data[4]);
                    String password = data[5];

                    Users user;
                    switch (role) {
                        //case PATIENT:
                            //user = new Patient(userID, name, gender, age);
                            //patients.add((Patient) user);
                            //break;
                        case DOCTOR:
                            user = new Doctor(userID, name, gender, age, password);
                            doctors.add((Doctor) user);
                            break;
                        case ADMINISTRATOR:
                            user = new Administrator(userID, name, gender, age, password);
                            administrators.add((Administrator) user);
                            break;
                        case PHARMACIST: // Add this case for pharmacists
                            user = new Pharmacist(userID, name, gender, age,password);
                            pharmacists.add((Pharmacist) user);
                            break;
                        default:
                            System.out.println("Unknown role: " + role);
                    }
                }
            }
        }
    }


    // Method to append a new staff member to the file
    @Override
    public void appendData(String filename, Users staff) throws IOException {
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
            bw.write("," + staff.getPassword());
            bw.write("\n"); // End of line for this entry
        }
    }
    




    public void rewriteStaff(String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write the header
            bw.write("UserID,Name,Role,Gender,Age,Password\n");
            
            // Write each staff member's details
            for (Doctor doctor : doctors) {
                bw.write(doctor.getUserID() + "," + doctor.getName() + "," + doctor.getRole() + "," + doctor.getGender() + "," + doctor.getAge() + "," + doctor.getPassword() + "\n");
            }
            for (Administrator admin : administrators) {
                bw.write(admin.getUserID() + "," + admin.getName() + "," + admin.getRole() + "," + admin.getGender() + "," + admin.getAge() + "," + admin.getPassword() + "\n");
            }
            for (Pharmacist pharmacist : pharmacists) {
                bw.write(pharmacist.getUserID() + "," + pharmacist.getName() + "," + pharmacist.getRole() + "," + pharmacist.getGender() + "," + pharmacist.getAge() + "," + pharmacist.getPassword() + "\n");
            }
        }
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

    // to retreive all the staffs
    public List<Users> getStaffList() {
        List<Users> allStaff = new ArrayList<>();
        allStaff.addAll(doctors);
        allStaff.addAll(administrators);
        allStaff.addAll(pharmacists);
        return allStaff;
    }


}