package sriracha.simulator.model.elements.ctlsources;

import sriracha.simulator.model.CircuitElement;

/**
 * Base class for Controlled sources.
 */
public abstract class ControlledSource extends CircuitElement {

    /**
     * control nodes positive and negative
     * positive current flows rom positive to negative node
     */
    int ncPlus;
    int ncMinus;

    /**
     * Node indices for source
     * for current source: Current flows from nPlus, through source, out nMinus
     */
    int nPlus;
    int nMinus;

    /**
     * Factor for controlled source equation
     */
    double gm;


    /**
     * Set the indices that correspond to the circuit element's nodes.
     * The nodes are assumed to be in the order they are in the netlist.
     * (-1 is always ground)
     *
     * @param indices the ordered node indices
     */
    @Override
    public void setNodeIndices(int... indices) {
        nPlus = indices[0];
        nMinus = indices[1];
        ncPlus = indices[2];
        ncMinus = indices[3];

    }

    /**
     * Constructor for controlled sources
     *
     * @param gm - factor in source equation
     */
    ControlledSource(String name, double gm) {
        super(name);
        this.gm = gm;
    }

    /**
     * @return an array containing the matrix indices for the nodes in this circuit element
     */
    @Override
    public int[] getNodeIndices() {
        return new int[]{nPlus, ncMinus, ncPlus, ncMinus};
    }


}
