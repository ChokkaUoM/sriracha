package sriracha.frontend.model.elements;

import sriracha.frontend.model.*;

public class Inductor extends TwoPortElement
{
    private Property[] properties;

    private float inductance = 1;
    private String unit = "μH";

    @Override
    public Property[] getProperties()
    {
        if (properties == null)
        {
            properties = new Property[]{
                    new ScalarProperty("Inductance", "H")
                    {
                        @Override
                        public String getValue()
                        {
                            return String.valueOf(inductance);
                        }
                        @Override
                        public void _trySetValue(String value)
                        {
                            float floatValue = Float.parseFloat(value);
                            if (floatValue <= 0)
                                throw new NumberFormatException("Inductance value must be greater than zero");
                            inductance = floatValue;
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
        return "Inductor";
    }

    @Override
    public String getNameTemplate()
    {
        return "L%d";
    }
}
