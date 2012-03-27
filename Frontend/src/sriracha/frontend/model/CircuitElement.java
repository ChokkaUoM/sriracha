package sriracha.frontend.model;

public abstract class CircuitElement
{
    abstract public Property[] getProperties();
    abstract public int getPortCount();

    protected CircuitElementPort[] ports;

    public CircuitElement()
    {
        ports = new CircuitElementPort[getPortCount()];
    }

    public CircuitElementPort[] getPorts()
    {
        return ports;
    }
}
