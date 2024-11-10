package ui;
import users.Administrator;
import users.Doctor;
import users.Patient;
import users.Pharmacist;
import users.Users;
import filereaders.InitialDataAppointmentSlots;
import filereaders.InitialDataAppointments;
import filereaders.InitialDataLeaveRequest;
import filereaders.InitialDataMedicine;
import filereaders.InitialDataPatient;
import filereaders.InitialDataStaff;
import filereaders.InitialDataLeaveRequest;
import usermanagement.PasswordManagement;
import usermanagement.StaffManagementControl;

import java.io.IOException;
import java.util.Scanner;
import authorization.AuthorizationControl;
import ui.StaffLeaveRequestUI;

import appointments.AppointmentManagementControl;
import appointments.MedicalManagement;
import appointmentslots.AppointmentSlotManagementControl;

public class DoctorUI {
    private Doctor doctor;
    private InitialDataStaff data;
    private InitialDataPatient patientData;


    public DoctorUI(Doctor doctor) {
        this.doctor = doctor;
    }

    public void displayMenu() {
        InitialDataStaff data = new InitialDataStaff();
        InitialDataPatient patientData = new InitialDataPatient();
        InitialDataAppointments dataAppointments = new InitialDataAppointments();
        InitialDataAppointmentSlots dataAppointmentSlots = new InitialDataAppointmentSlots();
        InitialDataMedicine medicineData = new InitialDataMedicine();
        InitialDataLeaveRequest leaveData = new InitialDataLeaveRequest();

        data.importData(); // Load data
        data.reloadData();

        patientData.importData();
        patientData.reloadData();

        dataAppointments.importData();
        dataAppointments.reloadData();

        dataAppointmentSlots.importData();
        dataAppointmentSlots.reloadData();

        leaveData.reloadData();

        // Print the doctor menu to the console
        AuthorizationControl authControl = new AuthorizationControl();
        // Load users into AuthorizationControl (assuming you have this in your code)
        authControl.loadCredentialsFromStaff(data, patientData); 

        StaffLeaveRequestUI leaveRequestUI = new StaffLeaveRequestUI(leaveData);

        while (true) {
            System.out.println("Doctor Menu");
            System.out.println("==================================="); // Add a border for the login section
            System.out.println("1. View Patient Medical Records");
            System.out.println("2. Update Patient Medical Records");
            System.out.println("3. View Personal Schedule");
            System.out.println("4. Set Availability for Appointments");
            System.out.println("5. Accept or Decline Appointment Requests");
            System.out.println("6. View Upcoming Appointments");
            System.out.println("7. Record Appointment Outcome");
            System.out.println("8. Apply for Leave");
            System.out.println("9. Change Password");
            System.out.println("10. Logout");
            System.out.println("===================================");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("\n==================================="); 
                    System.out.println("View Patient Medical Records");
                    System.out.println("===================================\n"); // Add a border for the login section
                    MedicalManagement medicalManagement = new MedicalManagement();
                    medicalManagement.viewOwnPatientMedicalRecords();

                    break;
                case 2:
                    System.out.println("\n==================================="); 
                    System.out.println("Update Patient Medical Records");
                    System.out.println("===================================\n"); // Add a border for the login section
                    MedicalManagement medicalManagement1 = new MedicalManagement();
                    medicalManagement1.updateMedicalRecord();
                    break;
                case 3:
                    // Add code to view personal schedule
                    System.out.println("\n==================================="); 
                    System.out.println("Viewing personal schedule");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentSlotManagementControl appointmentSlotManagement = new AppointmentSlotManagementControl(data, dataAppointmentSlots, dataAppointments, leaveData);
                    appointmentSlotManagement.viewPersonalSchedule();
                    break;
                case 4:
                    // Add code to set availability for appointments
                    System.out.println("\n==================================="); 
                    System.out.println("Setting availability for appointments");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentSlotManagementControl appointmentSlotManagementControl = new AppointmentSlotManagementControl(data, dataAppointmentSlots, dataAppointments, leaveData);
                    appointmentSlotManagementControl.setDoctorAvailability();
                    break;
                case 5:
                    System.out.println("\n==================================="); 
                    System.out.println("Accepting or declining appointment requests");
                    System.out.println("===================================\n"); // Add a border for the login section

                    AppointmentManagementControl appointmentManagementControl = new AppointmentManagementControl(medicineData,data,dataAppointments);
                    appointmentManagementControl.acceptOrDeclineAppointment();
                    break;
                case 6:
                    // Add code to view upcoming appointments
                    System.out.println("\n==================================="); 
                    System.out.println("View Upcoming Appointments");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentManagementControl appointmentManagementControl2 = new AppointmentManagementControl(medicineData,data,dataAppointments);
                    appointmentManagementControl2.viewAppointments(true);
                    break;
                case 7:
                    // Add code to record appointment outcome
                    System.out.println("\n==================================="); 
                    System.out.println("Recording appointment outcome");
                    System.out.println("===================================\n"); // Add a border for the login section
                    AppointmentManagementControl appointmentManagementControls = new AppointmentManagementControl(medicineData,data,dataAppointments);
                    appointmentManagementControls.outcomeRecordUpdate();

                    break;
                // case 8:
                //     // Add code to view appointment outcome records
                //     System.out.println("\n==================================="); 
                //     System.out.println("Viewing past appointment outcome records");
                //     System.out.println("===================================\n"); // Add a border for the login section
                //     AppointmentManagementControl appointmentManagementControl3 = new AppointmentManagementControl(medicineData,data,dataAppointments);
                //     appointmentManagementControl3.viewOutcomeRecords(false);
                //     break;
                case 8:
                    // Apply for leave
                    System.out.println("\n===================================");
                    System.out.println("Applying for Leave");
                    System.out.println("===================================");
                    leaveRequestUI.manageLeaveRequests(doctor.getUserID());
                    break;

                case 9:
                    // Add code to change password
                    System.out.println("\n==================================="); 
                    System.out.println("Changing password");
                    System.out.println("===================================\n"); // Add a border for the login section
                    PasswordManagement passwordManagement = new PasswordManagement(data, patientData);
                    //staffManagementControl.changeStaffPassword(scanner);
                    passwordManagement.changePassword(scanner);
                    break;
                case 10:
                    System.out.println("Logging Out...");
                    return; // Logout
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }




}
