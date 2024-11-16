package reportmanagement;

import filereaders.InitialDataPatient;
import users.Patient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

public class PatientReportControl {
    private InitialDataPatient dataPatient;

    public PatientReportControl(InitialDataPatient dataPatient) {
        this.dataPatient = dataPatient;
        dataPatient.reloadData();
    }

    // Age Distribution
    public void displayAgeDistribution() {
        Map<String, Integer> ageGroups = getAgeDistribution(dataPatient.getLists());
        System.out.println("\nAge Distribution of Patients:");
        printBarChart(ageGroups);
    }

    // Diagnosis Distribution
    public void displayDiagnosisDistribution() {
        Map<String, Integer> diagnosisCounts = getDiagnosisDistribution();
        System.out.println("\nDiagnosis Distribution of Patients (from highest to lowest):");
        printSortedBarChart(diagnosisCounts);
    }

    // Gender Distribution
    public void displayGenderDistribution() {
        Map<String, Integer> genderCounts = new HashMap<>();
        for (Patient patient : dataPatient.getLists()) {
            String gender = patient.getGender();
            genderCounts.put(gender, genderCounts.getOrDefault(gender, 0) + 1);
        }
        System.out.println("\nGender Distribution of Patients:");
        printBarChart(genderCounts);
    }

    // Common Treatments Distribution
    public void displayTreatmentDistribution() {
        Map<String, Integer> treatmentCounts = getTreatmentDistribution();
        System.out.println("\nTreatment Distribution of Patients (from highest to lowest):");
        printSortedBarChart(treatmentCounts);
    }

    // Average Age by Diagnosis

    public void displayAverageAgeByDiagnosis() {
        Map<String, Integer> diagnosisCounts = new HashMap<>();
        Map<String, Integer> diagnosisAgeSums = new HashMap<>();
        Map<String, List<String>> patientDiagnosesMap = getPatientDiagnosesMap();

        for (Map.Entry<String, List<String>> entry : patientDiagnosesMap.entrySet()) {
            Patient patient = dataPatient.findPatientById(entry.getKey());
            if (patient == null) {
                // System.err.println("Patient with ID " + entry.getKey() + " not found.
                // Skipping...");
                continue;
            }

            int age = calculateAge(patient.getDateOfBirth());
            for (String diagnosis : entry.getValue()) {
                diagnosisCounts.put(diagnosis, diagnosisCounts.getOrDefault(diagnosis, 0) + 1);
                diagnosisAgeSums.put(diagnosis, diagnosisAgeSums.getOrDefault(diagnosis, 0) + age);
            }
        }

        System.out.println("\nAverage Age by Diagnosis:");
        for (Map.Entry<String, Integer> entry : diagnosisCounts.entrySet()) {
            String diagnosis = entry.getKey();
            int count = entry.getValue();
            double avgAge = (double) diagnosisAgeSums.get(diagnosis) / count;
            System.out.printf("%-30s : %.2f years\n", diagnosis, avgAge);
        }
    }

    // Helper Methods
    private Map<String, Integer> getAgeDistribution(List<Patient> patients) {
        Map<String, Integer> ageGroups = new HashMap<>();
        for (Patient patient : patients) {
            int age = calculateAge(patient.getDateOfBirth());
            String group = getAgeGroup(age);
            ageGroups.put(group, ageGroups.getOrDefault(group, 0) + 1);
        }
        return ageGroups;
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    private String getAgeGroup(int age) {
        if (age < 18)
            return "0-17";
        else if (age < 30)
            return "18-29";
        else if (age < 50)
            return "30-49";
        else
            return "50+";
    }

    private Map<String, Integer> getDiagnosisDistribution() {
        Map<String, Integer> diagnosisCounts = new HashMap<>();
        Map<String, List<String>> patientDiagnosesMap = getPatientDiagnosesMap();

        for (List<String> diagnoses : patientDiagnosesMap.values()) {
            for (String diagnosis : diagnoses) {
                diagnosisCounts.put(diagnosis, diagnosisCounts.getOrDefault(diagnosis, 0) + 1);
            }
        }
        return diagnosisCounts;
    }

    private Map<String, Integer> getTreatmentDistribution() {
        Map<String, Integer> treatmentCounts = new HashMap<>();
        Map<String, List<String>> patientTreatmentsMap = getPatientTreatmentsMap();

        for (List<String> treatments : patientTreatmentsMap.values()) {
            for (String treatment : treatments) {
                treatmentCounts.put(treatment, treatmentCounts.getOrDefault(treatment, 0) + 1);
            }
        }
        return treatmentCounts;
    }

    private Map<String, List<String>> getPatientDiagnosesMap() {
        Map<String, List<String>> patientDiagnosesMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("hms/src/data/medical_records.csv"))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                String patientID = data[0];
                String[] diagnoses = data[1].split(",");
                patientDiagnosesMap.put(patientID, Arrays.asList(diagnoses));
            }
        } catch (IOException e) {
            System.out.println("Error reading medical records file: " + e.getMessage());
        }
        return patientDiagnosesMap;
    }

    private Map<String, List<String>> getPatientTreatmentsMap() {
        Map<String, List<String>> patientTreatmentsMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("hms/src/data/medical_records.csv"))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                String patientID = data[0];
                String[] treatments = data[2].split(",");
                patientTreatmentsMap.put(patientID, Arrays.asList(treatments));
            }
        } catch (IOException e) {
            System.out.println("Error reading medical records file: " + e.getMessage());
        }
        return patientTreatmentsMap;
    }

    private void printBarChart(Map<String, Integer> data) {
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            System.out.printf("%-20s : %s (%d)\n", entry.getKey(), generateBar(entry.getValue()), entry.getValue());
        }
    }

    private void printSortedBarChart(Map<String, Integer> data) {
        data.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("%-30s : %s (%d)\n", entry.getKey(), generateBar(entry.getValue()),
                        entry.getValue()));
    }

    private String generateBar(int count) {
        return "█".repeat(Math.max(0, count));
    }
}
