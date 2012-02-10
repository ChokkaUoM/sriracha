package sriracha.simulator.model;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.simulator.solver.interfaces.IEquation;

public class CurrentSource extends Source {

    /**
     * complex number representation of phasor vector.
     */
    private IComplex currentPhasor;
    private Double dcCurrent;

    /**
     * Standard AC CurrentSource constructor
     *
     * @param currentPhasor complex number representation of phasor vector
     * @param name name from netlist
     */
    public CurrentSource(String name, IComplex currentPhasor) {
        super(name);
        this.currentPhasor = currentPhasor;
        dcCurrent = null;
    }

    /**
     * Standard DC CurrentSource constructor
     *
     * @param dcCurrent current in Amperes
     * @param name name from netlist
     */
    public CurrentSource(String name, double dcCurrent) {
        super(name);
        this.dcCurrent = dcCurrent;
        currentPhasor = null;
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
        if(dcCurrent == null){
            equation.applySourceStamp(nMinus, currentPhasor);
            equation.applySourceStamp(nPlus, currentPhasor.opposite());
        }else {
           IComplex val = MathActivator.Activator.complex(dcCurrent, 0);
           equation.applySourceStamp(nMinus, val);
           equation.applySourceStamp(nPlus, val.opposite());
        }
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
