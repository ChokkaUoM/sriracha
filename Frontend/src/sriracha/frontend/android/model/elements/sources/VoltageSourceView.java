package sriracha.frontend.android.model.elements.sources;

import android.content.Context;
import sriracha.frontend.R;
import sriracha.frontend.android.*;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.android.model.CircuitElementView;
import sriracha.frontend.model.CircuitElement;
import sriracha.frontend.model.Property;
import sriracha.frontend.model.ScalarProperty;
import sriracha.frontend.model.elements.sources.VoltageSource;

import java.util.*;

public class VoltageSourceView extends CircuitElementView implements ScalarProperty.OnPropertyValueChangedListener
{
    private CircuitElementPortView ports[];

    public VoltageSourceView(Context context, CircuitElement element, float positionX, float positionY, WireManager wireManager)
    {
        super(context, element, positionX, positionY, wireManager);
        for (Property property : element.getProperties())
        {
            if (property instanceof ScalarProperty && ((ScalarProperty) property).getName().equalsIgnoreCase("AC Voltage"))
            {
                ((ScalarProperty) property).setOnPropertyValueChangedListener(this);
            }
        }
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.sources_voltage;
    }

    public int getAcDrawableId()
    {
        return R.drawable.sources_voltage_ac;
    }

    @Override
    public CircuitElementPortView[] getPortViews()
    {
        if (ports == null)
        {
            ports = new CircuitElementPortView[]{
                    new CircuitElementPortView(this, 0, -0.5f),
                    new CircuitElementPortView(this, 0, 0.5f),
            };
        }
        return ports;
    }

    @Override
    public void onPropertyValueChanged(Property property)
    {
        VoltageSource voltageSource = (VoltageSource) getElement();
        int drawable = voltageSource.getAmplitude() == 0 ? getDrawableId() : getAcDrawableId();
        setImageResource(drawable);
        invalidate();
    }

    @Override
    public UUID getTypeUUID()
    {
        return ElementTypeUUID.VOLTAGE_SOURCE;
    }
}
