package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class VoltageSource extends Source  {


    private double voltage;

    public VoltageSource(String name, double voltage) {
         super(name);
         this.voltage = voltage;
    }

    private int currentIndex;


    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(currentIndex, nPlus, 1);
        equation.applyRealStamp(currentIndex, nMinus, -1);
        equation.applyRealStamp(nPlus, currentIndex, 1);
        equation.applyRealStamp(nMinus, currentIndex, -1);

        equation.applySourceStamp(currentIndex, voltage);
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
    public CircuitElement buildCopy(String name) {
        return new VoltageSource(name, voltage);
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
