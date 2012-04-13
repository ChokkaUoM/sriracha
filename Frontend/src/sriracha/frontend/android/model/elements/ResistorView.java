package sriracha.frontend.android.model.elements;

import android.content.Context;
import sriracha.frontend.R;
import sriracha.frontend.android.*;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.android.model.CircuitElementView;
import sriracha.frontend.model.CircuitElement;

import java.util.*;

public class ResistorView extends CircuitElementView
{
    CircuitElementPortView ports[];

    public ResistorView(Context context, CircuitElement element, float positionX, float positionY, WireManager wireManager)
    {
        super(context, element, positionX, positionY, wireManager);
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.rlc_resistor;
    }

    @Override
    public CircuitElementPortView[] getPortViews()
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
        return ElementTypeUUID.RESISTOR;
    }
}
