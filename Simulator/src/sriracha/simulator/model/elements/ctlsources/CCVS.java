package sriracha.simulator.model.elements.ctlsources;

import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;

public class CCVS extends ControlledSource {

    int i0Index, ieIndex;

    /**
     * Current Controlled Voltage Source
     * Vs = gm * i0
     *
     * @param name - CCVS netlist name
     * @param gm   - factor in source equation
     */
    protected CCVS(String name, double gm) {
        super(name, gm);
    }

    @Override
    public void applyAC(ACEquation equation) {
        equation.applyRealMatrixStamp(i0Index, ncPlus, 1);
        equation.applyRealMatrixStamp(i0Index, ncMinus, -1);
        equation.applyRealMatrixStamp(ieIndex, nPlus, 1);
        equation.applyRealMatrixStamp(ieIndex, nMinus, -1);
        equation.applyRealMatrixStamp(ncPlus, i0Index, 1);
        equation.applyRealMatrixStamp(ncMinus, i0Index, -1);
        equation.applyRealMatrixStamp(nPlus, ieIndex, 1);
        equation.applyRealMatrixStamp(nMinus, ieIndex, -1);
        equation.applyRealMatrixStamp(i0Index, ieIndex, -gm);
    }

    @Override
    public void applyDC(DCEquation equation) {
        equation.applyMatrixStamp(i0Index, ncPlus, 1);
        equation.applyMatrixStamp(i0Index, ncMinus, -1);
        equation.applyMatrixStamp(ieIndex, nPlus, 1);
        equation.applyMatrixStamp(ieIndex, nMinus, -1);
        equation.applyMatrixStamp(ncPlus, i0Index, 1);
        equation.applyMatrixStamp(ncMinus, i0Index, -1);
        equation.applyMatrixStamp(nPlus, ieIndex, 1);
        equation.applyMatrixStamp(nMinus, ieIndex, -1);
        equation.applyMatrixStamp(i0Index, ieIndex, -gm);
    }

    @Override
    public int getNodeCount() {
        return 2;
    }

    @Override
    public int getExtraVariableCount() {
        return 2;
    }

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    @Override
    public CCVS buildCopy(String name) {
        return new CCVS(name, gm);
    }


    @Override
    public void setFirstVarIndex(int i) {
        i0Index = i;
        ieIndex = i + 1;
    }
}
