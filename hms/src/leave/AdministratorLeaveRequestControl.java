package leave;

import filereaders.InitialDataLeaveRequest;
import filereaders.InitialDataStaff;
import enums.LeaveRequestStatus;
import users.Users;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AdministratorLeaveRequestControl {
    private InitialDataLeaveRequest leaveData;
    private InitialDataStaff staffData;

    public AdministratorLeaveRequestControl(InitialDataLeaveRequest leaveData, InitialDataStaff staffData) {
        this.leaveData = leaveData;
        this.staffData = staffData;
    }

    // Display only pending leave requests in a formatted way with requester's name
    public void viewPendingLeaveRequests() {
        List<LeaveRequest> pendingRequests = leaveData.getAllRequests().stream()
            .filter(request -> request.getStatus() == LeaveRequestStatus.PENDING)
            .collect(Collectors.toList());

        if (pendingRequests.isEmpty()) {
            System.out.println("No pending leave requests found.");
            return;
        }

        System.out.printf("%-5s | %-12s | %-15s | %-10s | %-15s | %-30s\n", "No.", "Request ID", "Requester Name", "Date", "Status", "Reason");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (int i = 0; i < pendingRequests.size(); i++) {
            LeaveRequest request = pendingRequests.get(i);
            Users requester = staffData.getStaffList().stream()
                .filter(staff -> staff.getUserID().equals(request.getRequesterID()))
                .findFirst()
                .orElse(null);
            String requesterName = (requester != null) ? requester.getName() : "Unknown";

            System.out.printf("%-5d | %-12s | %-15s | %-10s | %-15s | %-30s\n",
                    (i + 1),
                    request.getLeaveRequestID(),
                    requesterName,
                    request.getLeaveDate(),
                    request.getStatus(),
                    request.getReason());
        }
    }

    // Review and approve or decline a leave request by selecting from a list of pending requests
    public void reviewPendingLeaveRequest(Scanner scanner) {
        List<LeaveRequest> pendingRequests = leaveData.getAllRequests().stream()
            .filter(request -> request.getStatus() == LeaveRequestStatus.PENDING)
            .collect(Collectors.toList());

        if (pendingRequests.isEmpty()) {
            System.out.println("No pending leave requests available to review.");
            return;
        }

        // Display pending requests to choose from
        viewPendingLeaveRequests();
        System.out.print("\nEnter the number of the leave request to review: ");
        int requestNumber = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (requestNumber < 1 || requestNumber > pendingRequests.size()) {
            System.out.println("Invalid choice. Please select a valid request number.");
            return;
        }

        LeaveRequest selectedRequest = pendingRequests.get(requestNumber - 1);

        // Prompt for decision
        System.out.print("Enter 'A' to Approve or 'D' to Decline: ");
        String decision = scanner.nextLine().trim().toUpperCase();

        if ("A".equals(decision)) {
            updateLeaveRequestStatus(selectedRequest, LeaveRequestStatus.APPROVED);
        } else if ("D".equals(decision)) {
            updateLeaveRequestStatus(selectedRequest, LeaveRequestStatus.REJECTED);
        } else {
            System.out.println("Invalid input. Please enter 'A' to Approve or 'D' to Decline.");
        }
    }

    // Update the status of the specified leave request
    private void updateLeaveRequestStatus(LeaveRequest request, LeaveRequestStatus newStatus) {
        request.setStatus(newStatus);
        leaveData.updateRequest(request);
        System.out.println("Leave request " + request.getLeaveRequestID() + " " + newStatus + " successfully.");
    }
}
