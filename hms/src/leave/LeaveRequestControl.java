// LeaveRequestControl.java

package leave;

import filereaders.InitialDataLeaveRequest;
import enums.LeaveRequestStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class LeaveRequestControl {
    private InitialDataLeaveRequest leaveData;

    public LeaveRequestControl(InitialDataLeaveRequest leaveData) {
        this.leaveData = leaveData;
    }

    // Submits a new leave request with a unique ID
    public void submitLeaveRequest(String staffID, LocalDate leaveDate, String reason) {
        String leaveRequestID = generateUniqueLeaveRequestID(); // Ensure ID is in the correct format "LR" + timestamp
        LeaveRequest leaveRequest = new LeaveRequest(leaveRequestID, staffID, leaveDate, reason);
        leaveData.addRequest(leaveRequest);
        System.out.println("Leave request submitted successfully with ID: " + leaveRequestID);
    }

    // Views all leave requests for a specific staff member
    public void viewLeaveRequests(String staffID) {
        List<LeaveRequest> staffRequests = leaveData.getAllRequests().stream()
            .filter(request -> request.getRequesterID().equals(staffID))
            .collect(Collectors.toList());
    
        if (staffRequests.isEmpty()) {
            System.out.println("No leave requests found.");
        } else {
            System.out.printf("%-5s | %-15s | %-10s | %-30s\n", "No.", "Leave Date", "Status", "Reason");
            System.out.println("---------------------------------------------------------------");
            
            int index = 1;
            for (LeaveRequest request : staffRequests) {
                System.out.printf(
                    "%-5d | %-15s | %-10s | %-30s\n",
                    index++,
                    request.getLeaveDate(),
                    request.getStatus(),
                    request.getReason()
                );
            }
        }
    }

    // Generates a unique leave request ID in the format "LR" + timestamp
    private String generateUniqueLeaveRequestID() {
        return "LR" + System.currentTimeMillis(); // Correct format is "LR" + timestamp
    }
}
