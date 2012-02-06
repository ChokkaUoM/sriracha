package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class CurrentSource extends Source {

    /**
     * current in Amperes
     */
    private double current;

    /**
     * Standard CurrentSource constructor
     *
     * @param current current in Amperes
     * @param name name from netlist
     */
    public CurrentSource(String name, double current) {
        super(name);
        this.current = current;
    }

    /**
     * @return an array containing the matrix indices for the nodes in this circuit element
     */
    @Override
    public int[] getNodeIndices() {
        return new int[]{nPlus, nMinus};
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applySourceStamp(nMinus, current);
        equation.applySourceStamp(nPlus, -current);
    }

    @Override
    public int getNodeCount() {
        return 2;
    }

    @Override
    public int getExtraVariableCount() {
        return 0;
    }

    @Override
    public CurrentSource buildCopy(String name) {
        return new CurrentSource(name, current);
    }
}
