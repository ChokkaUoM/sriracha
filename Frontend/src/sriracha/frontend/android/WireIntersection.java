package sriracha.frontend.android;

import java.util.*;

public class WireIntersection implements IWireIntersection
{
    private ArrayList<WireSegment> segments = new ArrayList<WireSegment>(4); // Sticking to a 90deg grid.

    public int x;
    public int y;

    public WireIntersection(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void addSegment(WireSegment segment)
    {
        segments.add(segment);
    }

    @Override
    public void replaceSegment(WireSegment oldSegment, WireSegment newSegment)
    {
        if (!segments.remove(oldSegment))
            throw new IllegalArgumentException("Segment not found in collection");
        segments.add(newSegment);
    }

    @Override
    public void removeSegment(WireSegment segment)
    {
        segments.remove(segment);
    }

    @Override
    public ArrayList<WireSegment> getSegments()
    {
        return segments;
    }

    public ArrayList<IWireIntersection> getNeighbours()
    {
        ArrayList<IWireIntersection> neighbours = new ArrayList<IWireIntersection>();
        for (WireSegment segment : segments)
        {
            IWireIntersection neighbour = segment.getStart() != this ? segment.getStart() : segment.getEnd();
            neighbours.add(neighbour);
        }
        return neighbours;
    }

    public WireIntersection duplicate(WireSegment segment, WireManager wireManager)
    {
        WireIntersection newIntersection = new WireIntersection(x, y);
        wireManager.addIntersection(newIntersection);

        // Connect the segment that's being moved to the new node.
        segment.replaceIntersection(this, newIntersection);
        newIntersection.addSegment(segment);

        // Connect the old node and the new node with a brand new segment
        WireSegment newSegment = new WireSegment(segment.getContext(), wireManager, this, newIntersection);
        wireManager.addSegment(newSegment);

        newIntersection.addSegment(newSegment);
        this.replaceSegment(segment, newSegment);

        return newIntersection;
    }

    @Override
    public boolean duplicateOnMove(WireSegment segment)
    {
        IWireIntersection neighbour = segment.getStart() != this ? segment.getStart() : segment.getEnd();
        return hasNeighbourOpposite(neighbour);
    }

    private boolean hasNeighbourOpposite(IWireIntersection neighbour)
    {
        for (IWireIntersection possibleOpposite : getNeighbours())
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
            IWireIntersection other = segment.getStart() != this ? segment.getStart() : segment.getEnd();
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
            IWireIntersection other = segment.getStart() != this ? segment.getStart() : segment.getEnd();
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
