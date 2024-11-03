package appointments;

public class AppointmentOutcome {
    private String appointmentID;
    private String notes;
    private Prescription[] prescribedMedication;
    private int index;

    public AppointmentOutcome(String ID){
        this.appointmentID = ID;
        this.index = -1; // Start with no prescriptions
    }
    
    // Add the first prescription
    public void giveNewPrescription(int n, Prescription newMeds) {
        this.prescribedMedication = new Prescription[n];
        this.prescribedMedication[0] = newMeds;
        this.index = 0; // Start index
    }

    // Add new prescription (not first prescription)
    public void addPrescription (Prescription newMeds){
        if (index < prescribedMedication.length - 1) {
            prescribedMedication[++index] = newMeds;
        } else {
            System.out.println("No more prescriptions can be added.");
        }
    }

    public String getID(){
        return this.appointmentID;
    }

    public void setNotes(String doctorsNotes) {
        this.notes = doctorsNotes;
    }

    public String getNotes() {
        return this.notes;
    }


}
