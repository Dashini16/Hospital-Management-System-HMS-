package filereaders;


import appointments.*;
import enums.*;
import medicalrecords.OutcomeRecord;
import medicalrecords.Prescription;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.io.File;


import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

//import Users.*;
//import Medications.*;

public class InitialDataAppointments implements ListInterface<Appointment>,AppointmentSearchInterface,DataImporter, DateFormatterInterface, DateFormatterService, DataExporterAppend<Appointment> , DataExporterWriter<Appointment>  {
    private List<Appointment> appointments;


    public InitialDataAppointments() {
        appointments = new ArrayList<>();

    }
    @Override
    public void importData() {
        try {
            importDatafromFile("hms/src/data/Appointments_List.csv");



        } catch (IOException e) {
            System.err.println("Error reading data: " + e.getMessage());
        }
    }


    @Override
    public void reloadData() {
        // Clear existing data from memory
        appointments.clear();

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
                Appointment appointment = parseAppointment(line);
                if (appointment != null) {
                    appointments.add(appointment);
                    //System.out.println("Appointment imported: " + appointment);
                }
            }
        }
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
    public String formatDate(String dateString) {
        DateTimeFormatter formatter = createDateFormatter();
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    //APPEND THE APPOINTMENTS IF FILE ALREADY EXISTS IF NOT CREATE A NEW FILE
    public void appendData(String filename, Appointment appointment) throws IOException {
        boolean fileExists = new File(filename).exists();
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            // If the file doesn't exist, write a header first
            if (!fileExists) {
                bw.write("AppointmentID,PatientID,DoctorID,Date,Time,Status,Outcome Record\n");
            }
            String formattedDate = formatDate(appointment.getDate());
            // Append the appointment data
            bw.write(appointment.getAppointmentID() + "," + appointment.getPatientID() + "," +
                     appointment.getDoctorID() + "," + formattedDate + "," +
                     appointment.getTime() + "," + appointment.getStatus() +","+appointment.getOutcomeRecord()+ "\n");
        }
    }

    public void writeAppointmentToFilewithdefaultOutcome(String filename, Appointment appointment) throws IOException {
        File file = new File(filename);
        List<String> lines = new ArrayList<>();
        boolean fileExists = file.exists();
        
        // Read the existing data to update it
        if (fileExists) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            }
        }
    
        // Prepare the new Outcome Record string
        StringBuilder prescriptionList = new StringBuilder();
        for (Prescription p : appointment.getOutcomeRecord().getPrescriptions()) {
            if (prescriptionList.length() > 0) prescriptionList.append(";");
            prescriptionList.append(p.getMedicationName()).append(",").append(p.getStatus());
        }
    
        // Constructing the outcome record using String.format
        String outcomeRecord = String.format("[%s, %s, %s, [%s]]",
                appointment.getOutcomeRecord().getDateOfAppointment(),
                appointment.getOutcomeRecord().getServiceType(),
                appointment.getOutcomeRecord().getConsultationNotes(),
                prescriptionList.toString());
        
        // Format the date using the formatDate method
        String formattedDate = formatDate(appointment.getDate());
    
        // Construct the appointment line with the formatted date
        String appointmentLine = String.join(",",
        appointment.getAppointmentID(),
        appointment.getPatientID(),
        appointment.getDoctorID(),
        formattedDate,  // Use the formatted date
        appointment.getTime(),
        appointment.getStatus().toString(),  // Convert AppointmentStatus to String
        outcomeRecord
);

    
        boolean appointmentUpdated = false;
    
        // Check if the file contains the appointment ID
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith(appointment.getAppointmentID() + ",")) {
                // Update the existing appointment line
                lines.set(i, appointmentLine);
                appointmentUpdated = true;
                break;
            }
        }
    
        // If the appointment was not found, append it
        if (!appointmentUpdated) {
            lines.add(appointmentLine);
        }
    
        // Write the updated lines back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // Write the header if the file is newly created
            if (!fileExists) {
                bw.write("AppointmentID,PatientID,DoctorID,Date,Time,Status,Outcome Record\n");
            }
            for (String line : lines) {
                bw.write(line + "\n");
            }
        }
    }
    




private Appointment parseAppointment(String line) {
    String[] parts = line.split(",", 7); // Limit split to 7 to include outcome record as a single part
    if (parts.length < 7) {
        System.out.println("Invalid line format: KDFGDJFKGJDFKGJKDJGKDJFGKD" + line);
        return null;
    }

    String appointmentID = parts[0];
    String patientID = parts[1];
    String doctorID = parts[2];
    String date = parts[3];
    String time = parts[4];
    String statusString = parts[5];

    AppointmentStatus status;
    try {
        // Convert the status string to the AppointmentStatus enum
        status = AppointmentStatus.valueOf(statusString.toUpperCase());
    } catch (IllegalArgumentException e) {
        System.out.println("Invalid appointment status: " + statusString);
        return null; // Return null or handle as necessary
    }

    // Parse the outcome record, handling NULL properly
    OutcomeRecord outcomeRecord = parseOutcomeRecord(parts[6]);

    // Create the appointment object
    Appointment appointment = new Appointment(appointmentID, patientID, doctorID, date, time);
    appointment.setOutcomeRecord(outcomeRecord); // This can be null without issues
    appointment.updateStatus(status);
    return appointment;
}



