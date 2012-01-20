package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class Capacitor extends CircuitElement{
    
    private int nodeA, nodeB;

    private double capacitance;

    public Capacitor(int nodeA, int nodeB, double capacitance) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.capacitance = capacitance;
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applyComplexStamp(nodeA, nodeA, capacitance);
        equation.applyComplexStamp(nodeA, nodeB, -capacitance);
        equation.applyComplexStamp(nodeB, nodeA, -capacitance);
        equation.applyComplexStamp(nodeB, nodeB, capacitance);


    }

}
