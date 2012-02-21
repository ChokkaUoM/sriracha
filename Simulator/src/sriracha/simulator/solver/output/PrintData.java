package sriracha.simulator.solver.output;

import sriracha.simulator.IDataPoint;
import sriracha.simulator.IPrintData;

import java.util.ArrayList;
import java.util.List;

/**
 * Similar to a IAnalysisResults object except this one has been filtered
 * by a .PRINT statement.
 *
 * This is the implementation for the IPrintData interface which is exposed to the frontend
 * for returning results.
 */
public class PrintData implements IPrintData {

    private List<IDataPoint> data;

    public PrintData() {
        data = new ArrayList<IDataPoint>();
    }

    public void addResult(IDataPoint vector){
        data.add(vector);
    }

    @Override
    public List<IDataPoint> getData() {
        return data;
    }


    @Override
    public String toString() {
        return data.toString();
    }
}

