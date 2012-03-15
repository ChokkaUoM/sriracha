package sriracha.frontend.android.model;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.elements.sources.*;
import sriracha.frontend.model.elements.sources.*;

public class CircuitElementActivator
{
    Context context;

    public CircuitElementActivator(Context context)
    {
        this.context = context;
    }

    public CircuitElementView instantiateElement(int elementId, float positionX, float positionY)
    {
        switch (elementId)
        {
            case R.id.sources_voltage:
                return new VoltageSourceView(context, new VoltageSource(), positionX, positionY);

            case R.id.sources_current:
                return new CurrentSourceView(context, new CurrentSource(), positionX, positionY);

            default:
                return null;
        }
    }
}
