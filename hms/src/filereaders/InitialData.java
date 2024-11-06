package filereaders;

import users.*;
import medicinemanagements.*;
import appointments.*;

import enums.*;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
//import Users.*;
//import Medications.*;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

//import Users.*;
//import Medications.*;

public class InitialData {
    private List<Patient> patients;
    private List<Doctor> doctors;
    private List<Administrator> administrators;
    private List<Pharmacist> pharmacists; // Add list for pharmacists
    private List<Medicine> medicines; // Add this line
    private List<ReplenishmentRequest> replenishmentRequests; // Add this line
    private List<Appointment> appointments;

    public InitialData() {
        patients = new ArrayList<>();
        doctors = new ArrayList<>();
        administrators = new ArrayList<>();
        pharmacists = new ArrayList<>(); // Initialize pharmacists list
        medicines = new ArrayList<>(); // Initialize the medicines list
        replenishmentRequests = new ArrayList<>(); // Initialize the request list
        appointments = new ArrayList<>();
    }

    public void importData() {
        try {
            importPatients("hms\\src\\data\\Patient_List.csv");
            importStaff("hms\\src\\data\\Staff_List.csv");
            importMedicines("hms\\src\\data\\Medicine_List.csv"); // Import medicine list
            importReplenishmentRequests("hms\\src\\data\\Replenishment_Requests.csv");
            importAppointments("hms\\src\\data\\Appointments_List.csv");
        } catch (IOException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
    }


    public void reloadData() {
        // Clear existing data from memory
        patients.clear();
        doctors.clear();
        administrators.clear();
        pharmacists.clear();
        medicines.clear();
        replenishmentRequests.clear();
        
        // Reload data from files
        importData(); 
        System.out.println("Data reloaded successfully.");
    }
    
    private DateTimeFormatter createDateFormatter() {
        return new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("d/M/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("d/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .toFormatter();
    }
    private void importPatients(String filename) throws IOException {
        DateTimeFormatter dateFormatter = createDateFormatter(); // Create formatter
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 7) {
                    Patient patient = new Patient(data[0], data[1],LocalDate.parse(data[2], dateFormatter), data[3], data[4], data[5],data[6]); 
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
                     patient.getGender() + "," + patient.getBloodType() + "," + patient.getContactInfo() +","+ patient.getPassword() + "\n");
            patients.add(patient); // Update the list in memory
        }
    }

    // Method to append a new staff member to the file
    public void appendStaff(String filename, Users staff) throws IOException {
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
    public void rewriteMedicines(String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write the header
            bw.write("Name,InitialStock,LowStockLevelAlert\n");
    
            // Write each medicine's details
            for (Medicine medicine : medicines) {
                bw.write(medicine.getName() + "," + medicine.getInitialStock() + "," + medicine.getLowStockLevelAlert() + "\n");
            }
        }
    }

    public void appendReplenishmentRequest(String filename, ReplenishmentRequest request) throws IOException {
        boolean fileExists = new File(filename).exists();
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            // If the file doesn't exist, write a header first
            if (!fileExists) {
                bw.write("MedicineName,RequestedQuantity,Status\n");
            }
            // Append the request data
            bw.write(request.getMedicineName() + "," + request.getRequestedStock() + "," + request.getStatus() + "\n");
        }
    }
    
    public void rewriteReplenishmentRequests(String filename, List<ReplenishmentRequest> requests) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write the header
            bw.write("MedicineName,RequestedQuantity,Status\n");
    
            // Write each replenishment request's details
            for (ReplenishmentRequest request : requests) {
                bw.write(request.getMedicineName() + "," + request.getRequestedStock() + "," + request.getStatus() + "\n");
            }
        }
    }

    private void importReplenishmentRequests(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String medicineName = data[0];
                    int requestedQuantity = Integer.parseInt(data[1].trim());
                    RequestStatus status = RequestStatus.valueOf(data[2].trim().toUpperCase());
                    ReplenishmentRequest request = new ReplenishmentRequest(medicineName, requestedQuantity);
                    request.setStatus(status);
                    replenishmentRequests.add(request);
                }
            }
        }
    }
    public void rewritePatients(String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write the header
            bw.write("UserID,Name,DateOfBirth,Gender,BloodType,ContactInfo,Password\n");
            
            // Write each patient's details
            for (Patient patient : patients) {
                bw.write(patient.getUserID() + "," + patient.getName() + "," +
                         patient.getDateOfBirth() + "," + patient.getGender() + "," +
                         patient.getBloodType() + "," + patient.getContactInfo() + "," +
                         patient.getPassword() + "\n");
            }
        }
    }

    // APPOINTMENT LIST
    // import appointments from Appointments_List.csv
    private void importAppointments(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    Appointment newappt = new Appointment(data[0], data[1], data[2], data[3], data[4]);
                    appointments.add(newappt); // Add to appointments list
                }
            }
        }
    }

    private Appointment getAppointmentInfo(String line) {
        String[] data = line.split(",", 7); // Limit split to 7 to include outcome record as a single part
        if (data.length < 7) {
            System.out.println("Invalid line format: " + line);
            return null;
        }
    
        String AppointmentID = data[0];
        String PatientID = data[1];
        String DoctorID = data[2];
        String date = data[3];
        String time = data[4];
        String ApptStatus = data[5];
    
        AppointmentStatus appointmentStatus;
        try {
            // Convert the string to the enum type AppointmentStatus
            appointmentStatus = AppointmentStatus.valueOf(ApptStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid appointment status: " + ApptStatus);
            return null; 
        }
        
        // Create the appointment object
        Appointment appointment = new Appointment(AppointmentID, PatientID, DoctorID, date, time);
        appointment.updateStatus(appointmentStatus);
        return appointment;
    }

    public Appointment findAppointment(String filename, String appointmentID) throws IOException {
        File file = new File(filename);
        List<String> lines = new ArrayList<>();
        Appointment existingAppointment = null;

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                    // Check if this line contains the specified Appointment ID
                    if (line.startsWith(appointmentID + ",")) {
                        existingAppointment = getAppointmentInfo(line);
                        break; 
                    }
                }
            }
        }
        return existingAppointment; // Return the found appointment
    }

    //Method to change date format
    // private String formatDate(String dateString) {
    //     DateTimeFormatter formatter = createDateFormatter();
    //     LocalDate date = LocalDate.parse(dateString, formatter);
    //     return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    // }

    // append Appointment (Schedule An Appoinment)
    public void appendAppointments(String filename, Appointment appointment) throws IOException {
        boolean fileExists = new File(filename).exists();
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            // If the file doesn't exist, write a header first
            if (!fileExists) {
                bw.write("AppointmentID,PatientID,DoctorID,Date,Time,Status,Outcome Record\n");
            }
            //String apptDate = formatDate(appointment.getDate());
            // Append the appointment data
            bw.write(appointment.getAppointmentID() + "," + appointment.getPatientID() + "," +
                     appointment.getDoctorID() + "," + appointment.getDate() + "," +
                     appointment.getTime() + "," + appointment.getStatus() +","+appointment.getOutcomeRecord()+ "\n");
        }
    }
    
    // rewrite Appointment (reschedule)
    // delete Appointment (Cancel Appointment)
    // rewrite Appointment (any changes for Appointment Outcome)


    public List<ReplenishmentRequest> getReplenishmentRequests() {
        return replenishmentRequests;
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
    
    public List<Appointment> getAppointments(){ 
        return appointments;
    }
    
}