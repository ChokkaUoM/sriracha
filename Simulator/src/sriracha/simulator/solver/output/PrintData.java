package sriracha.simulator.solver.output;

import sriracha.simulator.IDataPoint;
import sriracha.simulator.IPrintData;

import java.util.ArrayList;
import java.util.List;

/**
 * Similar to a IAnalysisResults object except this one has been filtered
 * by a .PRINT statement.
 * <p/>
 * This is the implementation for the IPrintData interface which is exposed to the frontend
 * for returning results.
 */
public class PrintData implements IPrintData
{

    private ArrayList<IDataPoint> data;

    private ArrayList<String> labels;

    public PrintData()
    {
        data = new ArrayList<IDataPoint>();
        labels = new ArrayList<String>();
    }

    public void addResult(IDataPoint vector)
    {
        data.add(vector);
    }

    @Override
    public List<IDataPoint> getData()
    {
        return data;
    }

    @Override
    public List<String> getLabels()
    {
        return labels;
    }

    public void addLabel(String label)
    {
        labels.add(label);
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();


        for (String lbl : labels)
        {
            sb.append(lbl);
            sb.append("\t\t");

        }

        sb.append("\n");
        sb.append("================================================================================\n");


        for (IDataPoint point : data)
        {
            sb.append(point.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}

