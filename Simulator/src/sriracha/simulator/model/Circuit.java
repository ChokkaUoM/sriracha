package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

import java.util.ArrayList;

public class Circuit{

    private int nodeCount;

    public ArrayList<CircuitElement> elements;

    public Circuit(int nodeCount) {
        this.nodeCount = nodeCount;
        elements = new ArrayList<CircuitElement>();
    }

   // @Override
    public void applyStamp(IEquation equation) {
        for(CircuitElement e : elements){
            e.applyStamp(equation);
        }
    }

 //   @Override
    public int getNodeCount() {
        return nodeCount;
    }

  //  @Override
    public int getVariableCount() {
        int count = 0;
        for (CircuitElement e : elements) {
            count += e.getVariableCount();
        }
        return count;
    }


}
