package sriracha.simulator.model;

import sriracha.simulator.model.interfaces.IAddVariable;
import sriracha.simulator.solver.interfaces.IEquation;

public class CCCS extends ControlledSource implements IAddVariable {

    int currentIndex;

    /**
     * Current controlled Current Source
     * Is = gm * i0
     *
     * @param i      - current i0 in node
     * @param iPrime - current i0 out node
     * @param k      - current Is out node
     * @param kPrime - current Is in node
     * @param gm     - factor in source equation
     */
    public CCCS(int i, int iPrime, int k, int kPrime, double gm) {
        super(i, iPrime, k, kPrime, gm);
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(currentIndex, i, 1);
        equation.applyRealStamp(currentIndex, iPrime, -1);
        equation.applyRealStamp(currentIndex, k, -gm);
        equation.applyRealStamp(currentIndex, kPrime, gm);
        equation.applyRealStamp(i, currentIndex, 1);
        equation.applyRealStamp(iPrime, currentIndex, -1);

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
