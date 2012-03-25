package sriracha.frontend.android;

import android.graphics.*;

import java.util.*;

public class WireManager implements WireView.OnAddVertex
{
    private ArrayList<WireView> wires = new ArrayList<WireView>();

    public ArrayList<WireView> getWires() { return wires; }
    public void add(WireView wire)
    {
        wires.add(wire);
    }

    public boolean connectToExistingSegment(WireNode node)
    {
        for (WireView wire : wires)
        {
            for (int i = 1; i < wire.getVertices().size(); i++)
            {
                WireNode start = wire.getVertices().get(i - 1);
                WireNode end = wire.getVertices().get(i);

                WireSegment segment = new WireSegment(wire, start, end);
                if (segment.isPointOnSegment(node.x, node.y))
                {
                    segment.split(node.x, node.y);
                    return true;
                }
            }
        }
        return false;
    }

    public WireSegment getClosestSegment(float x, float y)
    {
        for (WireView wire : wires)
        {
            for (int i = 1; i < wire.getVertices().size(); i++)
            {
                WireNode start = wire.getVertices().get(i - 1);
                WireNode end = wire.getVertices().get(i);

                WireSegment segment = new WireSegment(wire, start, end);
                if (i == 1)
                    segment.setDuplicateStartOnMove(true);
                if (i == wire.getVertices().size() - 1)
                    segment.setDuplicateEndOnMove(true);

                if (segment.getBounds().contains((int) x, (int) y))
                    return segment;
            }
        }
        return null;
    }

    @Override
    public void onAddVertex(WireView wireView)
    {
        ArrayList<WireNode> allVertices = new ArrayList<WireNode>();
        for (WireView wire : wires)
        {
            allVertices.addAll(wire.getVertices());
        }

        for (WireNode node1 : allVertices)
        {
            for (WireNode node2 : allVertices)
            {
                if (node1 != node2 && node1.x == node2.x && node1.y == node2.y)
                {
                    // TODO: consolidate nodes at same location
                }
            }
        }
    }
}
