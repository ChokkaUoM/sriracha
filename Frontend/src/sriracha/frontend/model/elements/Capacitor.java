package sriracha.frontend.model.elements;

import sriracha.frontend.model.*;

public class Capacitor extends TwoPortElement
{
    private Property[] properties;

    private float capacitance = 1;
    private String unit = "μF";

    public Capacitor(CircuitElementManager elementManager)
    {
        super(elementManager);
    }

    @Override
    public Property[] getProperties()
    {
        if (properties == null)
        {
            properties = new Property[]{
                    new ScalarProperty("Capacitance", "F")
                    {
                        @Override
                        public String getValue()
                        {
                            return String.valueOf(capacitance);
                        }
                        @Override
                        public void _trySetValue(String value)
                        {
                            float floatValue = Float.parseFloat(value);
                            if (floatValue <= 0)
                                throw new NumberFormatException("Capacitance value must be greater than zero");
                            capacitance = floatValue;
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
        return "Capacitor";
    }

    @Override
    public String getNameTemplate()
    {
        return "C%d";
    }

    @Override
    public String toNetlistString(String[] nodes)
    {
        return super.toNetlistString(nodes) + capacitance + ScalarProperty.translateUnit(unit);
    }
}
