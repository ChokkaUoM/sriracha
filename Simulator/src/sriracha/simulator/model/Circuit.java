package sriracha.simulator.model;

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
    
    

    public Circuit expandedCircuit() {
        //todo: expand basic circuit elements into submodels such that the new nodeCount works for equation generation
        return this;
    }
}
