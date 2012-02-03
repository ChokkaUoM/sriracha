package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

import java.util.ArrayList;
import java.util.HashMap;

public class Circuit{

    private int nodeCount;

    public ArrayList<CircuitElement> elements;

    public Circuit(int nodeCount) {
        this.nodeCount = nodeCount;
        elements = new ArrayList<CircuitElement>();
    }



    /**
     * mapping from node names in received netlist to index in final matrix.
     * Ground node is always the tuple ("0", -1)
     * Internal SubMatrix nodes are not mapped here
     */
    private HashMap<String, Integer> nodeMap;

    /**
     * Add new node mapping
     * @param nodeName - name of node from netlist
     * @param index - matrix index for voltage.
     */
    public void addNodeMapping(String nodeName, int index){
        nodeMap.put(nodeName, index);
    }


    public void applyStamp(IEquation equation) {
        for(CircuitElement e : elements){
            e.applyStamp(equation);
        }
    }


    public int getNodeCount() {
        return nodeCount;
    }


    public int getVariableCount() {
        int count = 0;
        for (CircuitElement e : elements) {
            count += e.getVariableCount();
        }
        return count;
    }


}
