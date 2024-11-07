package filereaders;

import users.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;


public class InitialDataPatient implements  DataImporter, DataExporterAppend<Patient> , ListInterface<Patient>, DateFormatterInterface{
    private List<Patient> patients;



    public InitialDataPatient() {
        patients = new ArrayList<>();

    }

    @Override
    public void importData() {
        try {

            importDatafromFile("hms\\src\\data\\Patient_List.csv");

        } catch (IOException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
    }


    @Override
    public void reloadData() {
        // Clear existing data from memory
        patients.clear();
        // Reload data from files
        importData(); 
        //System.out.println("Data reloaded successfully.");
    }
    @Override
    public DateTimeFormatter createDateFormatter() {
        return new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("d/M/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("d/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .toFormatter();
    }
    @Override
    public void importDatafromFile(String filename) throws IOException {
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

    @Override
    // Method to append a new patient to the file
    public void appendData(String filename, Patient patient) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(patient.getUserID() + "," + patient.getName() + "," + patient.getDateOfBirth() + "," +
                     patient.getGender() + "," + patient.getBloodType() + "," + patient.getContactInfo() +","+ patient.getPassword() + "\n");
            patients.add(patient); // Update the list in memory
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

    @Override
    public List<Patient> getLists() {
        return patients;
    }


}