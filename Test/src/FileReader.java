import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {
    
    private String path;


    public FileReader(String path) {
        this.path = path;
    }


    public String getContents() {

        File file = new File(path);

        if(!file.isFile()) return null;

        StringBuilder fileContents = new StringBuilder((int)file.length());


        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            //will not happen
        }

        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    
    
    
}
