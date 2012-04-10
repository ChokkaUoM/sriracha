package sriracha.frontend;

import sriracha.frontend.android.designer.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;
import sriracha.frontend.model.elements.sources.*;

import java.util.*;

public class NetlistNode
{
    private ArrayList<CircuitElementPortView> ports;
    private ArrayList<WireIntersection> intersections;
    private int index;

    public NetlistNode(ArrayList<CircuitElementPortView> ports, ArrayList<WireIntersection> intersections, int index)
    {
        this.ports = ports;
        this.intersections = intersections;
        this.index = index;
    }

    public ArrayList<CircuitElementPortView> getPorts()
    {
        return ports;
    }
    public ArrayList<WireIntersection> getIntersections()
    {
        return intersections;
    }
    public int getIndex()
    {
        return index;
    }

    public boolean containsPort(CircuitElementPort port)
    {
        return ports.contains(port);
    }

    public boolean connectsToElement(CircuitElement element)
    {
        for (CircuitElementPortView port : ports)
        {
            if (port.getElement().getElement() == element)
                return true;
        }

        return false;
    }

    private boolean isGround()
    {
        for (CircuitElementPortView port : ports)
        {
            if (port.getElement().getElement() instanceof Ground)
                return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return isGround() ? "0" : "n" + index;
    }
}
