package appointments;
import java.util.ArrayList;
import java.util.List;

import filereaders.InitialData;
import users.Users;


public class AppointmentControl {
    private InitialData data;
    private List<Appointment> listOfAppointments;
    private Users currentUser;

    public String genAppointmentID(int appointmentCounter){
        String id;
        if (appointmentCounter<10) {
            id = "AP00"+ appointmentCounter+1;
        }else if(appointmentCounter<100){
            id = "AP0"+ appointmentCounter+1;
        } else{
            id = "AP"+ appointmentCounter+1;
        }
        return id;
    }

    public AppointmentControl(InitialData data, Users user){
        this.currentUser = user;
        this.data = data;

        this.listOfAppointments = data.getAppointments();
    }

    public void scheduleAppointment(String doctorid, String date, String time){
        Appointment newApp = new Appointment(genAppointmentID(listOfAppointments.size()), currentUser.getUserID(), doctorid, date, time);
        // process for appendAppointment
    }

    public void rescheduleAppointment(String appointmentID){
        // insert looping to fetch an appointment from listOfAppointments using appointmentID
        // process for rewriting Appointment (reschedule)
    }
    
    public void cancelAppointment(){
        
    }

    public void updateAppointmentStatus(String appointmentID){
        // insert looping to fetch an appointment from listOfAppointments using appointmentID
        // 
    }

    // private Appointment findAppointmentbyID(){
    //     for(Appointment appointment :listOfAppointments){

    //     }

    // }
    
}
