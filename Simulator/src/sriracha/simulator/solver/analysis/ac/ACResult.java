package sriracha.simulator.solver.analysis.ac;

import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.analysis.IResultVector;

/**
 * Contains the results for a single frequency point at all nodes
 */
public class ACResult implements IResultVector
{
    private double w;
    private IComplexVector vector;

    ACResult(double w, IComplexVector vector)
    {
        this.w = w;
        this.vector = vector;
    }

    @Override
    public double getX()
    {
        return w;
    }


    @Override
    public IComplexVector getData()
    {
        return vector;
    }
}
