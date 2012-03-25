package sriracha.frontend.android;

import android.content.*;
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

    public WireSegment getClosestSegment(float x, float y)
    {
        for (WireSegment segment : segments)
        {
            if (segment.getBounds().contains((int) x, (int) y))
                return segment;
        }
        return null;
    }

    public void invalidateAll()
    {
        for (WireSegment segment : segments)
            segment.invalidate();
    }
}
