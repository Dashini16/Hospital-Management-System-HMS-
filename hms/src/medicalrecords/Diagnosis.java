package medicalrecords;

public class Diagnosis {
	private String diagnosisID;
	private String description;
	
	public Diagnosis(String diagnosisID, String description) {
		this.diagnosisID = diagnosisID;
		this.description = description;
	}
	
	public String getDiagnosisID() {
		return diagnosisID;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDiagnosisID(String diagnosisID) {
		this.diagnosisID = diagnosisID;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
