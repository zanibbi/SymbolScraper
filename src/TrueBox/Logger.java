package TrueBox;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.List;

class Logger {

    String filePath = System.getProperty("user.dir");

    Logger(String filename) {
        this.filePath = System.getProperty("user.dir") + "/" + filename;
    }

    public void writeBatchLog(List<String[]> data) {
        // first create file object for file placed at location 
        // specified by filepath 
        File file = new File(filePath); 
    
        try { 
            // create FileWriter object with file as parameter 
            FileWriter outputfile = new FileWriter(file); 
    
            // create CSVWriter with no separator 
            CSVWriter writer = new CSVWriter(outputfile); 
                                             
            writer.writeAll(data); 
    
            // closing writer connection 
            writer.close(); 
        } 
        catch (IOException e) { 
            e.printStackTrace(); 
        } 
    }
}