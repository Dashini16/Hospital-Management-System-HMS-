package medicalrecords;

public class Treatment {
	private String treatmentID;
	private String description;
	
	public Treatment(String treatmentID, String description) {
		this.treatmentID = treatmentID;
		this.description = description;
	}
	
	public String gettreatmentID() {
		return treatmentID;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDiagnosisID(String treatmentID) {
		this.treatmentID = treatmentID;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
