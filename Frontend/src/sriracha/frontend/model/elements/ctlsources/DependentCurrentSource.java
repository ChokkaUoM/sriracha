package sriracha.frontend.model.elements.ctlsources;

import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.*;
import sriracha.frontend.model.elements.sources.*;

import java.util.*;

public class DependentCurrentSource extends TwoPortElement
{
    private Property[] properties;

    private CircuitElement dependsOn;
    private float gain;

    private String type = "Dependent Current Source";

    public DependentCurrentSource(CircuitElementManager elementManager)
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
                        public String getValue()
                        {
                            return dependsOn != null ? dependsOn.getName() : "[Select...]";
                        }
                        @Override
                        public void _trySetValue(String value)
                        {
                            for (CircuitElement element : elementManager.getElements())
                            {
                                if (element.getName().equals(value))
                                {
                                    dependsOn = element;
                                    if (element instanceof VoltageSource || element instanceof DependentVoltageSource)
                                        type = "VCCS";
                                    else
                                        type = "CCCS";
                                    rename();
                                    return;
                                }
                            }
                        }
                        @Override
                        public ArrayList<CircuitElement> getElementsList()
                        {
                            ArrayList<CircuitElement> elements = super.getElementsList();
                            elements.remove(self);
                            return elements;
                        }
                    },
                    new ScalarProperty("Gain", "") {
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

    private void rename()
    {
        if (type.equals("VCCS") && name.equals("F"+index))
            name = "G" + index;
        else if (type.equals("CCCS") && name.equals("G"+index))
            name = "F" + index;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String getNameTemplate()
    {
        // VCCS - Symbol "G"
        // CCCS - Symbol "F"
        return "G%d";
    }
}
