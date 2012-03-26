package sriracha.frontend.android;

import java.util.*;

public class WireNode implements IWireNode
{
    private ArrayList<WireSegment> segments = new ArrayList<WireSegment>(4); // Sticking to a 90deg grid.

    public int x;
    public int y;

    public WireNode(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void replaceSegment(WireSegment oldSegment, WireSegment newSegment)
    {
        if (!segments.remove(oldSegment))
            throw new IllegalArgumentException("Segment not found in collection");
        segments.add(newSegment);
    }

    @Override
    public void addSegment(WireSegment segment)
    {
        segments.add(segment);
    }

    @Override
    public ArrayList<WireSegment> getSegments()
    {
        return segments;
    }

    public ArrayList<IWireNode> getNeighbours()
    {
        ArrayList<IWireNode> neighbours = new ArrayList<IWireNode>();
        for (WireSegment segment : segments)
        {
            IWireNode neighbour = segment.getStart() != this ? segment.getStart() : segment.getEnd();
            neighbours.add(neighbour);
        }
        return neighbours;
    }

    public WireNode duplicate(WireSegment segment, WireManager wireManager)
    {
        WireNode newNode = new WireNode(x, y);
        wireManager.addNode(newNode);

        // Connect the segment that's being moved to the new node.
        segment.replaceNode(this, newNode);
        newNode.addSegment(segment);

        // Connect the old node and the new node with a brand new segment
        WireSegment newSegment = new WireSegment(segment.getContext(), this, newNode);
        wireManager.addSegment(newSegment);

        newNode.addSegment(newSegment);
        this.replaceSegment(segment, newSegment);

        return newNode;
    }

    @Override
    public boolean duplicateOnMove(WireSegment segment)
    {
        IWireNode neighbour = segment.getStart() != this ? segment.getStart() : segment.getEnd();
        return hasNeighbourOpposite(neighbour);
    }

    private boolean hasNeighbourOpposite(IWireNode neighbour)
    {
        for (IWireNode possibleOpposite : getNeighbours())
        {
            if (possibleOpposite != neighbour
                    && (possibleOpposite.getX() == neighbour.getX() || possibleOpposite.getY() == neighbour.getY()))
                return true;
        }
        return false;
    }

    public WireSegment getSegmentTowardX(int x)
    {
        for (WireSegment segment : segments)
        {
            IWireNode other = segment.getStart() != this ? segment.getStart() : segment.getEnd();
            if (x > this.x && other.getX() > this.x)
                return segment;
            else if (x < this.x && other.getX() < this.x)
                return segment;
        }
        return null;
    }

    public WireSegment getSegmentTowardY(int y)
    {
        for (WireSegment segment : segments)
        {
            IWireNode other = segment.getStart() != this ? segment.getStart() : segment.getEnd();
            if (y > this.y && other.getY() > this.y)
                return segment;
            else if (y < this.y && other.getY() < this.y)
                return segment;
        }
        return null;
    }

    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }

    @Override
    public String toString()
    {
        return String.format("(%d, %d)", x, y);
    }
}
