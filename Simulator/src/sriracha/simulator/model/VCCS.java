package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class VCCS extends ControlledSource {


    private int currentIndex;


    /**
     * Voltage controlled Current Source
     * Vccs: I = gm * v0
     * where v0 is a voltage elsewhere in the circuit
     * I is the current from the source
     *
     * @param name - VCCS name from netlist
     * @param gm     - factor in source current equation
     */
    public VCCS(String name, double gm) {
        super(name, gm);
    }


    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(ncPlus, nPlus, gm);
        equation.applyRealStamp(ncPlus, nMinus, -gm);
        equation.applyRealStamp(ncMinus, nPlus, -gm);
        equation.applyRealStamp(ncMinus, nMinus, gm);
    }

    @Override
    public int getNodeCount() {
        return 2;
    }

    @Override
    public int getExtraVariableCount() {
        return 0;
    }

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    @Override
    public VCCS buildCopy(String name) {
        return new VCCS(name, gm);
    }

}
