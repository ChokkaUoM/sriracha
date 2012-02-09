package sriracha.simulator.model;

import sriracha.math.interfaces.IComplex;
import sriracha.simulator.solver.interfaces.IEquation;

public class CurrentSource extends Source {

    /**
     * complex number representation of phasor vector.
     */
    private IComplex currentPhasor;

    /**
     * Standard CurrentSource constructor
     *
     * @param currentPhasor current in Amperes
     * @param name name from netlist
     */
    public CurrentSource(String name, IComplex currentPhasor) {
        super(name);
        this.currentPhasor = currentPhasor;
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
        equation.applySourceStamp(nMinus, currentPhasor);
        equation.applySourceStamp(nPlus, currentPhasor.opposite());
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
        return new CurrentSource(name, currentPhasor);
    }
}
