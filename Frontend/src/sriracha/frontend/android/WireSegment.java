package sriracha.frontend.android;

import android.graphics.*;

public class WireSegment
{
    private static final int BOUNDS_PADDING = 20;

    private WireView wire;

    private WireNode start;
    private WireNode end;

    private boolean duplicateStartOnMove;
    private boolean duplicateEndOnMove;

    public WireSegment(WireView wire, WireNode start, WireNode end)
    {
        this.wire = wire;
        this.start = start;
        this.end = end;
    }

    public WireView getWire() { return wire; }
    public WireNode getStart() { return start; }
    public WireNode getEnd() { return end; }

    public void setDuplicateStartOnMove(boolean duplicateStartOnMove)
    {
        this.duplicateStartOnMove = duplicateStartOnMove;
    }
    public void setDuplicateEndOnMove(boolean duplicateEndOnMove)
    {
        this.duplicateEndOnMove = duplicateEndOnMove;
    }

    public void setX(int x)
    {
        if (x != start.x)
        {
            if (start.hasNeighbourOpposite(end))
                duplicateStartOnMove = true;
            if (end.hasNeighbourOpposite(start))
                duplicateEndOnMove = true;

            if (duplicateStartOnMove)
                wire.insertBefore(new WireNode(start), start);
            if (duplicateEndOnMove)
                wire.insertAfter(new WireNode(end), end);
            duplicateStartOnMove = duplicateEndOnMove = false;
        }

        start.x = x;
        end.x = x;
    }

    public void setY(int y)
    {
        if (y != start.y)
        {
            if (start.hasNeighbourOpposite(end))
                duplicateStartOnMove = true;
            if (end.hasNeighbourOpposite(start))
                duplicateEndOnMove = true;

            if (duplicateStartOnMove)
                wire.insertBefore(new WireNode(start), start);
            if (duplicateEndOnMove)
                wire.insertAfter(new WireNode(end), end);
            duplicateStartOnMove = duplicateEndOnMove = false;
        }

        start.y = y;
        end.y = y;
    }

    public WireNode split(int x, int y)
    {
        WireNode newNode = null;
        if (isVertical())
            newNode = new WireNode(start.x, y);
        else
            newNode = new WireNode(x, start.y);

        wire.insertAfter(newNode, start);
        return newNode;
    }

    public boolean isVertical()
    {
        return start.x == end.x;
    }

    public boolean isPointOnSegment(int x, int y)
    {
        if (isVertical())
            return x == start.x && start.y <= y && y <= end.y;
        else
            return y == start.y && start.x <= x && x <= end.x;
    }

    public Rect getBounds()
    {
        if ((isVertical() && start.y > end.y) || (!isVertical() && start.x > end.x))
            return new Rect(end.x - BOUNDS_PADDING, end.y - BOUNDS_PADDING, start.x + BOUNDS_PADDING, start.y + BOUNDS_PADDING);
        else
            return new Rect(start.x - BOUNDS_PADDING, start.y - BOUNDS_PADDING, end.x + BOUNDS_PADDING, end.y + BOUNDS_PADDING);
    }
}
