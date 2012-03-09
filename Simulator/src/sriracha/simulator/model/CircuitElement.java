package sriracha.simulator.model;

import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;

/**
 * Base class for all circuit elements including sources and subcircuits
 */
public abstract class CircuitElement
{

    /**
     * Element name as described in the netlist
     */
    public String name;


    /**
     * Circuit Element Constructor
     *
     * @param name element name from netlist
     */
    protected CircuitElement(String name)
    {
        this.name = name;
    }

    /**
     * Set the indices that correspond to the circuit element's nodes.
     * The nodes are assumed to be in the order they are in the netlist.
     * (-1 is always ground)
     *
     * @param indices the ordered node indices
     */
    protected abstract void setNodeIndices(int... indices);

    /**
     * @return an array containing the matrix indices for the nodes in this circuit element
     */
    protected abstract int[] getNodeIndices();

    /**
     * Some elements add extra variables to the matrix. This method serves to set the index for the
     * first such variables and should be overridden in all appropriate elements,
     * the remaining variables take numbers sequentially.
     *
     * @param i index for the first of the extra variables required.
     */
    public void setFirstVarIndex(int i)
    {
    }

    /**
     * @return number of actual nodes this element is physically connected to
     */
    public abstract int getNodeCount();

    /**
     * @return total number of variables this element represents in the final matrix representation
     */
    public abstract int getExtraVariableCount();

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     * if referencedElement is not applicable pass null
     */
    protected abstract CircuitElement buildCopy(String name, CircuitElement referencedElement);

    /**
     * Stamps the equation for DC analysis.
     *
     * @param equation DCEquation object to be stamped
     */
    public abstract void applyDC(DCEquation equation);

    /**
     * Stamps the equation for AC analysis.
     *
     * @param equation ACEquation object to be stamped
     */
    public abstract void applyAC(ACEquation equation);


    @Override
    public String toString()
    {
        String s = name + " ";
        int[] nodes = getNodeIndices();
        for (int i : nodes)
        {
            s += i + " ";
        }

        return s.trim();
    }

    /**
     * circuit elements that use element referencing should override this method and return the appropriate element
     *
     * @return referenced element if applicable, null otherwise
     */
    public CircuitElement getReferencedElement()
    {return null;}

}
