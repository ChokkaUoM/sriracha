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
    public void setStart(IWireNode node) { start = node; }
    public void setEnd(IWireNode node) { end = node; }

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

        IWireNode startNode = start;
        IWireNode endNode = end;

        if (start.duplicateOnMove(this))
            startNode = start.duplicate(this, wireManager);
        if (end.duplicateOnMove(this))
            endNode = end.duplicate(this, wireManager);

        ((WireNode) startNode).x = x;
        ((WireNode) endNode).x = x;
    }

    public void setY(int y, WireManager wireManager)
    {
        if (y == start.getY())
            return;

        IWireNode startNode = start;
        IWireNode endNode = end;

        if (start.duplicateOnMove(this))
            startNode = start.duplicate(this, wireManager);
        if (end.duplicateOnMove(this))
            endNode = end.duplicate(this, wireManager);

        ((WireNode) startNode).y = y;
        ((WireNode) endNode).y = y;
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

    @Override
    public String toString()
    {
        return getStart().toString() + " - " + getEnd().toString();
    }
}
