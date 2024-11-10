package leave;

import enums.LeaveRequestStatus;
import java.time.LocalDate;

public class LeaveRequest {
    private String leaveRequestID;
    private String requesterID;
    private LocalDate leaveDate;
    private String reason;
    private LeaveRequestStatus status;

    // Constructor for creating a new leave request with specified ID and status
    public LeaveRequest(String leaveRequestID, String requesterID, LocalDate leaveDate, String reason, LeaveRequestStatus status) {
        this.leaveRequestID = leaveRequestID;
        this.requesterID = requesterID;
        this.leaveDate = leaveDate;
        this.reason = reason;
        this.status = status;
    }

    // Constructor for new leave requests with default status (PENDING)
    public LeaveRequest(String leaveRequestID, String requesterID, LocalDate leaveDate, String reason) {
        this(leaveRequestID, requesterID, leaveDate, reason, LeaveRequestStatus.PENDING);
    }

    // Getters and Setters
    public String getLeaveRequestID() { 
        return leaveRequestID; 
    }

    public String getRequesterID() { 
        return requesterID; 
    }

    public LocalDate getLeaveDate() { 
        return leaveDate; 
    }

    public String getReason() { 
        return reason; 
    }

    public LeaveRequestStatus getStatus() { 
        return status; 
    }

    public void setStatus(LeaveRequestStatus status) { 
        this.status = status; 
    }
}
