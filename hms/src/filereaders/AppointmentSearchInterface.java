package filereaders;
import java.io.IOException;

import appointments.*;

public interface AppointmentSearchInterface {
    Appointment findAppointment(String filename, String appointmentID) throws IOException;
}
