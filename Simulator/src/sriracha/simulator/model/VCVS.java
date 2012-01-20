package sriracha.simulator.model;

import sriracha.simulator.model.interfaces.IAddVariable;
import sriracha.simulator.solver.interfaces.IEquation;

public class VCVS extends ControlledSource implements IAddVariable {


    private int currentIndex;


    /**
     * Voltage controlled Voltage Source
     * Vcvs: Vs =  gm * v0
     * where v0 is a voltage elsewhere in the circuit
     * Vs is the source voltage
     *
     * @param i      - positive node on v0
     * @param iPrime - positive node on v0
     * @param k      - positive node on source
     * @param kPrime - negative node on source
     * @param gm     - factor in source Voltage equation
     */
    public VCVS(int i, int iPrime, int k, int kPrime, double gm) {
        super(i, iPrime, k, kPrime, gm);
    }


    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(iPrime, currentIndex, gm);
        equation.applyRealStamp(i, currentIndex, -gm);
        equation.applyRealStamp(k, currentIndex, 1);
        equation.applyRealStamp(kPrime, currentIndex, -1);
        equation.applyRealStamp(currentIndex, k, 1);
        equation.applyRealStamp(currentIndex, kPrime, -1);
    }

    @Override
    public int getVarIndexCount() {
        return 1;
    }

    @Override
    public void setFirstVarIndex(int i) {
        currentIndex = i;
    }
}
