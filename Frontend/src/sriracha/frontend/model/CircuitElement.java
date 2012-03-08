package sriracha.frontend.model;

import sriracha.frontend.*;

public abstract class CircuitElement
{
    abstract public int getPortCount();

    protected CircuitElement[] connections;

    public CircuitElement()
    {
        connections = new CircuitElement[getPortCount()];
    }
}
