package sriracha.frontend.model.elements;

import sriracha.frontend.model.*;

public class Inductor extends TwoPortElement
{
    private Property[] properties;

    private float inductance;
    private String unit;

    @Override
    public Property[] getProperties()
    {
        if (properties == null)
        {
            return new Property[]{
                    new ScalarProperty("Inductance", "H")
                    {
                        @Override
                        public String getValue()
                        {
                            return inductance == 0 ? "" : String.valueOf(inductance);
                        }
                        @Override
                        public void trySetValue(String value)
                        {
                            inductance = Float.parseFloat(value);
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
}
