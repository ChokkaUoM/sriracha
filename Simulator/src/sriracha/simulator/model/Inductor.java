package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class Inductor extends CircuitElement  {

    private int nPlus, nMinus;

    private double L;

    private int currentIndex;

    /**
     * flows from nPlus, through Inductor, out nMinus
     */
    private double initialCurrent;


    /**
     * @param l     - inductance
     */
    public Inductor(String name, double l){
        super(name);
        L = l;
        initialCurrent = 0;
    }

    /**
     * @param l     - inductance
     * @param ic - the initial current flowing through the inductor.
     */
    public Inductor(String name, double l, double ic) {
        super(name);
        L = l;
        initialCurrent = ic;
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(currentIndex, nPlus, 1);
        equation.applyRealStamp(currentIndex, nMinus, -1);
        equation.applyRealStamp(nPlus, currentIndex, 1);
        equation.applyRealStamp(nMinus, currentIndex, -1);

        equation.applyComplexStamp(currentIndex, currentIndex, L);

    }
    
    

    @Override
    public int getNodeCount() {
        return 2;
    }

    @Override
    public int getExtraVariableCount() {
        return 1;
    }

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    @Override
    public Inductor buildCopy(String name) {
        return new Inductor(name, L, initialCurrent);
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

    /**
     * @return an array containing the matrix indices for the nodes in this circuit element
     */
    @Override
    public int[] getNodeIndices() {
        return new int[]{nPlus, nMinus};
    }

    @Override
    public void setFirstVarIndex(int i) {
        currentIndex = i;
    }
}
