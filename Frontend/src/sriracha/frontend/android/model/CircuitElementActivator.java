package sriracha.frontend.android.model;

import android.content.Context;
import sriracha.frontend.R;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.model.elements.CapacitorView;
import sriracha.frontend.android.model.elements.InductorView;
import sriracha.frontend.android.model.elements.ResistorView;
import sriracha.frontend.android.model.elements.ctlsources.DependentCurrentSourceView;
import sriracha.frontend.android.model.elements.ctlsources.DependentVoltageSourceView;
import sriracha.frontend.android.model.elements.sources.CurrentSourceView;
import sriracha.frontend.android.model.elements.sources.GroundView;
import sriracha.frontend.android.model.elements.sources.VCCView;
import sriracha.frontend.android.model.elements.sources.VoltageSourceView;
import sriracha.frontend.model.CircuitElementManager;
import sriracha.frontend.model.elements.Capacitor;
import sriracha.frontend.model.elements.Inductor;
import sriracha.frontend.model.elements.Resistor;
import sriracha.frontend.model.elements.ctlsources.DependentCurrentSource;
import sriracha.frontend.model.elements.ctlsources.DependentVoltageSource;
import sriracha.frontend.model.elements.sources.CurrentSource;
import sriracha.frontend.model.elements.sources.Ground;
import sriracha.frontend.model.elements.sources.VCC;
import sriracha.frontend.model.elements.sources.VoltageSource;

public class CircuitElementActivator
{
    Context context;

    public CircuitElementActivator(Context context)
    {
        this.context = context;
    }

    public CircuitElementView instantiateElement(int elementId, float positionX, float positionY, CircuitElementManager elementManager, WireManager wireManager)
    {
        switch (elementId)
        {
            case R.id.sources_voltage:
                return new VoltageSourceView(context, new VoltageSource(elementManager), positionX, positionY, wireManager);

            case R.id.sources_current:
                return new CurrentSourceView(context, new CurrentSource(elementManager), positionX, positionY, wireManager);

            case R.id.sources_dependent_voltage:
                return new DependentVoltageSourceView(context, new DependentVoltageSource(elementManager), positionX, positionY, wireManager);

            case R.id.sources_dependent_current:
                return new DependentCurrentSourceView(context, new DependentCurrentSource(elementManager), positionX, positionY, wireManager);

            case R.id.sources_vcc:
                return new VCCView(context, new VCC(elementManager), positionX, positionY, wireManager);

            case R.id.sources_ground:
                return new GroundView(context, new Ground(elementManager), positionX, positionY, wireManager);

            case R.id.rlc_resistor:
                return new ResistorView(context, new Resistor(elementManager), positionX, positionY, wireManager);

            case R.id.rlc_capacitor:
                return new CapacitorView(context, new Capacitor(elementManager), positionX, positionY, wireManager);

            case R.id.rlc_inductor:
                return new InductorView(context, new Inductor(elementManager), positionX, positionY, wireManager);

            default:
                return null;
        }
    }
}
