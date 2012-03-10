package sriracha.simulator.solver.analysis;

import sriracha.math.interfaces.IVector;

public interface IResultVector
{

    /**
     * gets the x value this analysis was done against.
     * frequency for .AC, time for .TRANS ...
     */
    public double getX();

    /**
     * @return - The completed Solved Vector
     */
    public IVector getData();

}
