package sriracha.simulator.model;

public abstract class Source extends CircuitElement {

    /**
     * Node indices for source
     * for current source: Current flows from nPlus, through source, out nMinus
      */
    protected int nPlus, nMinus;
    /**
     * @param name element name from netlist
     */
    protected Source(String name) {
        super(name);
    }

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

    }
    
    
}
