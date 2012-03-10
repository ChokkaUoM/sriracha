package sriracha.simulator.solver.analysis.dc;

import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.solver.analysis.IAnalysisResults;
import sriracha.simulator.solver.analysis.IResultVector;

import java.util.ArrayList;
import java.util.List;

public class DCResults implements IAnalysisResults
{
    private List<IResultVector> data;

    public DCResults()
    {
        data = new ArrayList<IResultVector>();
    }

    public void addVector(double dcValue, IRealVector vector)
    {
        data.add(new DCResult(dcValue, vector));
    }


    @Override
    public List<IResultVector> getData()
    {
        return data;
    }

}
