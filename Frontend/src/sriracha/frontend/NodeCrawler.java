package sriracha.frontend;

import sriracha.frontend.android.designer.IWireIntersection;
import sriracha.frontend.android.designer.WireIntersection;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.designer.WireSegment;
import sriracha.frontend.android.model.CircuitElementPortView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Responsible for traversing the segment graph and finding the
 * individual nodes
 */
public class NodeCrawler
{

    private WireManager wireManager;

    private ArrayList<NetlistNode> nodes;

    private HashMap<WireSegment, NetlistNode> segmentMap;

    private HashMap<IWireIntersection, NetlistNode> intersectionMap;

    public NodeCrawler(WireManager wireManager)
    {
        this.wireManager = wireManager;
        nodes = new ArrayList<NetlistNode>();
        segmentMap = new HashMap<WireSegment, NetlistNode>();
        intersectionMap = new HashMap<IWireIntersection, NetlistNode>();
    }

    public NetlistNode nodeFromSegment(WireSegment segment)
    {
        return segmentMap.get(segment);
    }

    public NetlistNode nodeFromIntersection(IWireIntersection intersection)
    {
        return intersectionMap.get(intersection);
    }

    /**
     * does the traversal and stores the information for retrieval
     */
    public void computeMappings()
    {
        findNodes();
        findIntersectionMap();
        findSegmentMap();

    }

    private void findSegmentMap()
    {
        for (WireSegment segment : wireManager.getSegments())
        {
            segmentMap.put(segment, intersectionMap.get(segment.getStart()));
        }
    }

    private void findIntersectionMap()
    {
        for (NetlistNode node : nodes)
        {
            for (IWireIntersection intersection : node.getPorts())
            {
                intersectionMap.put(intersection, node);
            }
            for (IWireIntersection intersection : node.getIntersections())
            {
                intersectionMap.put(intersection, node);
            }
        }
    }

    /**
     * Traverses wire graph finding nodes.
     */
    private void findNodes()
    {
        nodes.clear();
        ArrayList<WireSegment> segments = wireManager.getSegments();
        HashSet<WireSegment> processedSegments = new HashSet<WireSegment>();
        int nextNodeIndex = 1;

        for (WireSegment seg : segments)
        {
            if (processedSegments.contains(seg))
                continue;

            processedSegments.add(seg);

            ArrayList<CircuitElementPortView> ports = new ArrayList<CircuitElementPortView>();
            ArrayList<WireIntersection> intersections = new ArrayList<WireIntersection>();

            followWire(seg.getStart(), seg, ports, intersections, processedSegments);
            followWire(seg.getEnd(), seg, ports, intersections, processedSegments);

            nodes.add(new NetlistNode(ports, intersections, nextNodeIndex++));
        }
    }

    public ArrayList<NetlistNode> getNodes()
    {
        return nodes;
    }

    /**
     * Will follow segments through intersections until it finds all the ports and intersections
     * associated to this node. Stores the ports in the ports list and intersections in intersections.
     *
     * @param intersection      current intersection in traversal
     * @param parent            parent segment holding the current intersection
     * @param ports             list of previously found ports during traversal
     * @param intersections     list of previously found intersections during traversal
     * @param processedSegments list of already processed segments.
     */
    private void followWire(IWireIntersection intersection, WireSegment parent, ArrayList<CircuitElementPortView> ports, ArrayList<WireIntersection> intersections, HashSet<WireSegment> processedSegments)
    {
        if (intersection instanceof CircuitElementPortView)
        {
            ports.add((CircuitElementPortView) intersection);
        } else
        {
            intersections.add((WireIntersection) intersection);
        }

        for (WireSegment seg : intersection.getSegments())
        {

            if (seg == parent || processedSegments.contains(seg))
                continue;

            processedSegments.add(seg);

            if (seg.getStart() != intersection)
                followWire(seg.getStart(), seg, ports, intersections, processedSegments);
            else
                followWire(seg.getEnd(), seg, ports, intersections, processedSegments);
        }
    }
}
