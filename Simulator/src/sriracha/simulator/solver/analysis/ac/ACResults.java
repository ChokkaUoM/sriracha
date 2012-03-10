package sriracha.simulator.solver.analysis.ac;

import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.analysis.IAnalysisResults;
import sriracha.simulator.solver.analysis.IResultVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the results for a specific .AC analysis request
 */
public class ACResults implements IAnalysisResults
{

    private List<IResultVector> data;

    public ACResults()
    {
        data = new ArrayList<IResultVector>();
    }

    public void addVector(double w, IComplexVector vector)
    {
        data.add(new ACResult(w, vector));
    }


    @Override
    public List<IResultVector> getData()
    {
        return data;
    }

}

