package sriracha.simulator.model.elements.sources;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;

public class VoltageSource extends Source {


    public VoltageSource(String name, double dcValue) {
        super(name, dcValue, MathActivator.Activator.complex(0, 0));
    }

    public VoltageSource(String name, IComplex acPhasorValue) {
        super(name, 0, acPhasorValue);
    }

    public VoltageSource(String name, double dcValue, IComplex acPhasorValue) {
        super(name, dcValue, acPhasorValue);
    }

    private int currentIndex;


    @Override
    public void applyDC(DCEquation equation) {
        equation.applyMatrixStamp(currentIndex, nPlus, 1);
        equation.applyMatrixStamp(currentIndex, nMinus, -1);
        equation.applyMatrixStamp(nPlus, currentIndex, 1);
        equation.applyMatrixStamp(nMinus, currentIndex, -1);

        equation.applySourceVectorStamp(currentIndex, dcValue);
    }

    @Override
    public void applyAC(ACEquation equation) {
        equation.applyRealMatrixStamp(currentIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, nMinus, -1);
        equation.applyRealMatrixStamp(nPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(nMinus, currentIndex, -1);

        equation.applySourceVectorStamp(currentIndex, acPhasorValue);
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
        return new VoltageSource(name, dcValue, acPhasorValue);
    }

    @Override
    public String toString() {
        return super.toString() + " DC: " + dcValue + " AC: " + acPhasorValue;
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

    public int getCurrentVarIndex() {
        return currentIndex;
    }
}
