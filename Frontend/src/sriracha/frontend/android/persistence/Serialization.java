package sriracha.frontend.android.persistence;

import org.apache.http.entity.*;
import sriracha.frontend.*;
import sriracha.frontend.android.designer.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.android.model.elements.*;
import sriracha.frontend.android.model.elements.ctlsources.*;
import sriracha.frontend.android.model.elements.sources.*;

import java.io.*;
import java.util.*;

public class Serialization
{
    private CircuitDesigner circuitDesigner;

    public Serialization(CircuitDesigner circuitDesigner)
    {
        this.circuitDesigner = circuitDesigner;
    }

    public void serialize(ObjectOutputStream out) throws IOException
    {
        // Serialize elements
        ArrayList<CircuitElementView> elements = circuitDesigner.getElements();
        out.writeInt(elements.size());
        for (CircuitElementView element : elements)
        {
            serializeElement(element, out);
        }

        // Serialize intersections and segments
        ArrayList<WireSegment> segments = circuitDesigner.getWireManager().getSegments();
        out.writeInt(segments.size());
        for (WireSegment segment : segments)
        {
            segment.serialize(out);
        }
    }

    public void deserialize(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        int numElements = in.readInt();
        HashMap<UUID, CircuitElementView> uuidElementMap = new HashMap<UUID, CircuitElementView>(numElements);
        for (int i = 0; i < numElements; i++)
        {
            CircuitElementView element = circuitDesigner.instantiateElement(in.readInt());
            if (element != null)
            {
                element.deserialize(in);
                uuidElementMap.put(element.getUUID(), element);
                circuitDesigner.addElement(element);
            }
            else
                throw new InvalidObjectException("Element not found by ID");
        }

        WireManager wireManager = circuitDesigner.getWireManager();
        int numSegments = in.readInt();
        for (int i = 0; i < numSegments; i++)
        {
            WireSegment segment = new WireSegment(wireManager.getContext(), wireManager, null, null);
            segment.deserialize(in);

            wireManager.addSegment(segment);
            wireManager.addIntersection(segment.getStart());
            wireManager.addIntersection(segment.getEnd());
        }

        for (IWireIntersection intersection : wireManager.getIntersections())
        {
            if (intersection instanceof CircuitElementPortView)
            {
                CircuitElementPortView port = (CircuitElementPortView) intersection;
                CircuitElementView element = uuidElementMap.get(port.getElementUUID());
                if (element == null)
                    throw new InvalidObjectException("Element not found by UUID");

                port.setElement(element);
                for (int i = 0; i < element.getPortUUIDs().length; i++)
                {
                    if (element.getPortUUIDs()[i].equals(port.getUUID()))
                    {
                        element.setPort(i, port);
                        break;
                    }
                }
            }
        }
    }

    private void serializeElement(CircuitElementView elementView, ObjectOutputStream out) throws IOException
    {
        out.writeInt(getElementViewId(elementView));
        elementView.serialize(out);
    }

    private int getElementViewId(CircuitElementView elementView)
    {
        if (elementView instanceof VoltageSourceView)
            return R.id.sources_voltage;

        if (elementView instanceof CurrentSourceView)
            return R.id.sources_current;

        if (elementView instanceof DependentVoltageSourceView)
            return R.id.sources_dependent_voltage;

        if (elementView instanceof DependentCurrentSourceView)
            return R.id.sources_dependent_current;

        if (elementView instanceof VCCView)
            return R.id.sources_vcc;

        if (elementView instanceof GroundView)
            return R.id.sources_ground;

        if (elementView instanceof ResistorView)
            return R.id.rlc_resistor;

        if (elementView instanceof CapacitorView)
            return R.id.rlc_capacitor;

        if (elementView instanceof InductorView)
            return R.id.rlc_inductor;

        return -1;
    }
}
