package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class Resistor extends CircuitElement {

    protected int nodeA, nodeB;

    /**
     * conductance G = 1/R where R is in ohms
     * */
    private double G;

    /**
     * Standard Resistor Constructor
     * @param nodeA - node id (-1 for ground)
     * @param nodeB - node id (-1 for ground)
     * @param resistance - resistance in ohms
     */
    public Resistor(int nodeA, int nodeB, double resistance){
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        G = 1.0/resistance;
    }
    
    
    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(nodeA, nodeA, G);
        equation.applyRealStamp(nodeB, nodeB, G);
        equation.applyRealStamp(nodeA, nodeB, -G);
        equation.applyRealStamp(nodeB, nodeA, -G);


    }
}
