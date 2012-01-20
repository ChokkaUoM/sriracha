package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

public class VCCS extends Source {

    /**
     * nodes
     */
    private int j, jPrime, k, kPrime;
    
    private int currentIndex;

    //factor for source
    private double gm;


    /**
     * Voltage controlled Current Source
     * Vccs: I = gm * v0
     * where v0 is a voltage elsewhere in the circuit
     * I is the current from the source
     * @param j - positive node on v0
     * @param jPrime - positive node on v0
     * @param k - node on source where current flows in
     * @param kPrime - node on source where current flows out
     * @param gm - factor in source current equation
     */
    public VCCS(int j, int jPrime, int k, int kPrime, double gm) {
        this.j = j;
        this.jPrime = jPrime;
        this.k = k;
        this.kPrime = kPrime;
        this.gm = gm;
    }

    @Override
    public void applyStamp(IEquation equation) {
       equation.applyRealStamp(j, k, gm);
       equation.applyRealStamp(j, kPrime, -gm);
       equation.applyRealStamp(jPrime, k, -gm);
       equation.applyRealStamp(jPrime, kPrime, gm);
    }

}
