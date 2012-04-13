package sriracha.frontend.model;

import sriracha.frontend.model.elements.sources.*;

import java.util.*;

abstract public class ReferenceProperty extends Property
{
    abstract public int getTypeId();
    abstract public void setTypeId(int type);
    abstract public String getSourceElement();
    abstract public void setSourceElement(String value);
    abstract public String getSourceNode1();
    abstract public String getSourceNode2();
    abstract public void setSourceNode1(String node1);
    abstract public void setSourceNode2(String node2);

    protected CircuitElementManager elementManager;

    protected ReferenceProperty(CircuitElementManager elementManager)
    {
        this.elementManager = elementManager;
    }

    public ArrayList<CircuitElement> getSourceElementsList()
    {
        ArrayList<CircuitElement> elements = new ArrayList<CircuitElement>();
        for (CircuitElement element : elementManager.getElements())
        {
            if (element instanceof VoltageSource)
                elements.add(element);
        }
        return elements;
    }
}
