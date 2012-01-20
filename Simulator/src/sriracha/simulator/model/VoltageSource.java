package sriracha.simulator.model;

import sriracha.simulator.model.interfaces.IAddVariable;
import sriracha.simulator.solver.interfaces.IEquation;

public class VoltageSource extends Source implements IAddVariable {

    private int nodePos, nodeNeg;

    private double E;

    /**
     * @param nodePos
     * @param nodeNeg
     * @param e       - voltage?? i assume
     */
    public VoltageSource(int nodePos, int nodeNeg, double e) {
        this.nodePos = nodePos;
        this.nodeNeg = nodeNeg;
        E = e;
    }

    private int currentIndex;


    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(currentIndex, nodePos, 1);
        equation.applyRealStamp(currentIndex, nodeNeg, -1);
        equation.applyRealStamp(nodePos, currentIndex, 1);
        equation.applyRealStamp(nodeNeg, currentIndex, -1);

        equation.applySourceStamp(currentIndex, E);
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
