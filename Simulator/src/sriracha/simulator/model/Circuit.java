package sriracha.simulator.model;

import sriracha.simulator.model.interfaces.IAddVariable;

import java.util.ArrayList;

public class Circuit {

    private int nodeCount;
    
    public ArrayList<CircuitElement> elements;

    public Circuit(int nodeCount){
        this.nodeCount = nodeCount;
        elements = new ArrayList<CircuitElement>();
    }


    public int getNodeCount() {
        return nodeCount;
    }
    
    public int getVariableCount(){
        int count = 0;
        for(CircuitElement e : elements){
            if(e instanceof IAddVariable) {
                ((IAddVariable) e).setVariableIndex(nodeCount + count++);
            }
        }
        return count + nodeCount;
    }

   
}
