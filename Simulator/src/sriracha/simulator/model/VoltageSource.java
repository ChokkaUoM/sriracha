package sriracha.simulator.model;

import sriracha.math.interfaces.IComplex;
import sriracha.simulator.solver.interfaces.IEquation;

public class VoltageSource extends Source  {


    private IComplex voltagePhasor;

    public VoltageSource(String name, IComplex voltagePhasor) {
         super(name);
         this.voltagePhasor = voltagePhasor;
    }

    private int currentIndex;


    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(currentIndex, nPlus, 1);
        equation.applyRealStamp(currentIndex, nMinus, -1);
        equation.applyRealStamp(nPlus, currentIndex, 1);
        equation.applyRealStamp(nMinus, currentIndex, -1);

        //warning: if we ever have to deal with superposition this will not work
        equation.applySourceStamp(currentIndex, voltagePhasor);
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
    public VoltageSource buildCopy(String name) {
        return new VoltageSource(name, voltagePhasor);
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
