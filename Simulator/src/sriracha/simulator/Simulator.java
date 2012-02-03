package sriracha.simulator;

import java.util.HashMap;

/**
 * Main Class for interaction with the Simulator, abstracts away all the sub-components 
 * giving one cohesive object to deal with from the frontend
 */
public class Simulator {

    /**
     * mapping from node names in received netlist to index in final matrix.
     * Ground node is always the tuple ("0", -1)
     */
    private HashMap<String, Integer> nodeMap;
    
    public void addNodeMapping(String nodeName, int index){
        nodeMap.put(nodeName, index);
    }
}
