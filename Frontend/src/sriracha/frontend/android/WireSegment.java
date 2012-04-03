package sriracha.frontend.android;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class WireSegment extends View
{
    private static final int BOUNDS_PADDING = 20;

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    private IWireIntersection start;
    private IWireIntersection end;

    private WireManager wireManager;

    int r, g, b;
    static int rs, gs, bs;
    public WireSegment(Context context, WireManager wireManager, IWireIntersection start, IWireIntersection end)
    {
        super(context);

        this.wireManager = wireManager;

        this.start = start;
        this.end = end;

        r = rs++;
        if (r > 1)
        {
            rs = 0;
            gs++;
        }
        g = gs;
        if (gs > 1)
        {
            gs = 0;
            bs++;
        }
        b = bs;
    }

    public IWireIntersection getStart() { return start; }
    public IWireIntersection getEnd() { return end; }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        //paint.setColor(isSelected() ? Color.rgb(0xCC, 0xCC, 0) : Color.GRAY);
        paint.setColor(Color.argb(0x7F, 0x7F * r, 0x7F * g, 0x7F * b));
        paint.setStrokeWidth(isSelected() ? 6 : 4);

        //canvas.drawLine(start.getX(), start.getY(), end.getX(), end.getY(), paint);
        float randSX = (float) Math.random() * 20 - 10, randSY = (float) Math.random() * 20 - 10;
        float randEX = (float) Math.random() * 20 - 10, randEY = (float) Math.random() * 20 - 10;
        canvas.drawLine(start.getX() + randSX, start.getY() + randSY, end.getX() + randEX, end.getY() + randEY, paint);
        canvas.drawCircle(start.getX() + randSX, start.getY() + randSY, 4, paint);
        canvas.drawCircle(end.getX() + randEX, end.getY() + randEY, 4, paint);
    }

    public boolean moveX(int x) { return moveX(x, null); }
    public boolean moveY(int y) { return moveY(y, null); }

    public boolean moveX(int x, IWireIntersection onlyThisNode)
    {
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
        }
        else
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
}
