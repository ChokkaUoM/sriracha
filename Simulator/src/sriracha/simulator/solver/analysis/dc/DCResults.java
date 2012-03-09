package sriracha.simulator.solver.analysis.dc;

import sriracha.math.interfaces.IComplexVector;
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

    @Override
    public void addVector(double w, IComplexVector vector)
    {
        //  data.add(new DCResult(w, vector));
    }


    @Override
    public List<IResultVector> getData()
    {
        return data;
    }

}
