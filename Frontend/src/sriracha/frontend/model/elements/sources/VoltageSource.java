package sriracha.frontend.model.elements.sources;

import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.*;

public class VoltageSource extends TwoPortElement
{
    private Property[] properties;

    private boolean isDC = true;

    private float dcVoltage;
    private String dcVoltageUnit;

    private float amplitude;
    private String amplitudeUnit;

    private float frequency;
    private String frequencyUnit;

    private float phase;
    private String phaseUnit;

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
                public void trySetValue(String value)
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

            final ScalarProperty acProp = new ScalarProperty("AC Voltage", "V")
            {
                @Override
                public String getValue()
                {
                    return amplitude == 0 ? "" : String.valueOf(amplitude);
                }
                @Override
                public void trySetValue(String value)
                {
                    amplitude = Float.parseFloat(value);
                }
                @Override
                public String getUnit()
                {
                    return amplitudeUnit == null || amplitudeUnit.isEmpty() ? this.getBaseUnit() : amplitudeUnit;
                }
                @Override
                public void setUnit(String newUnit)
                {
                    amplitudeUnit = newUnit;
                }
            };

            final ScalarProperty freqProp = new ScalarProperty("Frequency", "Hz")
            {
                @Override
                public String getValue()
                {
                    return frequency == 0 ? "" : String.valueOf(frequency);
                }
                @Override
                public void trySetValue(String value)
                {
                    frequency = Float.parseFloat(value);
                }
                @Override
                public String getUnit()
                {
                    return frequencyUnit == null || frequencyUnit.isEmpty() ? this.getBaseUnit() : frequencyUnit;
                }
                @Override
                public void setUnit(String newUnit)
                {
                    frequencyUnit = newUnit;
                }
                @Override
                public String[] getUnitsList()
                {
                    return new String[]{"Hz", "ω"};
                }
            };

            final ScalarProperty phaseProp = new ScalarProperty("Phase", "°")
            {
                @Override
                public String getValue()
                {
                    return phase == 0 ? "" : String.valueOf(phase);
                }
                @Override
                public void trySetValue(String value)
                {
                    phase = Float.parseFloat(value);
                }
                @Override
                public String getUnit()
                {
                    return phaseUnit == null || phaseUnit.isEmpty() ? this.getBaseUnit() : phaseUnit;
                }
                @Override
                public void setUnit(String newUnit)
                {
                    phaseUnit = newUnit;
                }
                @Override
                public String[] getUnitsList()
                {
                    return new String[]{"°", "rad"};
                }
            };

            properties = new Property[]{
                    dcProp,
                    new BooleanProperty("Alternating Current")
                    {
                        @Override
                        public void trySetValue(String newValue)
                        {
                            super.trySetValue(newValue);
                            acProp.setEnabled(value);
                            freqProp.setEnabled(value);
                            phaseProp.setEnabled(value);
                        }
                    },
                    acProp, freqProp, phaseProp
            };
        }
        return properties;
    }
}
