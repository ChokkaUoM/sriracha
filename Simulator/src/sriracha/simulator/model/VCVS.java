package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class VCVS extends ControlledSource  {


    private int currentIndex;


    /**
     * Voltage controlled Voltage Source
     * Vcvs: Vs =  gm * v0
     * where v0 is a voltage elsewhere in the circuit
     * Vs is the source voltage
     *
     * @param name  - VCVS name from netlist
     * @param gm     - factor in source Voltage equation
     */
    public VCVS(String name, double gm) {
        super(name, gm);
    }


    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(ncMinus, currentIndex, gm);
        equation.applyRealStamp(ncPlus, currentIndex, -gm);
        equation.applyRealStamp(nPlus, currentIndex, 1);
        equation.applyRealStamp(nMinus, currentIndex, -1);
        equation.applyRealStamp(currentIndex, nPlus, 1);
        equation.applyRealStamp(currentIndex, nMinus, -1);
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
        return new VCVS(name, gm);
    }


    @Override
    public void setFirstVarIndex(int i) {
        currentIndex = i;
    }
}
