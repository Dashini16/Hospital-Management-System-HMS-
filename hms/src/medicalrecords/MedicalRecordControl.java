package medicalrecords;

import java.util.HashMap;
import java.util.Map;
import appointments.Prescription;


public class MedicalRecordControl {
	
	//Store records using patientID as the key
	private Map<String, MedicalRecord> records = new HashMap<>();
	
	public void viewMedicalRecord(String patientID) {
		MedicalRecord patientRecord = records.get(patientID);
		
		if (patientRecord != null) {
		    System.out.println(patientRecord);
		}
		else {
			System.out.printf("No record found for patient ID: %s\n", patientID);
		}
	}
	
	public void updateMedicalRecord(String patientID, Diagnosis diagnosis, Treatment treatment, Prescription prescription) {
		MedicalRecord patientRecord = records.computeIfAbsent(patientID, MedicalRecord::new);
		
		//Update record
		if(diagnosis != null) patientRecord.addDiagnosis(diagnosis);
		if(treatment != null) patientRecord.addTreatment(treatment);
		if(prescription != null) patientRecord.addPrescription(prescription);
		
		System.out.printf("Updated record for patient ID: %s\n", patientID);
			
	}

}
