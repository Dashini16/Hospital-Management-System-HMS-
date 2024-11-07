package filereaders;

import java.io.IOException;


public interface DataExporterAppend<T> {
    void appendData(String filename, T object) throws IOException;
}
