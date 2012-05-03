package sriracha.frontend.android.persistence;

import sriracha.frontend.android.AnalysisMenu;
import sriracha.frontend.android.designer.CircuitDesigner;
import sriracha.frontend.android.designer.IWireIntersection;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.designer.WireSegment;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.android.model.CircuitElementView;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Responsible for serializing data for saving and loading circuits.
 * Although Java provides built-in serialization facilities through implementation
 * of the Serializable interface, there is a restriction that prevents us from using
 * it in certain cases.
 * If a class implements Serializable and its parent class does not, then the parent
 * class must provide a no-argument constructor.
 * This is not the case for the Android View class, therefore we cannot automatically
 * serialize our View subclasses. For these, we use manual serialization, writing the
 * relevant data to an ObjectOutputStream that is passed in. For serialization of 
 * relationships between two non-serizable classes (such as the CircuitElementView-CircuitElementPortView relationship),
 * we generate UUIDs to stand in place of the objects and store those instead.
 */
public class Serialization
{
    private CircuitDesigner circuitDesigner;
    private AnalysisMenu analysisMenu;

    public Serialization(CircuitDesigner circuitDesigner, AnalysisMenu analysisMenu)
    {
        this.circuitDesigner = circuitDesigner;
        this.analysisMenu = analysisMenu;
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

        //serialize analysis menu
        analysisMenu.serialize(out);
    }

    public void deserialize(ObjectInputStream in) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException
    {
        int numElements = in.readInt();
        HashMap<UUID, CircuitElementView> uuidElementMap = new HashMap<UUID, CircuitElementView>(numElements);
        for (int i = 0; i < numElements; i++)
        {
            UUID elementUUID = (UUID) in.readObject();
            CircuitElementView element = circuitDesigner.instantiateElement(elementUUID);
            if (element != null)
            {
                element.deserialize(in);
                uuidElementMap.put(element.getUUID(), element);
                circuitDesigner.addElement(element);
            } else
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

                String elementName = element.getElement().getName();
                element.getElement().init(circuitDesigner.getElementManager());
                element.getElement().setName(elementName);

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

        analysisMenu.deserialize(in);
    }

    private void serializeElement(CircuitElementView elementView, ObjectOutputStream out) throws IOException
    {
        out.writeObject(elementView.getTypeUUID());
        elementView.serialize(out);
    }
}
