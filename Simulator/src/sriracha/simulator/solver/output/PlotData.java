package sriracha.simulator.solver.output;

import sriracha.simulator.IDataPoint;
import sriracha.simulator.IPlotData;

import java.util.List;

/**
 * Similar to a IAnalysisResults object except this one has been filtered
 * by a .PLOT statement.
 *
 * This is the implementation for the IPlotData interface which is exposed to the frontend
 * for returning results.
 */
public class PlotData implements IPlotData {

    private List<IDataPoint> data;


    public void addResult(IDataPoint vector){
        data.add(vector);
    }

    @Override
    public List<IDataPoint> getData() {
        return data;
    }
}

