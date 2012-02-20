package sriracha.simulator.model;

import sriracha.simulator.model.interfaces.ICollectElements;
import sriracha.simulator.solver.IEquation;

import java.util.HashMap;

public class Circuit implements ICollectElements{

    private HashMap<String, CircuitElement> elements;

    /**
     * mapping from node names in received netlist to index in final matrix.
     * Ground node is always the tuple ("0", -1)
     * Internal SubMatrix nodes are not mapped here
     */
    private HashMap<String, Integer> nodeMap;

    public Circuit() {
        elements = new HashMap<String, CircuitElement>();
        nodeMap = new HashMap<String, Integer>();
        nodeMap.put("0", -1);
    }

    @Override
    public void addElement(CircuitElement e){
        elements.put(e.name, e);
    }

    /**
     * Add new node mapping
     *
     * @param nodeName - name of node from netlist
     * @return index for node
     */
    @Override
    public int assignNodeMapping(String nodeName) {
        if (!nodeMap.containsKey(nodeName)) {
            nodeMap.put(nodeName, getNodeCount());
        }
        return nodeMap.get(nodeName);
    }

    public int getNodeIndex(String nodeName){
        return nodeMap.get(nodeName);
    }
    
    public CircuitElement getElement(String name){
        if(elements.containsKey(name)){
            return elements.get(name);
        }
        return null;
    }

    public void applyStamp(IEquation equation) {
        for (CircuitElement e : elements.values()) {
            e.applyStamp(equation);
        }
    }

    /**
     * number of nodes in the circuit excluding ground and internal subcircuit nodes.
     *
     * @return number of nodes
     */
    public int getNodeCount() {
        return nodeMap.size() - 1;
    }


    /**
     * Assigns indices to the additional variables required by some elements
     * and also to internal nodes in subcircuits
     */
    public void assignAdditionalVarIndices() {
        int index = getNodeCount();
        for (CircuitElement e : elements.values()) {
            if (e.getExtraVariableCount() > 0) {
                e.setFirstVarIndex(index);
                index += e.getExtraVariableCount();
            }
        }
    }

    public int getMatrixSize() {
        int evCount = 0;
        for (CircuitElement e : elements.values()) {
            evCount += e.getExtraVariableCount();
        }
        return evCount + getNodeCount();
    }
    @Override
    public String toString() {
        String s = "";
        for(CircuitElement e : elements.values()){
            s += e + "\n";
        }
        return s;
    }
}
