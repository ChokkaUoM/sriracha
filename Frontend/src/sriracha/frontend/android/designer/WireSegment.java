package sriracha.frontend.android.designer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.io.*;

public class WireSegment extends View
{
    private static final int BOUNDS_PADDING = 20;

    private IWireIntersection start;
    private IWireIntersection end;

    private WireManager wireManager;

    public WireSegment(Context context, WireManager wireManager, IWireIntersection start, IWireIntersection end)
    {
        super(context);

        this.wireManager = wireManager;

        this.start = start;
        this.end = end;
    }

    public IWireIntersection getStart()
    {
        return start;
    }

    public IWireIntersection getEnd()
    {
        return end;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(isSelected() ? Color.rgb(0xCC, 0xCC, 0) : Color.GRAY);
        paint.setStrokeWidth(isSelected() ? 6 : 4);

        canvas.drawLine(start.getX(), start.getY(), end.getX(), end.getY(), paint);
        canvas.drawCircle(start.getX(), start.getY(), 4, paint);
        canvas.drawCircle(end.getX(), end.getY(), 4, paint);
    }

    public boolean moveX(int x)
    {
        return moveX(x, null);
    }

    public boolean moveY(int y)
    {
        return moveY(y, null);
    }

    public boolean moveX(int x, IWireIntersection onlyThisNode)
    {
        if (getLength() == 0)
            return true;

        int currentX = onlyThisNode == null ? start.getX() : onlyThisNode.getX();
        if (x == currentX)
            return false;

        boolean doStart = onlyThisNode == null || onlyThisNode == start;
        boolean doEnd = onlyThisNode == null || onlyThisNode == end;

        if (doStart && start.duplicateOnMove(this))
        {
            IWireIntersection oldStart = start;
            start.duplicate(this, wireManager);
            WireSegment segment = (oldStart instanceof WireIntersection) ? ((WireIntersection) oldStart).getSegmentTowardX(x) : null;
            if (segment != null)
            {
                oldStart.removeSegment(segment);
                start.addSegment(segment);
                segment.replaceIntersection(oldStart, start);
            }
        }
        if (doEnd && end.duplicateOnMove(this))
        {
            IWireIntersection oldEnd = end;
            end.duplicate(this, wireManager);
            WireSegment segment = (oldEnd instanceof WireIntersection) ? ((WireIntersection) oldEnd).getSegmentTowardX(x) : null;
            if (segment != null)
            {
                oldEnd.removeSegment(segment);
                end.addSegment(segment);
                segment.replaceIntersection(oldEnd, end);
            }
        }

        if (doStart)
            ((WireIntersection) start).x = x;
        if (doEnd)
            ((WireIntersection) end).x = x;

        return true;
    }

    public boolean moveY(int y, IWireIntersection onlyThisNode)
    {
        if (getLength() == 0)
            return true;

        int currentY = onlyThisNode == null ? start.getY() : onlyThisNode.getY();
        if (y == currentY)
            return false;

        boolean doStart = onlyThisNode == null || onlyThisNode == start;
        boolean doEnd = onlyThisNode == null || onlyThisNode == end;

        if (doStart && start.duplicateOnMove(this))
        {
            IWireIntersection oldStart = start;
            start.duplicate(this, wireManager);
            WireSegment segment = (oldStart instanceof WireIntersection) ? ((WireIntersection) oldStart).getSegmentTowardY(y) : null;
            if (segment != null)
            {
                oldStart.removeSegment(segment);
                start.addSegment(segment);
                segment.replaceIntersection(oldStart, start);
            }
        }
        if (doEnd && end.duplicateOnMove(this))
        {
            IWireIntersection oldEnd = end;
            end.duplicate(this, wireManager);
            WireSegment segment = (oldEnd instanceof WireIntersection) ? ((WireIntersection) oldEnd).getSegmentTowardY(y) : null;
            if (segment != null)
            {
                oldEnd.removeSegment(segment);
                end.addSegment(segment);
                segment.replaceIntersection(oldEnd, end);
            }
        }

        if (doStart)
            ((WireIntersection) start).y = y;
        if (doEnd)
            ((WireIntersection) end).y = y;

        return true;
    }

    public void replaceIntersection(IWireIntersection oldIntersection, IWireIntersection newIntersection)
    {
        if (start == oldIntersection)
            start = newIntersection;
        else if (end == oldIntersection)
            end = newIntersection;
        else
            throw new IllegalArgumentException("Intersection not found in segment");
    }

    public boolean isVertical()
    {
        return start.getX() == end.getX();
    }

    public boolean isPointOnSegment(int x, int y)
    {
        if (isVertical())
        {
            if (start.getY() <= end.getY())
                return x == start.getX() && start.getY() <= y && y <= end.getY();
            else
                return x == start.getX() && end.getY() <= y && y <= start.getY();
        } else
        {
            if (start.getX() <= end.getX())
                return y == start.getY() && start.getX() <= x && x <= end.getX();
            else
                return y == start.getY() && end.getX() <= x && x <= start.getX();
        }
    }

    public IWireIntersection otherEnd(IWireIntersection thisEnd)
    {
        return start != thisEnd ? start : end;
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

    public void serialize(ObjectOutputStream out) throws IOException
    {
        out.writeObject(start);
        out.writeObject(end);
    }

    public void deserialize(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        start = (IWireIntersection) in.readObject();
        end = (IWireIntersection) in.readObject();
        start.addSegment(this);
        end.addSegment(this);
    }
}
