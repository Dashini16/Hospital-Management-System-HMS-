package filereaders;

import medicinemanagements.*;
import enums.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;




public class InitialDatareplenishmentRequest implements DataImporter,ListInterface<ReplenishmentRequest>, DataExporterAppend<ReplenishmentRequest> {

    private List<ReplenishmentRequest> replenishmentRequests; // Add this line



    public InitialDatareplenishmentRequest() {
        replenishmentRequests = new ArrayList<>(); // Initialize the request list

    }

    @Override
    public void importData() {
        try {


            importDatafromFile("hms\\src\\data\\Replenishment_Requests.csv");

        } catch (IOException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
    }

    @Override
    public void reloadData() {

        replenishmentRequests.clear();

        // Reload data from files
        importData(); 
        //System.out.println("Data reloaded successfully.");
    }

    @Override
    public void appendData(String filename, ReplenishmentRequest request) throws IOException {
        boolean fileExists = new File(filename).exists();
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            // If the file doesn't exist, write a header first
            if (!fileExists) {
                bw.write("MedicineName,RequestedQuantity,Status\n");
            }
            // Append the request data
            bw.write(request.getMedicineName() + "," + request.getRequestedStock() + "," + request.getStatus() + "\n");
        }
    }
    
    public void rewriteReplenishmentRequests(String filename, List<ReplenishmentRequest> requests) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write the header
            bw.write("MedicineName,RequestedQuantity,Status\n");
    
            // Write each replenishment request's details
            for (ReplenishmentRequest request : requests) {
                bw.write(request.getMedicineName() + "," + request.getRequestedStock() + "," + request.getStatus() + "\n");
            }
        }
    }

    @Override
    public void importDatafromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String medicineName = data[0];
                    int requestedQuantity = Integer.parseInt(data[1].trim());
                    RequestStatus status = RequestStatus.valueOf(data[2].trim().toUpperCase());
                    ReplenishmentRequest request = new ReplenishmentRequest(medicineName, requestedQuantity);
                    request.setStatus(status);
                    replenishmentRequests.add(request);
                }
            }
        }
    }

    @Override

    public List<ReplenishmentRequest> getLists()
    {
        return replenishmentRequests;
    }

}