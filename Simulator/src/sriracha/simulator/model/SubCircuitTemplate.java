package sriracha.simulator.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Information necessary for building a new subcircuit object
 */
public class SubCircuitTemplate {

    /**
     * list of all elements forming the subcircuit
     */
    private ArrayList<CircuitElement> elements;




    public void addElement(CircuitElement element){
        elements.add(element);
    }


}
