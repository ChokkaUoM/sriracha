package sriracha.frontend.android.model.elements.sources;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

public class VCCView extends CircuitElementView
{
    CircuitElementPortView ports[];

    public VCCView(Context context, CircuitElement element, float positionX, float positionY)
    {
        super(context, element, positionX, positionY);
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
                    new CircuitElementPortView(this, 0.5f, 1),
            };
        }
        return ports;
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
}
