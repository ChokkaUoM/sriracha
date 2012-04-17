package sriracha.frontend.model;

import sriracha.frontend.*;

import java.io.*;
import java.util.*;

/**
 * Abstract class used to represent a circuit element for the user interface.
 * Each subclass is responsible for defining its own properties and making them available.
 * This class is also responsible for generating the string corresponding to the element's
 * entry in the netlist. If a subclass has a non-trivial netlist implementation, then it must
 * override the {@link CircuitElement#toNetlistString(java.lang.String[], NodeCrawler) toNetlistString} method.
 */
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
