package sriracha.simulator.solver.analysis;

import java.util.List;

/**
 * Contains the results of an analysis specification line in the netlist
 * ex ".AC 100 1000 2000"
 */
public interface IAnalysisResults
{

    public List<IResultVector> getData();


}
