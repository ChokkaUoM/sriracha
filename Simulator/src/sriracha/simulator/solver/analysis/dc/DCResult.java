package sriracha.simulator.solver.analysis.dc;

import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.solver.analysis.IResultVector;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 3/8/12
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class DCResult implements IResultVector
{
    private double dcValue;
    private IRealVector vector;

    public DCResult(double dcValue, IRealVector vector)
    {
        this.dcValue = dcValue;
        this.vector = vector;
    }

    public double getDcValue()
    {
        return dcValue;
    }

    public IRealVector getVector()
    {
        return vector;
    }

    @Override
    public double getX()
    {
        return dcValue;
    }

    @Override
    public IRealVector getData()
    {
        return vector;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
