package sriracha.simulator.model;

import sriracha.simulator.model.interfaces.IAddVariable;
import sriracha.simulator.solver.interfaces.IEquation;

public class CCVS extends ControlledSource implements IAddVariable {

    int i0Index, ieIndex;

    /**
     * Current Controlled Voltage Source
     * Vs = gm * i0
     *
     * @param i      - current i0 in node
     * @param iPrime - current i0 out node
     * @param k      - positive node on source
     * @param kPrime - negative node on source
     * @param gm     - factor in source equation
     */
    protected CCVS(int i, int iPrime, int k, int kPrime, double gm) {
        super(i, iPrime, k, kPrime, gm);
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(i0Index, i, 1);
        equation.applyRealStamp(i0Index, iPrime, -1);
        equation.applyRealStamp(ieIndex, k, 1);
        equation.applyRealStamp(ieIndex, kPrime, -1);
        equation.applyRealStamp(i, i0Index, 1);
        equation.applyRealStamp(iPrime, i0Index, -1);
        equation.applyRealStamp(k, ieIndex, 1);
        equation.applyRealStamp(kPrime, ieIndex, -1);
        equation.applyRealStamp(i0Index, ieIndex, -gm);
    }

    @Override
    public int getVarIndexCount() {
        return 2;
    }

    @Override
    public void setFirstVarIndex(int i) {
        i0Index = i;
        ieIndex = i + 1;
    }
}
