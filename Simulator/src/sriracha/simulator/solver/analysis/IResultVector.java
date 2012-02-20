package sriracha.simulator.solver.analysis;

import sriracha.math.interfaces.IComplexVector;

public interface IResultVector {

    /**
     * gets the x value this analysis was done against.
     * frequency for .AC, time for .TRANS ...
     */
    public double getX();

    /**
     * @return - The completed Solved Vector
     */
    public IComplexVector getData();

}
