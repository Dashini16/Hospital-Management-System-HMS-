package medicalrecords;

import java.util.HashMap;
import java.util.Map;
import medicinemanagements.Medicine;


public class medicalRecordControl {
	
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
	
	public void updateMedicalRecord(String patientID, String diagnoses, String treatment, Medicine prescription, String status) {
		MedicalRecord patientRecord = records.computeIfAbsent(patientID, MedicalRecord::new);
		
		//Update record
		if(diagnoses != null) patientRecord.addDiagnosis(diagnoses);
		if(treatment != null) patientRecord.addTreatment(treatment);
		if(prescription != null) patientRecord.addPrescription(prescription);
		if(status != null) patientRecord.setPrescriptionStatus(status);
		
		System.out.printf("Updated record for patient ID: %s\n", patientID);
			
	}

}
