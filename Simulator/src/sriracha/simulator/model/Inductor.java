package sriracha.simulator.model;

import sriracha.simulator.model.interfaces.IAddVariable;
import sriracha.simulator.solver.interfaces.IEquation;

public class Inductor extends CircuitElement implements IAddVariable{
    
    private int nodeA, nodeB;

    private double L;

    private int currentIndex;
    /**
     *
     * @param nodeA - node A index
     * @param nodeB - node B index
     * @param l - inductance
     */
    public Inductor(int nodeA, int nodeB, double l) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        L = l;
    }

    @Override
    public void applyStamp(IEquation equation) {
       equation.applyRealStamp(currentIndex, nodeA, 1);
       equation.applyRealStamp(currentIndex, nodeB, -1);
       equation.applyRealStamp(nodeA, currentIndex, 1);
       equation.applyRealStamp(nodeB, currentIndex, -1);

       equation.applyComplexStamp(currentIndex, currentIndex, L);

    }

    @Override
    public void setVariableIndex(int i) {
      currentIndex = i;
    }
}
