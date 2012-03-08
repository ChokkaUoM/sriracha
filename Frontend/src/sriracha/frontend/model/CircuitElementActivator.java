package sriracha.frontend.model;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.android.model.elements.sources.*;
import sriracha.frontend.model.elements.sources.*;
import sriracha.frontend.util.*;

public class CircuitElementActivator
{
    Context context;

    public CircuitElementActivator(Context context)
    {
        this.context = context;
    }

    public CircuitElementView instantiateElement(int elementId, Vector2 position)
    {
        switch (elementId)
        {
            case R.id.sources_voltage:
                return new VoltageSourceView(context, new VoltageSource(), position);

            case R.id.sources_current:
                return new CurrentSourceView(context, new CurrentSource(), position);

            default:
                return null;
        }
    }
}
