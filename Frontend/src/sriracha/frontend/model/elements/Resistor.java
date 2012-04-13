package sriracha.frontend.model.elements;

import sriracha.frontend.*;
import sriracha.frontend.model.*;

import java.io.*;

public class Resistor extends TwoPortElement implements Serializable
{
    private transient Property[] properties;

    private float resistance = 1;
    private String unit = "kΩ";

    public Resistor(CircuitElementManager elementManager)
    {
        super(elementManager);
    }

    public Property[] getProperties()
    {
        if (properties == null)
        {
            properties = new Property[]{
                    new ScalarProperty("Resistance", "Ω")
                    {
                        @Override
                        public String getValue()
                        {
                            return String.valueOf(resistance);
                        }
                        @Override
                        public void _trySetValue(String value)
                        {
                            float floatValue = Float.parseFloat(value);
                            if (floatValue <= 0)
                                throw new NumberFormatException("Resistor value must be greater than zero");
                            resistance = floatValue;
                        }
                        @Override
                        public String getUnit()
                        {
                            return unit == null || unit.isEmpty() ? this.getBaseUnit() : unit;
                        }
                        @Override
                        public void setUnit(String newUnit)
                        {
                            unit = newUnit;
                        }
                    }
            };
        }
        return properties;
    }

    @Override
    public String getType()
    {
        return "Resistor";
    }

    @Override
    public String getNameTemplate()
    {
        return "R%d";
    }

    @Override
    public String toNetlistString(String[] nodes, NodeCrawler crawler)
    {
        return super.toNetlistString(nodes, crawler) + resistance + ScalarProperty.translateUnit(unit);
    }
}
