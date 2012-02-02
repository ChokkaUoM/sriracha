package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class Capacitor extends CircuitElement {

    private int nodePos, nodeNeg;

    private double capacitance, initialVoltage;

    public Capacitor(int nodePos, int nodeNeg, double capacitance) {
        this.nodePos = nodePos;
        this.nodeNeg = nodeNeg;
        this.capacitance = capacitance;
        initialVoltage = 0;
    }

    public Capacitor(int nodePos, int nodeNeg, double capacitance, double ic) {
        this.nodePos = nodePos;
        this.nodeNeg = nodeNeg;
        this.capacitance = capacitance;
        this.initialVoltage = ic;
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applyComplexStamp(nodePos, nodePos, capacitance);
        equation.applyComplexStamp(nodePos, nodeNeg, -capacitance);
        equation.applyComplexStamp(nodeNeg, nodePos, -capacitance);
        equation.applyComplexStamp(nodeNeg, nodeNeg, capacitance);


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