private OutcomeRecord parseOutcomeRecord(String outcomeRecordStr) {
    // Check if the outcomeRecordStr is NULL or just whitespace
    if (outcomeRecordStr == null || outcomeRecordStr.trim().equalsIgnoreCase("NULL")) {
        return null; // Return null if it's NULL
    }
    
    if (!outcomeRecordStr.startsWith("[") || !outcomeRecordStr.endsWith("]")) {
        System.out.println("Invalid outcome record format: " + outcomeRecordStr);
        return null;
    }

    // Remove outer brackets and split
    String cleanedStr = outcomeRecordStr.substring(1, outcomeRecordStr.length() - 1).trim();
    String[] mainParts = cleanedStr.split(", ", 4);

    if (mainParts.length < 3) {
        System.out.println("Invalid outcome record format: " + outcomeRecordStr);
        return null;
    }

    String dateOfAppointment = mainParts[0].trim();
    String serviceType = mainParts[1].trim();
    String consultationNotes = mainParts[2].trim();
    OutcomeRecord outcomeRecord = new OutcomeRecord(dateOfAppointment, serviceType, consultationNotes);

    if (mainParts.length == 4) {
        String prescriptionPart = mainParts[3];
        if (prescriptionPart.startsWith("[") && prescriptionPart.endsWith("]")) {
            String prescriptionStr = prescriptionPart.substring(1, prescriptionPart.length() - 1);
            String[] prescriptions = prescriptionStr.split(";");

            for (String prescription : prescriptions) {
                String[] prescriptionDetails = prescription.split(",");
                if (prescriptionDetails.length >= 1) {
                    String medicationName = prescriptionDetails[0].trim();
                    String status = prescriptionDetails.length > 1 ? prescriptionDetails[1].trim() : "pending"; // Default to "pending" if no status
                    Prescription newPrescription = new Prescription(medicationName);
                    outcomeRecord.addPrescription(newPrescription); // Pass both medication name and status
                    newPrescription.updateStatus(PrescriptionStatus.valueOf(status.toUpperCase()));
                }
            }
        }
    }

    return outcomeRecord;
}


@Override
public void writeData(String filename, Appointment appointment) throws IOException {
    File file = new File(filename);
    List<String> lines = new ArrayList<>();
    boolean fileExists = file.exists();
    
    // Read the existing data to update it
    if (fileExists) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
    }
    
    // Prepare the new Outcome Record string
    StringBuilder prescriptionList = new StringBuilder();
    for (Prescription p : appointment.getOutcomeRecord().getPrescriptions()) {
        if (prescriptionList.length() > 0) prescriptionList.append(";");
        prescriptionList.append(p.getMedicationName()).append(",").append(p.getStatus());
    }

    // Constructing the outcome record using String.format
    String outcomeRecord = String.format("[%s, %s, %s, [%s]]",
            appointment.getOutcomeRecord().getDateOfAppointment(),
            appointment.getOutcomeRecord().getServiceType(),
            appointment.getOutcomeRecord().getConsultationNotes(),
            prescriptionList.toString());
    
    String appointmentLine = appointment.getAppointmentID() + "," +
            appointment.getPatientID() + "," +
            appointment.getDoctorID() + "," +
            appointment.getDate() + "," +
            appointment.getTime() + "," +
            appointment.getStatus() + "," +
            outcomeRecord;

    boolean appointmentUpdated = false;

    // Check if the file contains the appointment ID
    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.startsWith(appointment.getAppointmentID() + ",")) {
            // Update the existing appointment line
            lines.set(i, appointmentLine);
            appointmentUpdated = true;
            break;
        }
    }

    // If the appointment was not found, append it
    if (!appointmentUpdated) {
        lines.add(appointmentLine);
    }

    // Write the updated lines back to the file
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
        // Write the header if the file is newly created
        if (!fileExists) {
            bw.write("AppointmentID,PatientID,DoctorID,Date,Time,Status,Outcome Record\n");
        }
        for (String line : lines) {
            bw.write(line + "\n");
        }
    }
}

@Override
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
                        existingAppointment = parseAppointment(line);
                        break; // Found the appointment, exit the loop
                    }
                }
            }
        }
        return existingAppointment; // Return the found appointment or null
    }

    public List<Appointment> getLists() {
        return appointments;
    }
}
