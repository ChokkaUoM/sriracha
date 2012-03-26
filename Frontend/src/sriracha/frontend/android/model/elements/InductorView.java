package sriracha.frontend.android.model.elements;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

public class InductorView extends CircuitElementView
{
    CircuitElementPortView ports[];

    public InductorView(Context context, CircuitElement element, float positionX, float positionY)
    {
        super(context, element, positionX, positionY);
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.rlc_inductor;
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

    @Override
    public String getType()
    {
        return "Inductor";
    }

    @Override
    public String getNameTemplate()
    {
        return "L%d";
    }
}
