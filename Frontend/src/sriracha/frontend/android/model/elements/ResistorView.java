package sriracha.frontend.android.model.elements;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

public class ResistorView extends CircuitElementView
{
    public ResistorView(Context context, CircuitElement element, float positionX, float positionY)
    {
        super(context, element, positionX, positionY);
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.rlc_resistor;
    }

    @Override
    public CircuitElementPortView[] getElementPorts()
    {
        // TODO
        return new CircuitElementPortView[0];
    }

    @Override
    public String getType()
    {
        return "Resistor";
    }

    @Override
    public String getNameTemplate()
    {
        return "R%d";
    }
}
