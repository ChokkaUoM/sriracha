package sriracha.frontend.android;

import android.content.*;
import android.graphics.*;
import android.view.*;

import java.util.*;

public class WireManager
{
    private ArrayList<WireSegment> segments = new ArrayList<WireSegment>();
    private ArrayList<IWireNode> nodes = new ArrayList<IWireNode>();

    private ViewGroup canvasView;
    private Context context;

    public WireManager(ViewGroup canvasView)
    {
        this.canvasView = canvasView;
        context = canvasView.getContext();
    }

    public void addNode(IWireNode node)
    {
        if (!nodes.contains(node))
            nodes.add(node);
    }

    public void connectNewNode(IWireNode from, IWireNode to)
    {
        if (!nodes.contains(from))
            throw new IllegalArgumentException("from");

        // Add intermediate node to keep everything orthogonal
        if (from.getX() != to.getX() && from.getY() != to.getY())
        {
            WireNode intermediate = new WireNode(from.getX(), to.getY());
            connectNewNode(from, intermediate);
            from = intermediate;
        }

        nodes.add(to);

        WireSegment segment = new WireSegment(context, from, to);
        from.addSegment(segment);
        to.addSegment(segment);
        addSegment(segment);

        consolidateNodes();
    }

    public WireNode splitSegment(WireSegment segment, int x, int y)
    {
        if (!segment.isPointOnSegment(x, y))
            throw new IllegalArgumentException("Point not on segment");

        // Create two new segments by splitting the original one up.
        WireNode node = new WireNode(x, y);
        WireSegment firstHalf = new WireSegment(context, segment.getStart(), node);
        WireSegment secondHalf = new WireSegment(context, node, segment.getEnd());

        addSegment(firstHalf);
        addSegment(secondHalf);

        // Nodes know what segments they're attached to, so we update that information.
        segment.getStart().replaceSegment(segment, firstHalf);
        segment.getEnd().replaceSegment(segment, secondHalf);

        // The new node needs to know about the two new segments.
        node.addSegment(firstHalf);
        node.addSegment(secondHalf);

        // Get rid of the stale, old segment. We hates it.
        removeSegment(segment);

        return node;
    }

    public void consolidateNodes()
    {
        HashMap<Point, ArrayList<IWireNode>> toConsolidate = new HashMap<Point, ArrayList<IWireNode>>();

        for (int i = 0; i < nodes.size(); i++)
        {
            IWireNode node1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++)
            {
                IWireNode node2 = nodes.get(j);

                if (!(node1 instanceof WireNode) || !(node2 instanceof WireNode))
                    continue;

                // If two nodes have the same coordinates, we store them in a hashtable.
                // The hashtable is indexed by point, and each entry contains a list of
                // nodes that all have the same coordinates.
                if (node1.getX() == node2.getX() && node1.getY() == node2.getY())
                {
                    Point point = new Point(node1.getX(), node1.getY());
                    if (!toConsolidate.containsKey(point))
                        toConsolidate.put(point, new ArrayList<IWireNode>());

                    ArrayList<IWireNode> nodeList = toConsolidate.get(point);
                    if (!nodeList.contains(node1))
                        nodeList.add(node1);
                    if (!nodeList.contains(node2))
                        nodeList.add(node2);
                }
            }
        }

        for (ArrayList<IWireNode> nodeList : toConsolidate.values())
        {
            // When consolidating nodes, each affected segment must have the
            // relevant node replaced.

            // Create a new node to replace all the old ones.
            WireNode newNode = new WireNode(nodeList.get(0).getX(), nodeList.get(0).getY());
            nodes.add(newNode);

            for (IWireNode node : nodeList)
            {
                for (WireSegment segment : node.getSegments())
                {
                    if (segment.getLength() == 0)
                    {
                        removeSegment(segment);
                    }
                    else
                    {
                        newNode.addSegment(segment);
                        segment.replaceNode(node, newNode);
                    }
                }
                nodes.remove(node);
            }
        }

        // Lastly merge collinear segment
        ArrayList<IWireNode> toRemove = new ArrayList<IWireNode>();
        for (IWireNode node : nodes)
        {
            if (node instanceof WireNode && node.getSegments().size() == 2)
            {
                WireSegment seg1 = node.getSegments().get(0);
                WireSegment seg2 = node.getSegments().get(1);
                if (!(seg1.isVertical() ^ seg2.isVertical())) // Both vertical or both horizontal
                {
                    if (seg2.getStart() == node)
                    {
                        seg2.getEnd().replaceSegment(seg2, seg1);
                        seg1.replaceNode(node, seg2.getEnd());
                    }
                    else
                    {
                        seg2.getStart().replaceSegment(seg2, seg1);
                        seg1.replaceNode(node, seg2.getStart());
                    }

                    toRemove.add(node);
                    removeSegment(seg2);
                }
            }
        }
        nodes.removeAll(toRemove);
    }

    public void addSegment(WireSegment segment)
    {
        segments.add(segment);
        canvasView.addView(segment);
    }

    private void removeSegment(WireSegment segment)
    {
        segments.remove(segment);
        canvasView.removeView(segment);
    }

    public WireSegment getSegmentByPosition(float x, float y)
    {
        for (WireSegment segment : segments)
        {
            if (segment.getBounds().contains((int) x, (int) y))
                return segment;
        }
        return null;
    }

    public void selectSegment(WireSegment toSelect)
    {
        for (WireSegment segment : segments)
        {
            segment.setSelected(segment == toSelect);
        }
    }

    public void deleteSelectedSegment()
    {
        for (WireSegment segment : segments)
        {
            if (segment.isSelected())
            {
                segment.getStart().removeSegment(segment);
                segment.getEnd().removeSegment(segment);
                removeSegment(segment);
                consolidateNodes();
                break;
            }
        }
    }

    public void invalidateAll()
    {
        for (WireSegment segment : segments)
            segment.invalidate();
    }
}
