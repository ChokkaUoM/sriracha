import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class FileReader
{

    private String path;


    public FileReader(String path)
    {
        this.path = path;
    }


    public String getContents()
    {

        File file = new File(path);

        //  System.out.println(file.getAbsolutePath());

        if (!file.isFile()) return null;

        StringBuilder fileContents = new StringBuilder((int) file.length());


        Scanner scanner = null;
        try
        {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e)
        {
            //will not happen
        }

        if (scanner == null) return null;

        String lineSeparator = System.getProperty("line.separator");

        try
        {
            while (scanner.hasNextLine())
            {
                fileContents.append(scanner.nextLine());
                fileContents.append(lineSeparator);
            }
            return fileContents.toString();
        } finally
        {
            scanner.close();
        }
    }


}
