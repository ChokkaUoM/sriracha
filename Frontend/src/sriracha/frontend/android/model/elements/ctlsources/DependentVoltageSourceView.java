package sriracha.frontend.android.model.elements.ctlsources;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

public class DependentVoltageSourceView extends CircuitElementView
{
    CircuitElementPortView ports[];

    public DependentVoltageSourceView(Context context, CircuitElement element, float positionX, float positionY)
    {
        super(context, element, positionX, positionY);
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
}
