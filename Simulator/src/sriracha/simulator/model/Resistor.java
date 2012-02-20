package sriracha.simulator.model;

import sriracha.simulator.solver.IEquation;

public class Resistor extends CircuitElement {

    protected int nodeA, nodeB;

    /**
     * in ohms
     */
    private double resistance;

    /**
     * Standard Resistor Constructor
     *
     * @param resistance - resistance in ohms
     */
    public Resistor(String name, double resistance) {
        super(name);
        this.resistance = resistance;
    }


    @Override
    public void setNodeIndices(int... indices) {
        nodeA = indices[0];
        nodeB = indices[1];
    }

    @Override
    public int[] getNodeIndices() {
        return new int[]{nodeA, nodeB};
    }

    @Override
    public void applyStamp(IEquation equation) {
        double G = 1.0/resistance; //conductance
        equation.applyRealStamp(nodeA, nodeA, G);
        equation.applyRealStamp(nodeB, nodeB, G);
        equation.applyRealStamp(nodeA, nodeB, -G);
        equation.applyRealStamp(nodeB, nodeA, -G);


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
    public Resistor buildCopy(String name) {
        return new Resistor(name, resistance);
    }

    @Override
    public String toString() {
        return super.toString() + " " + resistance;
    }
}
