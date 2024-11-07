package filereaders;


import medicinemanagements.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InitialDataMedicine implements DataImporter, DataExporterAppend<Medicine>, ListInterface<Medicine> {
    private List<Medicine> medicines; // Add this line

    public InitialDataMedicine() {
        medicines = new ArrayList<>(); // Initialize the medicines list

    }

    @Override
    public void importData() {
        try {
            importDatafromFile("hms\\src\\data\\Medicine_List.csv"); // Import medicine list


        } catch (IOException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
    }

    @Override
    public void reloadData() {
        medicines.clear();

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
                String[] data = line.split(",");
                if (data.length == 3) {
                    String name = data[0];
                    int initialStock = Integer.parseInt(data[1].trim()); // Convert to integer
                    int lowStockLevel = Integer.parseInt(data[2].trim()); // Convert to integer
                    Medicine medication = new Medicine(name, initialStock, lowStockLevel);
                    medicines.add(medication); // Add to medicines list
                }
            }
        }
    }

    @Override
    // Method to append a new medicine to the file
    public void appendData(String filename, Medicine medicine) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(medicine.getName() + "," + medicine.getInitialStock() + "," + medicine.getLowStockLevelAlert() + "\n");
            medicines.add(medicine); // Update the list in memory
        }
    }

 
    public void rewriteMedicines(String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // Write the header
            bw.write("Name,InitialStock,LowStockLevelAlert\n");
    
            // Write each medicine's details
            for (Medicine medicine : medicines) {
                bw.write(medicine.getName() + "," + medicine.getInitialStock() + "," + medicine.getLowStockLevelAlert() + "\n");
            }
        }
    }

    @Override
    public List<Medicine> getLists() {  // Add this method
        return medicines;
    }

}