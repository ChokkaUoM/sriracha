package sriracha.frontend;

import sriracha.frontend.android.designer.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

import java.util.*;

public class NodeCrawler
{
    public NetlistNode mapSegmentToNode(WireSegment segment, WireManager wireManager)
    {
        CircuitElementPortView port = findSegmentPort(segment, new HashSet<WireSegment>());
        if (port != null)
        {
            ArrayList<NetlistNode> nodes = getNodeList(wireManager);
            for (NetlistNode node : nodes)
            {
                if (node.getPorts().contains(port))
                    return node;
            }
        }
        return null;
    }

    public HashMap<IWireIntersection, NetlistNode> getIntersectionNodeMap(WireManager wireManager)
    {
        HashMap<IWireIntersection, NetlistNode> map = new HashMap<IWireIntersection, NetlistNode>();
        ArrayList<NetlistNode> nodes = getNodeList(wireManager);
        for (NetlistNode node : nodes)
        {
            for (IWireIntersection intersection : node.getPorts())
            {
                map.put(intersection, node);
            }
            for (IWireIntersection intersection : node.getIntersections())
            {
                map.put(intersection, node);
            }
        }
        return map;
    }

    public int[] getNodeIndices(ArrayList<NetlistNode> nodes, CircuitElement element)
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

    public ArrayList<NetlistNode> getNodeList(WireManager wireManager)
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
            ArrayList<WireIntersection> intersections = new ArrayList<WireIntersection>();

            followWire(seg.getStart(), seg, ports, intersections, processedSegments);
            followWire(seg.getEnd(), seg, ports, intersections, processedSegments);

            nodes.add(new NetlistNode(ports, intersections, nextNodeIndex++));
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

        ArrayList<WireSegment> toProcess = new ArrayList<WireSegment>();
        toProcess.addAll(parent.getStart().getSegments());
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

    private void followWire(IWireIntersection intersection, WireSegment parent, ArrayList<CircuitElementPortView> ports, ArrayList<WireIntersection> intersections, HashSet<WireSegment> processedSegments)
    {
        if (intersection instanceof CircuitElementPortView)
        {
            ports.add((CircuitElementPortView) intersection);
            return;
        }

        intersections.add((WireIntersection) intersection);

        for (WireSegment seg : intersection.getSegments())
        {
            processedSegments.add(seg);

            if (seg == parent)
                continue;

            if (seg.getStart() != intersection)
                followWire(seg.getStart(), seg, ports, intersections, processedSegments);
            else
                followWire(seg.getEnd(), seg, ports, intersections, processedSegments);
        }
    }

}
