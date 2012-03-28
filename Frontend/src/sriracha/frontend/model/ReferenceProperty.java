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

    public ArrayList<String> getElementsList()
    {
        ArrayList<String> names = new ArrayList<String>();
        for (CircuitElement element : elementManager.getElements())
        {
            if (element instanceof VoltageSource || element instanceof CurrentSource
                    || element instanceof DependentVoltageSource || element instanceof DependentCurrentSource)
            {
                names.add(element.getName());
            }
        }
        return names;
    }
}
