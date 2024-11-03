package appointments;

public class AppointmentOutcome {
    private String appointmentID;
    private String notes;
    private Prescription[] prescribedMedication;
    private int index;

    public AppointmentOutcome(String ID){
        this.appointmentID = ID;
        this.prescribedMedication = new Prescription[10]; // Assuming a max of 10 prescriptions
        this.index = -1; // Start with no prescriptions
    }
    
    public void giveNewPrescription(int n, Prescription newMeds) {
        this.prescribedMedication = new Prescription[n];
        this.prescribedMedication[0] = newMeds; // Add the first prescription
        this.index = 0; // Start index
    }

    public void addPrescription (Prescription newMeds){
        if (index < prescribedMedication.length - 1) {
            prescribedMedication[++index] = newMeds; // Add new prescription
        } else {
            System.out.println("No more prescriptions can be added.");
        }
    }

    public void setNotes(String doctorsNotes) {
        this.notes = doctorsNotes;
    }

    public String getNotes() {
        return this.notes;
    }


}
