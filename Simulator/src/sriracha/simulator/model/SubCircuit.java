package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Subcircuits use a different internal node numbering system and maintains a mapping from internal nodes to
 * matrix indices
 */
public class SubCircuit extends CircuitElement{





    /**
     * Indices for nodes corresponding to external terminals
     */
    private ArrayList<Integer> nodes;


    public void addElement(CircuitElement element){
        elements.add(element);
    }


    @Override
    public void setNodeIndices(int... indices) {
        for(int i : indices){
            nodes.add(i);
        }
    }

    @Override
    public void applyStamp(IEquation equation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNodeCount() {
        return nodes.size();
    }

    @Override
    public int getVariableCount() {
        int count = 0;
        for(CircuitElement e : elements){
            count += e.getVariableCount();
        }
        return count;
    }

    public static SubCircuit build(SubCircuitTemplate template){

    }

    /**
     * Makes a copy of the subcircuit
     */
    @Override
    public SubCircuit buildCopy(){
       SubCircuit subCircuit = new SubCircuit();
       for(CircuitElement e : elements){
           CircuitElement copy = e.buildCopy();

           subCircuit.addElement(e.buildCopy());
       }

    }
}
