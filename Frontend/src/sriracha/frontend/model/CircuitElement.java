package sriracha.frontend.model;

import java.util.*;

abstract public class CircuitElement
{
    abstract public Property[] getProperties();

    abstract public int getPortCount();

    abstract public String getType();

    abstract public String getNameTemplate();

    private String name;
    private static HashMap<Class<? extends CircuitElement>, Integer> elementCount = new HashMap<Class<? extends CircuitElement>, Integer>();

    protected CircuitElementPort[] ports;

    public CircuitElement()
    {
        ports = new CircuitElementPort[getPortCount()];

        int thisElementCount = elementCount.containsKey(getClass()) ? elementCount.get(getClass()) + 1 : 1;
        elementCount.put(getClass(), Integer.valueOf(thisElementCount));
        setName(String.format(getNameTemplate(), thisElementCount));
    }

    public String getName() { return name; }
    public void setName(String name)
    {
        this.name = name;
    }

    public CircuitElementPort[] getPorts()
    {
        return ports;
    }
}
