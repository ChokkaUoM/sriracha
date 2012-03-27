package sriracha.frontend.model.elements;

import sriracha.frontend.model.*;

public class Resistor extends TwoPortElement
{
    private float resistance;
    private String unit;

    public Property[] getProperties()
    {
        return new Property[]{
                new ScalarProperty("Resistance", "Ω")
                {
                    @Override
                    public String getValue()
                    {
                        return resistance == 0 ? "" : String.valueOf(resistance);
                    }
                    @Override
                    public void trySetValue(String value)
                    {
                        resistance = Float.parseFloat(value);
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
