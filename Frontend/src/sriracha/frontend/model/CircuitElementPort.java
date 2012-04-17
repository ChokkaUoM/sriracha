package sriracha.frontend.model;

/**
 * Represents a port for a particular element.
 */
public class CircuitElementPort
{
    private CircuitElement element;
    private int index;
    private CircuitElementPort[] connections;

    public CircuitElementPort(CircuitElement element, int index, CircuitElementPort[] connections)
    {
        this.element = element;
        this.index = index;
        this.connections = connections;
    }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public CircuitElementPort[] getConnections()
    {
        return connections;
    }
}