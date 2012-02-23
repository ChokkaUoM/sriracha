package sriracha.simulator.model.elements.ctlsources;

import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;

public class CCCS extends ControlledSource {

    int currentIndex;

    /**
     * Current controlled Current Source
     * Is = gm * i0
     *
     * @param name - name from netlist
     * @param gm   - factor in source equation
     */
    public CCCS(String name, double gm) {
        super(name, gm);
    }

    @Override
    public void applyAC(ACEquation equation) {
        equation.applyRealMatrixStamp(currentIndex, ncPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, ncMinus, -1);
        equation.applyRealMatrixStamp(currentIndex, nMinus, -gm);
        equation.applyRealMatrixStamp(currentIndex, nPlus, gm);
        equation.applyRealMatrixStamp(ncPlus, currentIndex, 1);
        equation.applyRealMatrixStamp(ncMinus, currentIndex, -1);

    }

    @Override
    public void applyDC(DCEquation equation) {
        equation.applyMatrixStamp(currentIndex, ncPlus, 1);
        equation.applyMatrixStamp(currentIndex, ncMinus, -1);
        equation.applyMatrixStamp(currentIndex, nMinus, -gm);
        equation.applyMatrixStamp(currentIndex, nPlus, gm);
        equation.applyMatrixStamp(ncPlus, currentIndex, 1);
        equation.applyMatrixStamp(ncMinus, currentIndex, -1);

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
    public CCCS buildCopy(String name) {
        return new CCCS(name, gm);
    }


    @Override
    public void setFirstVarIndex(int i) {
        currentIndex = i;
    }
}
