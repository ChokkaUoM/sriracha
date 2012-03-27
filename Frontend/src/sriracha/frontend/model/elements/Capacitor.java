package sriracha.frontend.model.elements;

import sriracha.frontend.model.*;

public class Capacitor extends TwoPortElement
{
    private float capacitance;
    private String unit;

    @Override
    public Property[] getProperties()
    {
        return new Property[]{
                new ScalarProperty("Capacitance", "F")
                {
                    @Override
                    public String getValue()
                    {
                        return capacitance == 0 ? "" : String.valueOf(capacitance);
                    }
                    @Override
                    public void trySetValue(String value)
                    {
                        capacitance = Float.parseFloat(value);
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
}
