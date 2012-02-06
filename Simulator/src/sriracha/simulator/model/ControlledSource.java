package sriracha.simulator.model;

/**
 * Base class for current sources.
 * Uses small signal model
 */
public abstract class ControlledSource extends Source {

    /**
     * control nodes positive and negative
     * positive current flows rom positive to negative node
     */
    protected int ncPlus, ncMinus;

    //factor for source
    protected double gm;


    /**
     * Set the indices that correspond to the circuit element's nodes.
     * The nodes are assumed to be in the order they are in the netlist.
     * (-1 is always ground)
     *
     * @param indices the ordered node indices
     */
    @Override
    public void setNodeIndices(int... indices) {
        super.setNodeIndices(indices);
        ncPlus = indices[2];
        ncMinus = indices[3];

    }

    /**
     * Constructor for controlled sources
     *
     * @param gm     - factor in source equation
     */
    protected ControlledSource(String name, double gm) {
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
