package sriracha.simulator.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Information necessary for building a new subcircuit object
 */
public class SubCircuitTemplate {

    /**
     * corresponds to the name given to the subcircuit definition in the netlist
     */
    private String name;
    
    private int nodeCount;

    /**
     * list of all elements forming the subcircuit
     * node numbers should be defined with respect to the internal
     * subcircuit numbering system. This system should use sequential integers
     * starting from 0 where the first ones correspond to the external terminals
     * in the order they are defined in the netlist
     */
    private ArrayList<CircuitElement> elements;

    /**
     *
     * @param name - name given to the subcircuit definition in the netlist
     * @param nodeCount - number of external nodes in subcircuit template
     */
    public SubCircuitTemplate(String name, int nodeCount) {
        this.name = name;
        this.nodeCount = nodeCount;
    }

    /**
     * Adds a new element to the template using an internal numbering system that does
     * not correspond to a matrix index. They are the indices mapped in the internalNodeMap
     * @see this.addInternalNodeMapping()
     * @param element - new element with nodes numbered with internal system.
     */
    public void addElement(CircuitElement element){
        elements.add(element);
    }

    /**
     * Returns the number of external nodes 
     * @return
     */
    public int getExternalNodeCount(){
        return nodeCount;
    }

    /**
     * Returns the number of internal nodes
     * @return
     */
    public int getInternalNodeCount(){
        return internalNodeMap.size() - nodeCount;
    }

    /**
     * mapping from node names in received netlist to internal
     * subcircuit numbering system.
     * By convention if the Subcircuit has n terminals those should correspond to the integers 0 -> n-1
     * internal nodes take integers from n on
     */
    private HashMap<String, Integer> internalNodeMap;

    /**
     * This mapping system should use sequential integers
     * starting from 0 where the first ones correspond to the external terminals
     * in the order they are defined in the netlist
     * @param nodeName - name of node from netlist
     * @param index - internal index
     */
    public void addInternalNodeMapping(String nodeName, int index){
        internalNodeMap.put(nodeName, index);
    }


    public ArrayList<CircuitElement> getElements() {
        return elements;
    }
}
