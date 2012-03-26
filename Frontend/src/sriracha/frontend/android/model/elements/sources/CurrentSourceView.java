package sriracha.frontend.android.model.elements.sources;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

public class CurrentSourceView extends CircuitElementView
{
    CircuitElementPortView ports[];

    public CurrentSourceView(Context context, CircuitElement element, float positionX, float positionY)
    {
        super(context, element, positionX, positionY);
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.sources_current;
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
    public String getType()
    {
        return "Current Source";
    }

    @Override
    public String getNameTemplate()
    {
        return "I%d";
    }
}
