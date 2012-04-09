package sriracha.frontend.model;

import sriracha.frontend.model.elements.ctlsources.*;
import sriracha.frontend.model.elements.sources.*;

import java.util.*;

abstract public class ReferenceProperty extends Property
{
    protected CircuitElementManager elementManager;

    protected ReferenceProperty(CircuitElementManager elementManager)
    {
        this.elementManager = elementManager;
    }

    public ArrayList<CircuitElement> getElementsList()
    {
        ArrayList<CircuitElement> elements = new ArrayList<CircuitElement>();
        for (CircuitElement element : elementManager.getElements())
        {
            if (element instanceof VoltageSource || element instanceof CurrentSource
                    || element instanceof DependentVoltageSource || element instanceof DependentCurrentSource)
            {
                elements.add(element);
            }
        }
        return elements;
    }
}
