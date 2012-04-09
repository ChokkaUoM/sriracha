package sriracha.frontend;

import sriracha.frontend.android.IWireIntersection;
import sriracha.frontend.android.WireManager;
import sriracha.frontend.android.WireSegment;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.model.CircuitElement;
import sriracha.frontend.model.CircuitElementManager;
import sriracha.frontend.model.CircuitElementPort;
import sriracha.frontend.model.elements.sources.Ground;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * Generates the netlist representation of the current circuit and analysis requests
 */
public class NetlistGenerator
{
    public class NetlistNode
    {
        public ArrayList<CircuitElementPortView> ports;
        public int index;

        @Override
        public String toString()
        {
            return isGround() ? "0" : "n" + index;
        }

        private NetlistNode(ArrayList<CircuitElementPortView> ports, int index)
        {
            this.ports = ports;
            this.index = index;
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
    }

    public String generate(WireManager wireManager, CircuitElementManager circuitElementManager)
    {
        ArrayList<NetlistNode> nodes = getNodeList(wireManager);
        String result = "Generated Netlist\n"; // First line is name of netlist

        for (CircuitElement element : circuitElementManager.getElements())
        {
            int[] indices = getNodeIndices(nodes, element);

            if (nodes.size() == 2)
            {
                NetlistNode node1 = nodes.get(indices[0]);
                NetlistNode node2 = nodes.get(indices[1]);

                CircuitElementPort[] ports = element.getPorts();
                if (node2.containsPort(ports[0]))
                {
                    // swap node1 and node2
                    NetlistNode temp = node1;
                    nodes.set(0, node2);
                    nodes.set(1, temp);
                }
            }

            String[] nodeStrings = new String[nodes.size()];
            for (int i = 0; i < nodes.size(); i++)
                nodeStrings[i] = nodes.get(i).toString();

            result += element.toNetlistString(nodeStrings) + "\n";
        }

        return result;
    }

    public NetlistNode mapSegmentToNode(WireSegment segment, WireManager wireManager)
    {
        CircuitElementPortView port = findSegmentPort(segment, new HashSet<WireSegment>());
        if (port != null)
        {
            ArrayList<NetlistNode> nodes = getNodeList(wireManager);
            for (NetlistNode node : nodes)
            {
                if (node.ports.contains(port))
                    return node;
            }
        }
        return null;
    }

    private int[] getNodeIndices(ArrayList<NetlistNode> nodes, CircuitElement element)
    {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < nodes.size(); i++)
        {
            if (nodes.get(i).connectsToElement(element))
                indices.add(i);
        }

        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++)
            indicesArray[i] = indices.get(i);

        return indicesArray;
    }

    private ArrayList<NetlistNode> getNodeList(WireManager wireManager)
    {
        ArrayList<WireSegment> segments = wireManager.getSegments();
        HashSet<WireSegment> processedSegments = new HashSet<WireSegment>();
        ArrayList<NetlistNode> nodes = new ArrayList<NetlistNode>();
        int nextNodeIndex = 1;

        for (WireSegment seg : segments)
        {
            if (processedSegments.contains(seg))
                continue;

            ArrayList<CircuitElementPortView> ports = new ArrayList<CircuitElementPortView>();

            followWire(seg.getStart(), seg, ports, processedSegments);
            followWire(seg.getEnd(), seg, ports, processedSegments);

            nodes.add(new NetlistNode(ports, nextNodeIndex++));
        }

        return nodes;
    }

    private CircuitElementPortView findSegmentPort(WireSegment parent, HashSet<WireSegment> processedSegments)
    {
        if (parent.getStart() instanceof CircuitElementPortView)
            return (CircuitElementPortView) parent.getStart();
        if (parent.getEnd() instanceof CircuitElementPortView)
            return (CircuitElementPortView) parent.getEnd();

        processedSegments.add(parent);

        ArrayList<WireSegment> toProcess = parent.getStart().getSegments();
        toProcess.addAll(parent.getEnd().getSegments());
        toProcess.removeAll(processedSegments);

        for (WireSegment segment : toProcess)
        {
            CircuitElementPortView port = findSegmentPort(segment, processedSegments);
            if (port != null)
                return port;
        }
        return null;
    }

    private void followWire(IWireIntersection intersection, WireSegment parent, ArrayList<CircuitElementPortView> ports, HashSet<WireSegment> processedSegments)
    {
        if (intersection instanceof CircuitElementPortView)
        {
            ports.add((CircuitElementPortView) intersection);
            return;
        }

        for (WireSegment seg : intersection.getSegments())
        {
            processedSegments.add(seg);

            if (seg == parent)
                continue;

            if (seg.getStart() != intersection)
                followWire(seg.getStart(), seg, ports, processedSegments);
            else
                followWire(seg.getEnd(), seg, ports, processedSegments);
        }
    }
}
