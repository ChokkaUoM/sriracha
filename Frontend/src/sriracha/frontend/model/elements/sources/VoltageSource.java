package sriracha.frontend.model.elements.sources;

import sriracha.frontend.*;
import sriracha.frontend.android.*;
import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.*;

import java.io.*;

public class VoltageSource extends TwoPortElement implements Serializable
{
    private transient Property[] properties;

    private float dcVoltage;
    private String dcVoltageUnit;

    private float amplitude;
    private String amplitudeUnit;

    private float frequency;
    private String frequencyUnit;

    private float phase;
    private String phaseUnit;

    public VoltageSource(CircuitElementManager elementManager)
    {
        super(elementManager);
    }

    public float getDcVoltage() { return dcVoltage; }
    public void setDcVoltage(float dcVoltage)
    {
        this.dcVoltage = dcVoltage;
    }
    public String getDcVoltageUnit() { return dcVoltageUnit; }
    public void setDcVoltageUnit(String dcVoltageUnit)
    {
        this.dcVoltageUnit = dcVoltageUnit;
    }

    public float getAmplitude() { return amplitude; }
    public void setAmplitude(float amplitude)
    {
        this.amplitude = amplitude;
    }
    public String getAmplitudeUnit() { return amplitudeUnit; }
    public void setAmplitudeUnit(String amplitudeUnit)
    {
        this.amplitudeUnit = amplitudeUnit;
    }

    public float getFrequency() { return frequency; }
    public void setFrequency(float frequency)
    {
        this.frequency = frequency;
    }
    public String getFrequencyUnit() { return frequencyUnit; }
    public void setFrequencyUnit(String frequencyUnit)
    {
        this.frequencyUnit = frequencyUnit;
    }

    public float getPhase() { return phase; }
    public void setPhase(float phase)
    {
        this.phase = phase;
    }
    public String getPhaseUnit() { return phaseUnit; }
    public void setPhaseUnit(String phaseUnit)
    {
        this.phaseUnit = phaseUnit;
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

            final ScalarProperty acProp = new ScalarProperty("AC Voltage", "V")
            {
                @Override
                public String getValue()
                {
                    return amplitude == 0 ? "" : String.valueOf(amplitude);
                }
                @Override
                public void _trySetValue(String value)
                {
                    if (value.isEmpty())
                        amplitude = 0;
                    else
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
                public void _trySetValue(String value)
                {
                    if (value.isEmpty())
                        frequency = 0;
                    else
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
                public void _trySetValue(String value)
                {
                    if (value.isEmpty())
                        phase = 0;
                    else
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

            properties = new Property[]{dcProp, acProp, /*freqProp, */phaseProp};
        }
        return properties;
    }

    @Override
    public String getType()
    {
        return "Voltage Source";
    }

    @Override
    public String getNameTemplate()
    {
        return "V%d";
    }

    @Override
    public String toNetlistString(String[] nodes, NodeCrawler crawler)
    {
        return super.toNetlistString(nodes, crawler)
                + String.format("DC %f%s AC %f%s %f",
                dcVoltage, ScalarProperty.translateUnit(dcVoltageUnit),
                amplitude, ScalarProperty.translateUnit(amplitudeUnit),
                phase);

    }
}
