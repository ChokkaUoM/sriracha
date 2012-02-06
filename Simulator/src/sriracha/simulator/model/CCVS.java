package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class CCVS extends ControlledSource {

    int i0Index, ieIndex;

    /**
     * Current Controlled Voltage Source
     * Vs = gm * i0
     *
     * @param name  - CCVS netlist name
     * @param gm    - factor in source equation
     */
    protected CCVS(String name, double gm) {
        super(name, gm);
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(i0Index, ncPlus, 1);
        equation.applyRealStamp(i0Index, ncMinus, -1);
        equation.applyRealStamp(ieIndex, nPlus, 1);
        equation.applyRealStamp(ieIndex, nMinus, -1);
        equation.applyRealStamp(ncPlus, i0Index, 1);
        equation.applyRealStamp(ncMinus, i0Index, -1);
        equation.applyRealStamp(nPlus, ieIndex, 1);
        equation.applyRealStamp(nMinus, ieIndex, -1);
        equation.applyRealStamp(i0Index, ieIndex, -gm);
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
    public CircuitElement buildCopy(String name) {
        return new CCVS(name, gm);
    }


    @Override
    public void setFirstVarIndex(int i) {
        i0Index = i;
        ieIndex = i + 1;
    }
}
