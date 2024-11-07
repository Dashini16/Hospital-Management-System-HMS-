package databackups;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;

public class DatabaseBackUp {

    public DatabaseBackUp() {}

    // Method to create a timestamp string
    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    public static void backupDatabase() {
        // Define the source and backup directories
        File sourceDirectory = new File("hms/src/data");
        File backupDirectory = new File("hms/src/backup_data");

        // Create the backup directory if it doesn't exist
        if (!backupDirectory.exists()) {
            backupDirectory.mkdirs();
        }

        // Get all CSV files in the source directory
        File[] csvFiles = sourceDirectory.listFiles((dir, name) -> name.endsWith(".csv"));

        // Check if there are CSV files to back up
        if (csvFiles != null && csvFiles.length > 0) {
            // Generate a timestamp for the backup zip file
            String timestamp = getTimeStamp();
            File zipFile = new File(backupDirectory, "backup_" + timestamp + ".zip");

            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
                for (File csvFile : csvFiles) {
                    try (FileInputStream fis = new FileInputStream(csvFile)) {
                        // Create a new zip entry for each CSV file
                        ZipEntry zipEntry = new ZipEntry(csvFile.getName());
                        zipOut.putNextEntry(zipEntry);

                        // Read the CSV file and write it into the zip file
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zipOut.write(buffer, 0, length);
                        }
                        zipOut.closeEntry();
                    } catch (IOException e) {
                        System.err.println("Failed to add file to zip: " + csvFile.getName());
                        e.printStackTrace();
                    }
                }
                System.out.println("Backup completed. ZIP file: " + zipFile.getName());
            } catch (IOException e) {
                System.err.println("Failed to create ZIP file.");
                e.printStackTrace();
            }
        } else {
            System.out.println("No CSV files found to back up.");
        }
    }
}
