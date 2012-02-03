package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

import java.util.ArrayList;

public class SubCircuit extends CircuitElement{


    /**
     * list of all elements forming the subcircuit
     */
    private ArrayList<CircuitElement> elements;

    /**
     * 
     */
    private ArrayList<Integer> nodes;




    public void addElement(CircuitElement element){
        elements.add(element);
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

    /**
     * Makes a copy of the subcircuit
     */
    public SubCircuit buildCopy(){
       SubCircuit subCircuit = new SubCircuit();


    }
}
