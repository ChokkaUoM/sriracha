package sriracha.frontend.android;

import android.content.*;
import android.graphics.*;
import android.view.*;
import sriracha.frontend.android.model.*;

import java.util.*;

public class WireManager
{
    private ArrayList<WireSegment> segments = new ArrayList<WireSegment>();
    private ArrayList<IWireIntersection> intersections = new ArrayList<IWireIntersection>();

    private ViewGroup canvasView;
    private Context context;

    public WireManager(ViewGroup canvasView)
    {
        this.canvasView = canvasView;
        context = canvasView.getContext();
    }

    public void addIntersection(IWireIntersection intersection)
    {
        if (!intersections.contains(intersection))
            intersections.add(intersection);
    }

    public void connectNewIntersection(IWireIntersection from, IWireIntersection to)
    {
        if (!intersections.contains(from))
            throw new IllegalArgumentException("from");

        // Add intermediate intersection to keep everything orthogonal
        if (from.getX() != to.getX() && from.getY() != to.getY())
        {
            WireIntersection intermediate = new WireIntersection(from.getX(), to.getY());
            connectNewIntersection(from, intermediate);
            from = intermediate;
        }

        intersections.add(to);

        WireSegment segment = new WireSegment(context, this, from, to);
        from.addSegment(segment);
        to.addSegment(segment);
        addSegment(segment);

        consolidateIntersections();
    }

    public WireIntersection splitSegment(WireSegment segment, int x, int y)
    {
        if (!segment.isPointOnSegment(x, y))
            throw new IllegalArgumentException("Point not on segment");

        // Create two new segments by splitting the original one up.
        WireIntersection intersection = new WireIntersection(x, y);
        WireSegment firstHalf = new WireSegment(context, this, segment.getStart(), intersection);
        WireSegment secondHalf = new WireSegment(context, this, intersection, segment.getEnd());

        addSegment(firstHalf);
        addSegment(secondHalf);

        // Intersections know what segments they're attached to, so we update that information.
        segment.getStart().replaceSegment(segment, firstHalf);
        segment.getEnd().replaceSegment(segment, secondHalf);

        // The new intersection needs to know about the two new segments.
        intersection.addSegment(firstHalf);
        intersection.addSegment(secondHalf);

        // Get rid of the stale, old segment. We hates it.
        removeSegment(segment);

        return intersection;
    }

    /**
     * Takes WireIntersections at the same location, and merges them.
     * However, if the intersections have two segments pointing in the same
     * direction which are not the same segment, then they cannot be consolidated.
     * Segments that have one endpoint as a Port merge in such a way so that only
     * the port remains.
     * Segments that have both endpoints as Ports do not merge.
     */
    public void consolidateIntersections()
    {
        HashMap<Point, ArrayList<IWireIntersection>> toConsolidate = new HashMap<Point, ArrayList<IWireIntersection>>();

        for (int i = 0; i < intersections.size(); i++)
        {
            IWireIntersection intersection1 = intersections.get(i);
            for (int j = i + 1; j < intersections.size(); j++)
            {
                IWireIntersection intersection2 = intersections.get(j);

                // If two intersections have the same coordinates, we store them in a hashtable.
                // The hashtable is indexed by point, and each entry contains a list of
                // intersections that all have the same coordinates.
                if (intersection1.getX() == intersection2.getX() && intersection1.getY() == intersection2.getY())
                {
                    Point point = new Point(intersection1.getX(), intersection1.getY());
                    if (!toConsolidate.containsKey(point))
                        toConsolidate.put(point, new ArrayList<IWireIntersection>());

                    ArrayList<IWireIntersection> intersectionList = toConsolidate.get(point);

                    if (!intersectionList.contains(intersection1))
                        intersectionList.add(intersection1);
                    if (!intersectionList.contains(intersection2))
                        intersectionList.add(intersection2);
                }
            }
        }

        for (ArrayList<IWireIntersection> intersectionList : toConsolidate.values())
        {
            int portCount = 0;
            for (IWireIntersection intersection : intersectionList)
            {
                if (intersection instanceof CircuitElementPortView)
                    portCount++;
            }
            
            if (portCount >= 2)
                continue;

            if (portCount == 1)
            {

            }

            // When consolidating intersections, each affected segment must have the
            // relevant intersection replaced.

            // Create a new intersection to replace all the old ones.
            WireIntersection newIntersection = new WireIntersection(intersectionList.get(0).getX(), intersectionList.get(0).getY());
            intersections.add(newIntersection);

            for (IWireIntersection intersection : intersectionList)
            {
                for (WireSegment segment : intersection.getSegments())
                {
                    if (segment.getLength() == 0)
                    {
                        removeSegment(segment);
                    }
                    else
                    {
                        newIntersection.addSegment(segment);
                        segment.replaceIntersection(intersection, newIntersection);
                    }
                }
                intersections.remove(intersection);
            }
        }

        // Lastly merge collinear segment
        ArrayList<IWireIntersection> toRemove = new ArrayList<IWireIntersection>();
        for (IWireIntersection intersection : intersections)
        {
            if (intersection instanceof WireIntersection && intersection.getSegments().size() == 2)
            {
                WireSegment seg1 = intersection.getSegments().get(0);
                WireSegment seg2 = intersection.getSegments().get(1);
                if (!(seg1.isVertical() ^ seg2.isVertical())) // Both vertical or both horizontal
                {
                    if (seg2.getStart() == intersection)
                    {
                        seg2.getEnd().replaceSegment(seg2, seg1);
                        seg1.replaceIntersection(intersection, seg2.getEnd());
                    }
                    else
                    {
                        seg2.getStart().replaceSegment(seg2, seg1);
                        seg1.replaceIntersection(intersection, seg2.getStart());
                    }

                    toRemove.add(intersection);
                    removeSegment(seg2);
                }
            }
        }
        intersections.removeAll(toRemove);
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

    public WireSegment getSegmentByPosition(float x, float y)
    {
        for (WireSegment segment : segments)
        {
            if (segment.getBounds().contains((int) x, (int) y))
                return segment;
        }
        return null;
    }

    public void selectSegment(WireSegment toSelect)
    {
        for (WireSegment segment : segments)
        {
            segment.setSelected(segment == toSelect);
        }
    }

    public void deleteSelectedSegment()
    {
        for (WireSegment segment : segments)
        {
            if (segment.isSelected())
            {
                segment.getStart().removeSegment(segment);
                segment.getEnd().removeSegment(segment);
                removeSegment(segment);
                consolidateIntersections();
                break;
            }
        }
    }

    public void invalidateAll()
    {
        for (WireSegment segment : segments)
            segment.invalidate();
    }
}
