package sriracha.frontend.android.model.elements.ctlsources;

import android.content.Context;
import sriracha.frontend.R;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.android.model.CircuitElementView;
import sriracha.frontend.model.CircuitElement;

import java.util.*;

public class DependentCurrentSourceView extends CircuitElementView
{
    CircuitElementPortView ports[];

    public DependentCurrentSourceView(Context context, CircuitElement element, float positionX, float positionY, WireManager wireManager)
    {
        super(context, element, positionX, positionY, wireManager);
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.sources_dependent_current;
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
        return UUID.fromString("38a63260-8841-4d67-be54-14bbb68e3eb6");
    }
}
