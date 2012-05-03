package sriracha.frontend.model.elements.sources;

import sriracha.frontend.NodeCrawler;
import sriracha.frontend.model.CircuitElement;
import sriracha.frontend.model.CircuitElementManager;
import sriracha.frontend.model.Property;
import sriracha.frontend.model.ScalarProperty;

public class VCC extends CircuitElement
{
    private transient Property[] properties;

    private float dcVoltage;
    private String dcVoltageUnit;

    public VCC(CircuitElementManager elementManager)
    {
        super(elementManager);
    }

    public float getDcVoltage()
    {
        return dcVoltage;
    }

    public void setDcVoltage(float dcVoltage)
    {
        this.dcVoltage = dcVoltage;
    }

    public String getDcVoltageUnit()
    {
        return dcVoltageUnit;
    }

    public void setDcVoltageUnit(String dcVoltageUnit)
    {
        this.dcVoltageUnit = dcVoltageUnit;
    }

    @Override
    public Property[] getProperties()
    {
        if (properties == null)
        {
            final ScalarProperty dcProp = new ScalarProperty("DC Voltage", "V")
            {
                @Override
                public String getValue()
                {
                    return dcVoltage == 0 ? "" : String.valueOf(dcVoltage);
                }

                @Override
                public void _trySetValue(String value)
                {
                    dcVoltage = Float.parseFloat(value);
                }

                @Override
                public String getUnit()
                {
                    return dcVoltageUnit == null || dcVoltageUnit.isEmpty() ? this.getBaseUnit() : dcVoltageUnit;
                }

                @Override
                public void setUnit(String newUnit)
                {
                    dcVoltageUnit = newUnit;
                }
            };

            properties = new Property[]{dcProp};
        }
        return properties;
    }

    @Override
    public int getPortCount()
    {
        return 1;
    }

    @Override
    public String getType()
    {
        return "VCC";
    }

    @Override
    public String getNameTemplate()
    {
        return "VCC%d";
    }

    @Override
    public String toNetlistString(String[] nodes, NodeCrawler crawler)
    {
        return String.format("%s %s 0 %f%s", name, nodes[0], dcVoltage, ScalarProperty.translateUnit(dcVoltageUnit));
    }
}
