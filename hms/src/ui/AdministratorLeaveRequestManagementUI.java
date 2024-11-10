package ui;

import leave.AdministratorLeaveRequestControl;
import filereaders.InitialDataLeaveRequest;
import filereaders.InitialDataStaff;

import java.util.Scanner;

public class AdministratorLeaveRequestManagementUI {
    private AdministratorLeaveRequestControl leaveRequestControl;

    public AdministratorLeaveRequestManagementUI(InitialDataLeaveRequest leaveData, InitialDataStaff staffData) {
        this.leaveRequestControl = new AdministratorLeaveRequestControl(leaveData, staffData);
    }

    public void manageLeaveRequests() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\nLeave Request Management:");
            System.out.println("1. View Pending Leave Requests");
            System.out.println("2. Approve or Decline Pending Leave Request");
            System.out.println("3. Exit to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("\n===================================");
                    System.out.println("Pending Leave Requests");
                    System.out.println("===================================\n");
                    leaveRequestControl.viewPendingLeaveRequests();
                    break;

                case 2:
                    System.out.println("\n===================================");
                    System.out.println("Approve or Decline Pending Leave Request");
                    System.out.println("===================================\n");
                    leaveRequestControl.reviewPendingLeaveRequest(scanner);
                    break;

                case 3:
                    System.out.println("Returning to Main Menu...");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
