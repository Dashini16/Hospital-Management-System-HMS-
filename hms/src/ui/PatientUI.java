package ui;

import users.Patient;

import java.util.Scanner;

import appointments.AppointmentManagementControl;
import appointments.AppointmentScheduling;
import appointments.MedicalManagement;
import appointmentslots.AppointmentSlotManagementControl;
import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataAppointments;
import filereaders.InitialDataMedicine;
import filereaders.InitialDataPatient;
import filereaders.InitialDataStaff;
import usermanagement.PasswordManagement;
import usermanagement.PatientManagementControl;
import usermanagement.StaffManagementControl;
import authorization.AuthorizationControl;

import java.util.Scanner;

public class PatientUI {
    private Patient patient;
    
    public PatientUI(Patient patient) {
        this.patient = patient;
    }

    public void displayMenu() {
        InitialDataStaff initialData = new InitialDataStaff();
        InitialDataPatient initialDataPatient = new InitialDataPatient();
        InitialDataAppointments dataAppointments = new InitialDataAppointments();
        InitialDataAppointmentSlots dataAppointmentSlots = new InitialDataAppointmentSlots();
        InitialDataMedicine medicineData = new InitialDataMedicine();
        initialData.importData();
        initialData.reloadData();

        initialDataPatient.importData();
        initialDataPatient.reloadData();

        dataAppointments.importData();
        dataAppointments.reloadData();

        dataAppointmentSlots.importData();
        dataAppointmentSlots.reloadData();

        medicineData.importData();
        medicineData.reloadData();
        

        // Print the pharmacist menu to the console
        AuthorizationControl authControl = new AuthorizationControl();
        // Load users into AuthorizationControl (assuming you have this in your code)
        authControl.loadCredentialsFromStaff(initialData, initialDataPatient); 
        // Print the patient menu to the console
        while (true) {
            System.out.println("Patient Menu:");
            System.out.println("===================================");
            System.out.println("1. View Medical Record");
            System.out.println("2. Update Personal Information");
            System.out.println("3. View Available Appointment Slots");
            System.out.println("4. Schedule an Appointment");
            System.out.println("5. Reschedule an Appointment");
            System.out.println("6. Cancel an Appointment");
            System.out.println("7. View Scheduled Appointments");
            System.out.println("8. View Past Appointment Outcome Records");
            System.out.println("9. Add Your Medical Record");
            System.out.println("10. Change Password");
            System.out.println("11. Logout");
            System.out.println("===================================");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    // Code to view medical record
                    System.out.println("\n==================================="); 
                    System.out.println("View Medical Record");
                    System.out.println("===================================\n"); // Add a border for the login section
                    MedicalManagement medicalManagement = new MedicalManagement();
                    medicalManagement.viewMedicalRecords();
                    break;
                case 2:
                    // Code to update personal information
                    System.out.println("\n==================================="); 
                    System.out.println("Update Personal Information");
                    System.out.println("===================================\n"); // Add a border for the login section
                    PatientManagementControl patientManagementControl = new PatientManagementControl(initialDataPatient);
                    patientManagementControl.updatePatient(scanner);;
                    break;
                case 3:
                    // Code to view available appointment slots
                    System.out.println("\n==================================="); 
                    System.out.println("View Available Appointment Slots");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentSlotManagementControl appointmentManagementControl2 = new AppointmentSlotManagementControl(initialData,dataAppointmentSlots,dataAppointments);
                    appointmentManagementControl2.viewAvailableTimeSlots();;
                    break;
                case 4:
                    // Code to schedule an appointment
                    System.out.println("\n==================================="); 
                    System.out.println("Schedule an Appointment");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentScheduling appointmentManagementControl = new AppointmentScheduling(initialData,dataAppointments, dataAppointmentSlots);
                    appointmentManagementControl.scheduleAppointment();;
                    break;
                case 5:
                    // Code to reschedule an appointment
                    System.out.println("\n==================================="); 
                    System.out.println("Reschedule an Appointment");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentScheduling appointmentManagementControl3 = new AppointmentScheduling(initialData,dataAppointments, dataAppointmentSlots);
                    appointmentManagementControl3.rescheduleAppointment();
                    
                    break;
                case 6:
                    // Code to cancel an appointment
                    System.out.println("\n==================================="); 
                    System.out.println("Cancel an Appointment");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentManagementControl appointmentManagementControl1 = new AppointmentManagementControl(medicineData, initialData,dataAppointments);
                    appointmentManagementControl1.cancelAppointment();
                    
                    break;
                case 7:
                    // Code to view scheduled appointments
                    System.out.println("\n==================================="); 
                    System.out.println("View Scheduled Appointments");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentManagementControl appointmentManagementControl4 = new AppointmentManagementControl(medicineData, initialData,dataAppointments);
                    appointmentManagementControl4.viewAppointments(false);
                    break;
                case 8:
                    // Code to view past appointment outcome records
                    System.out.println("\n==================================="); 
                    System.out.println("View Past Appointment Outcome Records");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentManagementControl appointmentManagementControl5 = new AppointmentManagementControl(medicineData, initialData,dataAppointments);
                    appointmentManagementControl5.viewOutcomeRecords(true);
                    break;
            
                case 9:
                    // Add code to create patient medical record
                    System.out.println("\n==================================="); 
                    System.out.println("Add Your Medical Record");
                    System.out.println("===================================\n"); // Add a border for the login section
                    MedicalManagement medicalManagement2 = new MedicalManagement();
                    medicalManagement2.createMedicalRecord();
                    break;
                case 10:
                    System.out.println("\n==================================="); 
                    System.out.println("Change Password");
                    System.out.println("===================================\n"); // Add a border for the login section
                    PasswordManagement passwordManagement = new PasswordManagement(initialData, initialDataPatient);
                    //patientPasswordManagementControl.changePatientPassword(scanner);;
                    passwordManagement.changePassword(scanner);
                    break;
                case 11:
                    System.out.println("Logging out...");
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
