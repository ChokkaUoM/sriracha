package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

/**
 * Base class for all circuit elements including sources and subcircuits
 */
public abstract class CircuitElement {

    /**
     * Element name as described in the netlist
     */
    public String name;

    /**
     * Circuit Element Constructor
     * @param name element name from netlist
     */
    protected CircuitElement(String name) {
        this.name = name;
    }

    /**
     * Set the indices that correspond to the circuit element's nodes.
     * The nodes are assumed to be in the order they are in the netlist.
     * (-1 is always ground)
     * @param indices the ordered node indices
     */
    public abstract void setNodeIndices(int ... indices);

    /**
     * @return an array containing the matrix indices for the nodes in this circuit element
     */
    public abstract int[] getNodeIndices();

    /**
     * Some elements add extra variables to the matrix. This method serves to set the index for the
     * first such variables and should be overridden in all appropriate elements,
     * the remaining variables take numbers sequentially.
     * @param i index for the first of the extra variables required.
     */
    public void setFirstVarIndex(int i){}

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
    public abstract int getExtraVariableCount();

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    public abstract CircuitElement buildCopy(String name);
}
