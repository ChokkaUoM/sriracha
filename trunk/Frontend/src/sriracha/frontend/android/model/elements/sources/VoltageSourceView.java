package sriracha.frontend.android.model.elements.sources;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.model.*;

public class VoltageSourceView extends CircuitElementView
{
    CircuitElementPortView ports[];

    public VoltageSourceView(Context context, CircuitElement element, float positionX, float positionY)
    {
        super(context, element, positionX, positionY);
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.sources_voltage;
    }

    @Override
    public CircuitElementPortView[] getElementPorts()
    {
        if (ports == null)
        {
            ports = new CircuitElementPortView[]{
                    new CircuitElementPortView(this, 0.5f, 0),
                    new CircuitElementPortView(this, 0.5f, 1),
            };
        }
        return ports;
    }
}