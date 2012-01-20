package sriracha.simulator.model;

import sriracha.simulator.model.interfaces.IAddVariable;
import sriracha.simulator.solver.interfaces.IEquation;

public class IdealOpAmp extends CircuitElement implements IAddVariable{

    /**
     * nodes
     */
    private int posIn, negIn, out;
    
    
    private int varIndex;


    /**
     *  Ideal opamp
     * @param posIn - positive input node
     * @param negIn - negative input node
     * @param out  - output node
     */
    public IdealOpAmp(int posIn, int negIn, int out) {
        this.posIn = posIn;
        this.negIn = negIn;
        this.out = out;
    }

    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(varIndex, out, 1);
        equation.applyRealStamp(posIn, varIndex, -1);
        equation.applyRealStamp(negIn, varIndex, 1);

    }

    @Override
    public int getVarIndexCount() {
        return 1;
    }

    @Override
    public void setFirstVarIndex(int i) {
       varIndex = i;
    }
}
