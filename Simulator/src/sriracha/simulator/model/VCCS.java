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
     * @param i      - positive node on v0
     * @param iPrime - positive node on v0
     * @param k      - node on source where current flows in
     * @param kPrime - node on source where current flows out
     * @param gm     - factor in source current equation
     */
    public VCCS(int i, int iPrime, int k, int kPrime, double gm) {
        super(i, iPrime, k, kPrime, gm);
    }


    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(i, k, gm);
        equation.applyRealStamp(i, kPrime, -gm);
        equation.applyRealStamp(iPrime, k, -gm);
        equation.applyRealStamp(iPrime, kPrime, gm);
    }

}
