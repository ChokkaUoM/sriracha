package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class CurrentSource extends Source {

    /**
     * the node positive current flows out of
     */
    private int outNode;

    /**
     * the node positive current flows in to
     */
    private int inNode;

    /**
     * current in Amperes
     */
    private double current;

    /**
     * Standard CurrentSource constructor
     *
     * @param outNode the node positive current flows out of
     * @param inNode  the node positive current flows in to
     * @param current current in Amperes
     */
    public CurrentSource(int outNode, int inNode, double current) {
        this.outNode = outNode;
        this.inNode = inNode;
        this.current = current;
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applySourceStamp(outNode, current);
        equation.applySourceStamp(inNode, -current);
    }

    @Override
    public int getNodeCount() {
        return 2;
    }

    @Override
    public int getVariableCount() {
        return 2;
    }
}
