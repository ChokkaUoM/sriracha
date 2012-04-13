package sriracha.frontend.android.model.elements.ctlsources;

import sriracha.frontend.*;
import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.*;

import java.util.*;

abstract public class DependentSource extends TwoPortElement
{
    abstract protected void rename();

    public final static int CURRENT_CONTROLLED = 0;
    public final static int VOLTAGE_CONTROLLED = 1;

    protected int typeId = 0;

    protected transient Property[] properties;

    protected CircuitElement sourceElement;
    protected String sourceNode1, sourceNode2;
    protected float gain;

    public DependentSource(CircuitElementManager elementManager)
    {
        super(elementManager);
    }

    @Override
    public Property[] getProperties()
    {
        if (properties == null)
        {
            final CircuitElement self = this;

            properties = new Property[]{
                    new ReferenceProperty(elementManager)
                    {
                        @Override
                        public int getTypeId()
                        {
                            return typeId;
                        }
                        @Override
                        public void setTypeId(int type)
                        {
                            typeId = type;
                            rename();
                        }
                        @Override
                        public String getSourceElement()
                        {
                            return sourceElement != null ? sourceElement.getName() : "[Select...]";
                        }
                        @Override
                        public void setSourceElement(String value)
                        {
                            for (CircuitElement element : elementManager.getElements())
                            {
                                if (element.getName().equals(value))
                                {
                                    sourceElement = element;
                                    return;
                                }
                            }
                        }
                        @Override
                        public String getSourceNode1()
                        {
                            return sourceNode1 == null ? "[...]" : sourceNode1;
                        }
                        @Override
                        public String getSourceNode2()
                        {
                            return sourceNode2 == null ? "[...]" : sourceNode2;
                        }
                        @Override
                        public void setSourceNode1(String node1)
                        {
                            sourceNode1 = node1;
                        }
                        @Override
                        public void setSourceNode2(String node2)
                        {
                            sourceNode2 = node2;
                        }
                        @Override
                        public ArrayList<CircuitElement> getSourceElementsList()
                        {
                            ArrayList<CircuitElement> elements = super.getSourceElementsList();
                            elements.remove(self);
                            return elements;
                        }
                    },
                    new ScalarProperty("Gain", "")
                    {
                        @Override
                        public String getUnit()
                        {
                            return "";
                        }
                        @Override
                        public void setUnit(String unit)
                        {
                        }
                        @Override
                        public String getValue()
                        {
                            return String.valueOf(gain);
                        }
                        @Override
                        public void _trySetValue(String value)
                        {
                            gain = Float.parseFloat(value);
                        }
                    }
            };
        }
        return properties;
    }

    @Override
    public String toNetlistString(String[] nodes, NodeCrawler crawler)
    {
        String s = super.toNetlistString(nodes, crawler);
        if (typeId == CURRENT_CONTROLLED)
        {
            s += String.format("%s %f", sourceElement.getName(), gain);
        }
        else if (typeId == VOLTAGE_CONTROLLED)
        {
            s += String.format("%s %s %f", sourceNode1, sourceNode2, gain);
        }

        return s;
    }
}
