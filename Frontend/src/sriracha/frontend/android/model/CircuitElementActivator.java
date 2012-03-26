package sriracha.frontend.android.model;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.elements.*;
import sriracha.frontend.android.model.elements.ctlsources.*;
import sriracha.frontend.android.model.elements.sources.*;
import sriracha.frontend.model.elements.*;
import sriracha.frontend.model.elements.ctlsources.*;
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

            case R.id.sources_dependent_voltage:
                return new DependentVoltageSourceView(context, new DependentVoltageSource(), positionX, positionY);

            case R.id.sources_dependent_current:
                return new DependentCurrentSourceView(context, new DependentCurrentSource(), positionX, positionY);

            case R.id.sources_vcc:
                return new VCCView(context, new VCC(), positionX, positionY);

            case R.id.sources_ground:
                return new GroundView(context, new Ground(), positionX, positionY);

            case R.id.rlc_resistor:
                return new ResistorView(context, new Resistor(), positionX, positionY);

            case R.id.rlc_capacitor:
                return new CapacitorView(context, new Capacitor(), positionX, positionY);

            case R.id.rlc_inductor:
                return new InductorView(context, new Inductor(), positionX, positionY);

            default:
                return null;
        }
    }
}
