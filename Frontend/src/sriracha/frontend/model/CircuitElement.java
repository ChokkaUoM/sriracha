package sriracha.frontend.model;

import sriracha.frontend.*;

import java.io.*;
import java.util.*;

abstract public class CircuitElement implements Serializable
{
    abstract public Property[] getProperties();

    abstract public int getPortCount();

    abstract public String getType();

    abstract public String getNameTemplate();

    public String toNetlistString(String[] nodes, NodeCrawler crawler)
    {
        String s = getName() + " ";
        for(String s1 : nodes)
        {
            s += s1 + " ";
        }
        return s;
    }

    protected transient CircuitElementManager elementManager;

    protected String name;
    protected int index;
    private static HashMap<Class<? extends CircuitElement>, Integer> elementCount = new HashMap<Class<? extends CircuitElement>, Integer>();

    protected CircuitElementPort[] ports;

    public CircuitElement(CircuitElementManager elementManager)
    {
        init(elementManager);
    }

    public void init(CircuitElementManager elementManager)
    {
        this.elementManager = elementManager;
        ports = new CircuitElementPort[getPortCount()];

        String generatedName = null;
        do
        {
            index = elementCount.containsKey(getClass()) ? elementCount.get(getClass()) + 1 : 1;
            elementCount.put(getClass(), Integer.valueOf(index));
            generatedName = String.format(getNameTemplate(), index);
        } while (elementManager.getElementByName(generatedName) != null); // Ensure unique names
        setName(generatedName);
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

    public CircuitElementPort[] getPorts()
    {
        return ports;
    }
}
