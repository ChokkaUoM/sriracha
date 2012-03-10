import sriracha.simulator.IDataPoint;
import sriracha.simulator.IPrintData;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;


class GnuplotFileMaker
{

    private List<IPrintData> results;

    private String cmdFile;

    private String[] dataFiles;
    private String[] spiceFiles;

    public GnuplotFileMaker(String cmdFile)
    {
        this.cmdFile = cmdFile;
    }

    public GnuplotFileMaker(List<IPrintData> results, String cmdFile)
    {
        this.results = results;
        this.cmdFile = cmdFile;
    }

    public GnuplotFileMaker(List<IPrintData> results)
    {

        this.results = results;
    }


    public GnuplotFileMaker()
    {
    }


    public void setCmdFile(String cmdFile)
    {
        this.cmdFile = cmdFile;
    }

    public void setResults(List<IPrintData> results)
    {
        this.results = results;
    }

    public void writeFiles()
    {
        generateDataFileNames();

        writeDataFiles();

        writeCmdFile();
    }

    private void generateDataFileNames()
    {
        dataFiles = new String[results.size()];
        spiceFiles = new String[results.size()];
        for (int i = 0; i < results.size(); i++)
        {
            dataFiles[i] = "scs.out" + (i + 1);
            spiceFiles[i] = "spice.out" + (i + 1);
        }
    }

    private void writeCmdFile()
    {
        BufferedOutputStream stream = openNew(cmdFile);

        StringBuilder fileContent = new StringBuilder();

        for (int i = 0; i < results.size(); i++)
        {


            fileContent.append("set term wxt " + i + "\n");
            fileContent.append("plot ");

            int columns = results.get(i).getData().get(0).totalVectorLength();
            for (int j = 0; j < columns; j++)
            {
                fileContent.append(quote(dataFiles[i]) + " using 1:" + (j + 2) + " with lines, ");
                fileContent.append(quote(spiceFiles[i]) + " using 1:" + (j + 2) + " with points");
                if (j + 1 < columns)
                {
                    fileContent.append(", ");
                }
            }
            fileContent.append("\n");


        }

        try
        {
            stream.write(fileContent.toString().getBytes());
        } catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally
        {
            try
            {
                stream.flush();
                stream.close();
            } catch (IOException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    private static String quote(String str)
    {
        return '\"' + str + '\"';
    }


    private void writeDataFiles()
    {
        int resCount = 0;

        for (IPrintData data : results)
        {
            BufferedOutputStream stream = openNew(dataFiles[resCount++]);
            try
            {
                for (IDataPoint point : data.getData())
                {
                    stream.write(format(point.getXValue()).getBytes());
                    stream.write('\t');

                    for (double[] vals : point.getVector())
                    {
                        for (double v : vals)
                        {
                            stream.write(format(v).getBytes());
                            stream.write('\t');
                        }
                    }
                    stream.write('\n');

                }

                //  stream.write(data.toString().getBytes());

            } catch (IOException e)
            {
                e.printStackTrace();
            } finally
            {
                try
                {
                    stream.flush();
                    stream.close();
                } catch (IOException e)
                {
                }
            }
        }
    }


    private BufferedOutputStream openNew(String path)
    {
        File file = new File(path);

        //always overwrite
        if (file.exists()) file.delete();

        try
        {
            file.createNewFile();
        } catch (IOException e)
        {
        }


        try
        {
            return new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e)
        {
        }

        return null;

    }


    private static String format(double val)
    {
        DecimalFormat format = new DecimalFormat("0.000000E00");
        return format.format(val);
    }

}
