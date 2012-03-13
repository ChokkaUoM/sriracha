package sriracha.simulator.solver.analysis;

import sriracha.math.interfaces.IVector;

public interface IResultVector
{

    /**
     * gets the x value this analysis was done against.
     * frequency for .AC, voltage/current for .DC ...
     */
    public double getX();

    /**
     * @return - The entire solution vector
     */
    public IVector getData();

}
