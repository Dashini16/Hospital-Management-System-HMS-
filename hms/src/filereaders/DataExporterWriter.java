package filereaders;
import java.io.IOException;
import appointments.*;

public interface DataExporterWriter<T> {
    void writeData(String filename, T object) throws IOException;
}