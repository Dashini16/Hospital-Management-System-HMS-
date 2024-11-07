package filereaders;

import medicalrecords.MedicalRecord;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import java.io.File;

public class InitialDataMedicalRecord implements ListInterface<MedicalRecord>,DataImporter, DataExporterAppend<MedicalRecord>{
    private List<MedicalRecord> medicalRecords;



    public InitialDataMedicalRecord() {

        medicalRecords = new ArrayList<>();

    }

    @Override
    public void importData() {
        try {



            importDatafromFile("hms\\src\\data\\Medical_Records.csv");



        } catch (IOException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
    }

    @Override
    public void reloadData() {
        medicalRecords.clear();

        // Reload data from files
        importData(); 
        //System.out.println("Data reloaded successfully.");
    }
    
  

    //import medical records
    @Override
    public void importDatafromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            reader.readLine(); // Skip the header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    String patientID = parts[0];
                    String[] diagnosisArray = parts[1].split(","); // Split diagnoses by comma
                    String[] treatmentArray = parts[2].split(","); // Split treatments by comma
    
                    MedicalRecord record = new MedicalRecord(patientID);
                    for (String diagnosis : diagnosisArray) {
                        record.addDiagnosis(diagnosis.trim());
                    }
                    for (String treatment : treatmentArray) {
                        record.addTreatment(treatment.trim());
                    }
                    medicalRecords.add(record); // Add the new record to the list
                    //System.out.println("Medical Record imported for Patient ID: " + medicalRecords.get(medicalRecords.size() - 1).getPatientID());
                }
            }
        }
    }
    

    //APPEND MEDICAL RECORDS

    public void appendData(String filename, MedicalRecord medicalRecord) throws IOException {
        //check if file doesn't exist
        boolean fileExists = new File(filename).exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            // If the file doesn't exist, write a header first
            if (!fileExists) {
                writer.write("PatientID;Diagnoses;Treatments\n");
            }
            // Append the medical record data
            writer.write(medicalRecord.getPatientID() + ";" + String.join(",", medicalRecord.getDiagnoses()) + ";" + String.join(",", medicalRecord.getTreatments()) + "\n");
        }
    }

    //rewrite medical records
    public void writeData(String filename, List<MedicalRecord> medicalRecords ) throws IOException 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) 
        {
            writer.write("PatientID;Diagnoses;Treatments\n");
            for (MedicalRecord medicalRecord : medicalRecords) {
                writer.write(medicalRecord.getPatientID() + ";" + String.join(",", medicalRecord.getDiagnoses()) + ";" + String.join(",", medicalRecord.getTreatments()) + "\n");
            }

        }
    }
    
@Override
    public List<MedicalRecord> getLists() {
        return medicalRecords;
    }

}