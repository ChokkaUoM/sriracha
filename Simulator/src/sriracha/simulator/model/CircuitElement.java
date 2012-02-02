package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

/**
 * Base class for all circuit elements including sources
 */
public abstract class CircuitElement {

    
    public String name;
    
    /**
     * Modifies the equation matrices by applying this elements stamp to them
     * @param equation - the equation to stamp.
     */
    public abstract void applyStamp(IEquation equation);

    /**
     *
     * @return number of actual nodes this element is physically connected to
     */
    public abstract int getNodeCount();

    /**
     *
     * @return total number of variables this element represents in the final matrix representation
     */
    public abstract int getVariableCount();

}
