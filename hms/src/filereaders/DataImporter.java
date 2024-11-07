package filereaders;
import java.io.IOException;
import appointments.*;

public interface DataImporter {
    void importDatafromFile(String filename) throws IOException;
    void reloadData();
    void importData();
}