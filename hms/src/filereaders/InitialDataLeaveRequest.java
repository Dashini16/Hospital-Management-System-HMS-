package filereaders;

import leave.LeaveRequest;
import enums.LeaveRequestStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InitialDataLeaveRequest {
    private List<LeaveRequest> leaveRequests;
    private final String filename = "hms/src/data/Leave_Requests.csv"; // Path to the CSV file

    public InitialDataLeaveRequest() {
        leaveRequests = new ArrayList<>();
        reloadData(); // Load data when initialized
    }

    // Load data from CSV into leaveRequests list
    public void reloadData() {
        leaveRequests.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip the header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    String requestID = data[0].trim();   // Unique LeaveRequestID
                    String requesterID = data[1].trim(); // Staff ID (RequesterID)
                    LocalDate leaveDate = LocalDate.parse(data[2].trim());
                    LeaveRequestStatus status = LeaveRequestStatus.valueOf(data[3].trim().toUpperCase());
                    String reason = data[4].trim();
                    leaveRequests.add(new LeaveRequest(requestID, requesterID, leaveDate, reason, status));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading leave requests: " + e.getMessage());
        }
    }

    // Append a new leave request to the CSV file and list
    public void addRequest(LeaveRequest leaveRequest) {
        leaveRequests.add(leaveRequest);
        saveRequestToFile(leaveRequest);
    }

    private void saveRequestToFile(LeaveRequest leaveRequest) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            // Write the new request in the correct CSV format and ensure it's on a new line
            bw.write(formatRequestAsCSV(leaveRequest));
            bw.newLine();  // Ensure the next entry starts on a new line
        } catch (IOException e) {
            System.out.println("Error saving leave request: " + e.getMessage());
        }
    }

    // Find a specific leave request by its unique LeaveRequestID
    public LeaveRequest findRequestByID(String leaveRequestID) {
        return leaveRequests.stream()
            .filter(request -> request.getLeaveRequestID().equals(leaveRequestID)) // Use LeaveRequestID
            .findFirst()
            .orElse(null);
    }

    // Update an existing leave request
    public void updateRequest(LeaveRequest updatedRequest) {
        leaveRequests.removeIf(request -> request.getLeaveRequestID().equals(updatedRequest.getLeaveRequestID())); // Use LeaveRequestID
        leaveRequests.add(updatedRequest);
        saveAllRequests(); // Re-save all requests after updating
    }

    // Save all leave requests to the CSV file (used for updates)
    private void saveAllRequests() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("RequestID,RequesterID,LeaveDate,Status,Reason");
            bw.newLine();  // Ensure the header is written first
            for (LeaveRequest request : leaveRequests) {
                String csvLine = formatRequestAsCSV(request);
                bw.write(csvLine);
                bw.newLine();  // Ensure each request is on a new line
            }
        } catch (IOException e) {
            System.out.println("Error updating leave requests: " + e.getMessage());
        }
    }

    // Format a LeaveRequest as a CSV line
    private String formatRequestAsCSV(LeaveRequest request) {
        return String.join(",", request.getLeaveRequestID(), request.getRequesterID(), request.getLeaveDate().toString(), request.getStatus().name(), request.getReason());
    }

    // Get all leave requests
    public List<LeaveRequest> getAllRequests() {
        return new ArrayList<>(leaveRequests);
    }
}
