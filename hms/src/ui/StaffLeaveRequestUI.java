package ui;

import leave.LeaveRequestControl;
import filereaders.InitialDataLeaveRequest;

import java.time.LocalDate;
import java.util.Scanner;

public class StaffLeaveRequestUI {
    private LeaveRequestControl leaveRequestControl;

    public StaffLeaveRequestUI(InitialDataLeaveRequest leaveData) {
        this.leaveRequestControl = new LeaveRequestControl(leaveData);
        leaveData.reloadData();
    }

    public void manageLeaveRequests(String staffID) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nLeave Request Management:");
            System.out.println("1. Apply for Leave");
            System.out.println("2. View My Leave Requests");
            System.out.println("3. Exit to Main Menu");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("\n===================================");
                    System.out.println("Apply for Leave");
                    System.out.println("===================================\n");
                    applyForLeave(scanner, staffID);
                    break;

                case 2:
                    System.out.println("\n===================================");
                    System.out.println("My Leave Requests");
                    System.out.println("===================================\n");
                    leaveRequestControl.viewLeaveRequests(staffID);
                    break;

                case 3:
                    return; // Go back to main menu

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void applyForLeave(Scanner scanner, String staffID) {
        System.out.print("Enter leave date (YYYY-MM-DD): ");
        LocalDate leaveDate = LocalDate.parse(scanner.nextLine().trim());
        System.out.print("Enter reason for leave: ");
        String reason = scanner.nextLine().trim();

        leaveRequestControl.submitLeaveRequest(staffID, leaveDate, reason);
    }
}
