package sriracha.simulator.model;

import sriracha.simulator.solver.interfaces.IEquation;

/**
 * Base class for all circuit elements including sources
 * */
public abstract class CircuitElement {


    public abstract void applyStamp(IEquation equation);



}
