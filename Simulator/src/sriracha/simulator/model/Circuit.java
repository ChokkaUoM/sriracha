package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

import java.util.ArrayList;
import java.util.HashMap;

public class Circuit{

    public ArrayList<CircuitElement> elements;

    public Circuit() {
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

    /**
     * number of nodes in the circuit excluding ground and internal subcircuit nodes.
     * @return number of nodes
     */
    public int getNodeCount() {
        return nodeMap.size();
    }


    /**
     * Assigns indices to the additional variables required by some elements
     * and also to internal nodes in subcircuits
     */
    public void assignAdditionalVarIndices(){
        int index = getNodeCount();
        for(CircuitElement e : elements){
            if(e.getExtraVariableCount() > 0){
                e.setFirstVarIndex(index);
                index += e.getExtraVariableCount();
            }
        }
    }

    public int getMatrixSize() {
        int evCount = 0;
        for (CircuitElement e : elements) {
            evCount += e.getExtraVariableCount();
        }
        return evCount + getNodeCount();
    }


}
