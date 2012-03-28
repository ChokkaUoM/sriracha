package sriracha.frontend.model;

import java.util.*;

abstract public class CircuitElement
{
    abstract public Property[] getProperties();

    abstract public int getPortCount();

    abstract public String getType();

    abstract public String getNameTemplate();

    protected CircuitElementManager elementManager;

    protected String name;
    protected int index;
    private static HashMap<Class<? extends CircuitElement>, Integer> elementCount = new HashMap<Class<? extends CircuitElement>, Integer>();

    protected CircuitElementPort[] ports;

    public CircuitElement(CircuitElementManager elementManager)
    {
        this.elementManager = elementManager;
        ports = new CircuitElementPort[getPortCount()];

        String generatedName = null;
        do
        {
            index = elementCount.containsKey(getClass()) ? elementCount.get(getClass()) + 1 : 1;
            elementCount.put(getClass(), Integer.valueOf(index));
            generatedName = String.format(getNameTemplate(), index);
            setName(generatedName);
        } while (elementManager.getElementByName(generatedName) != null); // Ensure unique names
    }

    public String getName() { return name; }
    public void setName(String name)
    {
        this.name = name;
    }

    public CircuitElementManager getElementManager()
    {
        return elementManager;
    }
}
