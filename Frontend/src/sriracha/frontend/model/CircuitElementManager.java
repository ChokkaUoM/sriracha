package sriracha.frontend.model;

import java.util.*;

/**
 * Manages the collection of {@link CircuitElement}s that have been created.
 */
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
    
    public CircuitElement getElementByName(String name)
    {
        for (CircuitElement element : elements)
        {
            if (element.getName().equalsIgnoreCase(name))
                return element;
        }
        return null;
    }
}
