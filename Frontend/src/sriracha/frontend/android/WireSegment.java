package sriracha.frontend.android;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class WireSegment extends View
{
    private static final int BOUNDS_PADDING = 20;

    private IWireNode start;
    private IWireNode end;

    private final Paint paint = new Paint();

    public WireSegment(Context context, IWireNode start, IWireNode end)
    {
        super(context);

        this.start = start;
        this.end = end;

        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(4);
    }

    public IWireNode getStart() { return start; }
    public IWireNode getEnd() { return end; }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawLine(start.getX(), start.getY(), end.getX(), end.getY(), paint);
        canvas.drawCircle(end.getX(), end.getY(), 4, paint);
    }

    public void setX(int x, WireManager wireManager)
    {
        if (x == start.getX())
            return;

        WireNode duplicateStart = null;
        WireNode duplicateEnd = null;

        if (start.duplicateOnMove(this))
        {
            duplicateStart = start.duplicate(this, wireManager);
            WireSegment segment = (start instanceof WireNode) ? ((WireNode) start).getSegmentTowardX(x) : null;
            if (segment != null)
            {
                segment.replaceNode(start, duplicateStart);
                duplicateStart.addSegment(segment);
            }
        }
        if (end.duplicateOnMove(this))
        {
            duplicateEnd = end.duplicate(this, wireManager);
            WireSegment segment = (end instanceof WireNode) ? ((WireNode) end).getSegmentTowardX(x) : null;
            if (segment != null)
            {
                segment.replaceNode(end, duplicateEnd);
                duplicateEnd.addSegment(segment);
            }
        }

        if (duplicateStart != null)
            duplicateStart.x = x;
        else
            ((WireNode) start).x = x;

        if (duplicateEnd != null)
            duplicateEnd.x = x;
        else
            ((WireNode) end).x = x;
    }

    public void setY(int y, WireManager wireManager)
    {
        if (y == start.getY())
            return;

        WireNode duplicateStart = null;
        WireNode duplicateEnd = null;

        if (start.duplicateOnMove(this))
        {
            duplicateStart = start.duplicate(this, wireManager);
            WireSegment segment = (start instanceof WireNode) ? ((WireNode) start).getSegmentTowardY(y) : null;
            if (segment != null)
            {
                segment.replaceNode(start, duplicateStart);
                duplicateStart.addSegment(segment);
            }
        }
        if (end.duplicateOnMove(this))
        {
            duplicateEnd = end.duplicate(this, wireManager);
            WireSegment segment = (end instanceof WireNode) ? ((WireNode) end).getSegmentTowardY(y) : null;
            if (segment != null)
            {
                segment.replaceNode(end, duplicateEnd);
                duplicateEnd.addSegment(segment);
            }
        }

        if (duplicateStart != null)
            duplicateStart.y = y;
        else
            ((WireNode) start).y = y;

        if (duplicateEnd != null)
            duplicateEnd.y = y;
        else
            ((WireNode) end).y = y;
    }

    public void replaceNode(IWireNode oldNode, IWireNode newNode)
    {
        if (start == oldNode)
            start = newNode;
        else if (end == oldNode)
            end = newNode;
        else
            throw new IllegalArgumentException("Node not found in segment");
    }

    public boolean isVertical()
    {
        return start.getX() == end.getX();
    }

    public boolean isPointOnSegment(int x, int y)
    {
        if (isVertical())
            return x == start.getX() && start.getY() <= y && y <= end.getY();
        else
            return y == start.getY() && start.getX() <= x && x <= end.getX();
    }

    public Rect getBounds()
    {
        if ((isVertical() && start.getY() > end.getY()) || (!isVertical() && start.getX() > end.getX()))
            return new Rect(end.getX() - BOUNDS_PADDING, end.getY() - BOUNDS_PADDING, start.getX() + BOUNDS_PADDING, start.getY() + BOUNDS_PADDING);
        else
            return new Rect(start.getX() - BOUNDS_PADDING, start.getY() - BOUNDS_PADDING, end.getX() + BOUNDS_PADDING, end.getY() + BOUNDS_PADDING);
    }

    public float getLength()
    {
        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString()
    {
        return start.toString() + " - " + end.toString();
    }
}
