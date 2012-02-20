package sriracha.simulator.model;

import sriracha.simulator.solver.IEquation;

public class CCCS extends ControlledSource {

    int currentIndex;

    /**
     * Current controlled Current Source
     * Is = gm * i0
     *
     * @param name  - name from netlist
     * @param gm    - factor in source equation
     */
    public CCCS(String name, double gm) {
        super(name, gm);
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(currentIndex, ncPlus, 1);
        equation.applyRealStamp(currentIndex, ncMinus, -1);
        equation.applyRealStamp(currentIndex, nMinus, -gm);
        equation.applyRealStamp(currentIndex, nPlus, gm);
        equation.applyRealStamp(ncPlus, currentIndex, 1);
        equation.applyRealStamp(ncMinus, currentIndex, -1);

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
