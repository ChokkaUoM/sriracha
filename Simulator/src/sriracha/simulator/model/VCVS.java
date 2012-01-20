package sriracha.simulator.model;

import sriracha.simulator.model.interfaces.IAddVariable;
import sriracha.simulator.solver.interfaces.IEquation;

public class VCVS extends Source implements IAddVariable{

    /**
     * nodes
     */
    private int j, jPrime, k, kPrime;

    private int currentIndex;

    //factor for source
    private double gm;


    /**
     * Voltage controlled Current Source
     * Vcvs: Vs =  gm * v0
     * where v0 is a voltage elsewhere in the circuit
     * Vs is the source voltage
     * @param j - positive node on v0
     * @param jPrime - positive node on v0
     * @param k - positive node on source
     * @param kPrime - negative node on source
     * @param gm - factor in source Voltage equation
     */
    public VCVS(int j, int jPrime, int k, int kPrime, double gm) {
        this.j = j;
        this.jPrime = jPrime;
        this.k = k;
        this.kPrime = kPrime;
        this.gm = gm;
    }


    @Override
    public void applyStamp(IEquation equation) {
       equation.applyRealStamp(jPrime, currentIndex, gm);
       equation.applyRealStamp(j, currentIndex, -gm);
       equation.applyRealStamp(k, currentIndex, 1);
       equation.applyRealStamp(kPrime, currentIndex, -1);
       equation.applyRealStamp(currentIndex, k, 1);
       equation.applyRealStamp(currentIndex, kPrime, -1);
    }

    @Override
    public void setVariableIndex(int i) {
       currentIndex = i;
    }
}
