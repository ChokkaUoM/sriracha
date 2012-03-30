package sriracha.frontend.android.model.elements.sources;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.sources.*;

public class CurrentSourceView extends CircuitElementView implements Property.OnPropertyValueChangedListener
{
    CircuitElementPortView ports[];

    public CurrentSourceView(Context context, CircuitElement element, float positionX, float positionY, WireManager wireManager)
    {
        super(context, element, positionX, positionY, wireManager);
        for (Property property : element.getProperties())
        {
            if (property instanceof ScalarProperty && ((ScalarProperty)property).getName().equalsIgnoreCase("AC Current"))
            {
                property.setOnPropertyValueChangedListener(this);
            }
        }
    }

    @Override
    public int getDrawableId()
    {
        return R.drawable.sources_current;
    }

    public int getAcDrawableId()
    {
        return R.drawable.sources_current_ac;
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
    public void onPropertyValueChanged(Property property)
    {
        CurrentSource currentSource = (CurrentSource)getElement();
        int drawable = currentSource.getAmplitude() == 0 ? getDrawableId() : getAcDrawableId();
        setImageResource(drawable);
        invalidate();
    }
}
