package sriracha.frontend.android.model;

import android.content.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.elements.*;
import sriracha.frontend.android.model.elements.ctlsources.*;
import sriracha.frontend.android.model.elements.sources.*;
import sriracha.frontend.model.*;
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

    public CircuitElementView instantiateElement(int elementId, float positionX, float positionY, CircuitElementManager elementManager)
    {
        switch (elementId)
        {
            case R.id.sources_voltage:
                return new VoltageSourceView(context, new VoltageSource(elementManager), positionX, positionY);

            case R.id.sources_current:
                return new CurrentSourceView(context, new CurrentSource(elementManager), positionX, positionY);

            case R.id.sources_dependent_voltage:
                return new DependentVoltageSourceView(context, new DependentVoltageSource(elementManager), positionX, positionY);

            case R.id.sources_dependent_current:
                return new DependentCurrentSourceView(context, new DependentCurrentSource(elementManager), positionX, positionY);

            case R.id.sources_vcc:
                return new VCCView(context, new VCC(elementManager), positionX, positionY);

            case R.id.sources_ground:
                return new GroundView(context, new Ground(elementManager), positionX, positionY);

            case R.id.rlc_resistor:
                return new ResistorView(context, new Resistor(elementManager), positionX, positionY);

            case R.id.rlc_capacitor:
                return new CapacitorView(context, new Capacitor(elementManager), positionX, positionY);

            case R.id.rlc_inductor:
                return new InductorView(context, new Inductor(elementManager), positionX, positionY);

            default:
                return null;
        }
    }
}
