package sriracha.simulator.solver.analysis.dc;

import sriracha.math.interfaces.IRealVector;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 3/8/12
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class DCResult
{
    private double w;
    private IRealVector vector;

    public DCResult(double w, IRealVector vector)
    {
        this.w = w;
        this.vector = vector;
    }

    public double getW()
    {
        return w;
    }

    public IRealVector getVector()
    {
        return vector;
    }
}
