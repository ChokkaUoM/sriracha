package sriracha.simulator.solver.analysis;

import sriracha.math.interfaces.IComplexVector;

import java.util.List;

/**
 * Contains the results of an analysis specification line in the netlist
 * ex ".AC 100 1000 2000"
 */
public interface IAnalysisResults
{


    public void addVector(double x, IComplexVector vector);

    public List<IResultVector> getData();


}
