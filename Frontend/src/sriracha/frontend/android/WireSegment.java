package sriracha.frontend.android;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class WireSegment extends View
{
    private static final int BOUNDS_PADDING = 20;

    private IWireIntersection start;
    private IWireIntersection end;

    public WireSegment(Context context, IWireIntersection start, IWireIntersection end)
    {
        super(context);

        this.start = start;
        this.end = end;
    }

    public IWireIntersection getStart() { return start; }
    public IWireIntersection getEnd() { return end; }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(isSelected() ? Color.rgb(0xCC, 0xCC, 0) : Color.GRAY);
        paint.setStrokeWidth(isSelected() ? 6 : 4);

        canvas.drawLine(start.getX(), start.getY(), end.getX(), end.getY(), paint);
        canvas.drawCircle(end.getX(), end.getY(), 4, paint);
    }

    public void setX(int x, WireManager wireManager)
    {
        if (x == start.getX())
            return;

        if (start.duplicateOnMove(this))
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
        if (end.duplicateOnMove(this))
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

        ((WireIntersection) start).x = x;
        ((WireIntersection) end).x = x;
    }

    public void setY(int y, WireManager wireManager)
    {
        if (y == start.getY())
            return;

        if (start.duplicateOnMove(this))
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
        if (end.duplicateOnMove(this))
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

        ((WireIntersection) start).y = y;
        ((WireIntersection) end).y = y;
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
