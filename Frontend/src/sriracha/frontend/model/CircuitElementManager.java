package sriracha.frontend.model;

import java.util.*;

public class CircuitElementManager
{
    private ArrayList<CircuitElement> elements;

    public CircuitElementManager()
    {
        elements = new ArrayList<CircuitElement>();
    }
    
    public void addElement(CircuitElement element)
    {
        elements.add(element);
    }
    
    public boolean removeElement(CircuitElement element)
    {
        return elements.remove(element);
    }

    public ArrayList<CircuitElement> getElements()
    {
        return elements;
    }
}
