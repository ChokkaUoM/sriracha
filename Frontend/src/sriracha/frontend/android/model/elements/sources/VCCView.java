package sriracha.frontend.android.model.elements.sources;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

public class VCCView extends CircuitElementView
{
    CircuitElementPortView ports[];

    public VCCView(Context context, CircuitElement element, float positionX, float positionY, WireManager wireManager)
    {
        super(context, element, positionX, positionY, wireManager);
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.sources_vcc;
    }

    @Override
    public CircuitElementPortView[] getElementPorts()
    {
        if (ports == null)
        {
            ports = new CircuitElementPortView[]{
                    new CircuitElementPortView(this, 0, -0.5f),
            };
        }
        return ports;
    }
}
