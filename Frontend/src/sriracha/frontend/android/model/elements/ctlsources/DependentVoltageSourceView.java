package sriracha.frontend.android.model.elements.ctlsources;

import android.content.Context;
import sriracha.frontend.R;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.android.model.CircuitElementView;
import sriracha.frontend.model.CircuitElement;

import java.util.*;

public class DependentVoltageSourceView extends CircuitElementView
{
    CircuitElementPortView ports[];

    public DependentVoltageSourceView(Context context, CircuitElement element, float positionX, float positionY, WireManager wireManager)
    {
        super(context, element, positionX, positionY, wireManager);
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.sources_dependent_voltage;
    }

    @Override
    public CircuitElementPortView[] getElementPorts()
    {
        if (ports == null)
        {
            ports = new CircuitElementPortView[]{
                    new CircuitElementPortView(this, 0, 0.5f),
                    new CircuitElementPortView(this, 0, -0.5f),
            };
        }
        return ports;
    }

    @Override
    public UUID getTypeUUID()
    {
        return UUID.fromString("cd36e545-67d6-4ff1-86ab-7dbd5dd4aefa");
    }
}
