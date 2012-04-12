package sriracha.frontend.android.designer;

import android.content.Context;
import android.graphics.Point;
import android.view.ViewGroup;
import sriracha.frontend.*;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.android.model.CircuitElementView;

import java.util.ArrayList;
import java.util.HashMap;

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

    public Context getContext()
    {
        return context;
    }

    public ArrayList<WireSegment> getSegments()
    {
        return segments;
    }

    public ArrayList<IWireIntersection> getIntersections()
    {
        return intersections;
    }

    public void addIntersection(IWireIntersection intersection)
    {
        if (!intersections.contains(intersection))
            intersections.add(intersection);
    }

    public void removeElement(CircuitElementView element)
    {
        for (CircuitElementPortView port : element.getElementPorts())
        {
            if (port.getSegments().size() > 0)
            {
                WireIntersection newIntersection = new WireIntersection(port.getX(), port.getY());
                intersections.add(newIntersection);
                for (WireSegment segment : port.getSegments())
                {
                    segment.replaceIntersection(port, newIntersection);
                    newIntersection.addSegment(segment);
                }
            }
            intersections.remove(port);
        }
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

        addIntersection(to);

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
        /**
         * Step 1:
         * Go through every pairing of intersections that we currently have. If they have the same location, then we add
         * them to a hashmap. This hashmap is index by location and each entry is a list of intersections at this
         * location.
         */
        HashMap<Point, HashMap<NetlistNode, ArrayList<IWireIntersection>>> toConsolidate = new HashMap<Point, HashMap<NetlistNode, ArrayList<IWireIntersection>>>();

        NodeCrawler crawler = new NodeCrawler();
        HashMap<IWireIntersection, NetlistNode> intersectionNodeMap = crawler.getIntersectionNodeMap(this);

        for (int i = 0; i < intersections.size(); i++)
        {
            IWireIntersection intersection = intersections.get(i);
            Point point = new Point(intersection.getX(), intersection.getY());

            if (!toConsolidate.containsKey(point))
                toConsolidate.put(point, new HashMap<NetlistNode, ArrayList<IWireIntersection>>());

            NetlistNode node = intersectionNodeMap.get(intersection);

            if (!toConsolidate.get(point).containsKey(node))
                toConsolidate.get(point).put(node, new ArrayList<IWireIntersection>());

            toConsolidate.get(point).get(node).add(intersection);
        }

        /**
         * Step 2:
         * Iterate through the hashmap.
         * For each list of intersections, there are three cases:
         *
         * - Case 1: Two or more of the intersections are ports.
         *           Once again, we do nothing, since we obviously can't
         *           eliminate any of the elements. Furthermore, we must not delete any zero-length segments that exist,
         *           since that would break the connection between the elements.
         *
         * - Case 2: One of the intersections is a port.
         *           In this case, we consolidate the other intersections, adding their segments to the port intersection.
         *
         * - Case 3: None of the above.
         *           The simplest case, we create a new intersection and add all the segments from the rest of the
         *           intersections to the new one.
         */
        for (HashMap<NetlistNode, ArrayList<IWireIntersection>> nodeIntersectionMap : toConsolidate.values())
        {
            for (ArrayList<IWireIntersection> intersectionList : nodeIntersectionMap.values())
            {
                if (intersectionList.size() <= 1)
                    continue;

                int portCount = 0;
                CircuitElementPortView port = null;

                for (IWireIntersection intersection : intersectionList)
                {
                    // Counting ports for cases 1 and 2.
                    if (intersection instanceof CircuitElementPortView)
                    {
                        portCount++;
                        port = (CircuitElementPortView) intersection;
                    }
                }

                // Case 1. Do nothing.
                if (portCount >= 2)
                    continue;

                IWireIntersection newIntersection = null;
                if (portCount == 1)
                {
                    // Case 2.
                    newIntersection = port;
                }
                else
                {
                    // Case 3. Create a new intersection to replace all the old ones.
                    newIntersection = new WireIntersection(intersectionList.get(0).getX(), intersectionList.get(0).getY());
                    intersections.add(newIntersection);
                }

                for (IWireIntersection intersection : intersectionList)
                {
                    ArrayList<WireSegment> intersectionSegments = new ArrayList<WireSegment>(intersection.getSegments());
                    for (WireSegment segment : intersectionSegments)
                    {
                        if (segment.getLength() == 0)
                        {
                            // If one end is a port, and it's a zero-length
                            // then we have to make sure that the segment gets removed
                            // from the port, since the port won't be removed.
                            segment.getStart().removeSegment(segment);
                            segment.getEnd().removeSegment(segment);
                            removeSegment(segment);
                        }
                        else
                        {
                            // When consolidating intersections, each affected segment must have the
                            // relevant intersection replaced.
                            newIntersection.addSegment(segment);
                            segment.replaceIntersection(intersection, newIntersection);
                        }
                    }
                    if (!(intersection instanceof CircuitElementPortView))
                        intersections.remove(intersection);
                }
            }
        }

        /**
         * Step 3:
         * Merge collinear segments.
         * Here, we remove any intersections that are entirely useless. Note that we don't destroy all collinear segments.
         * Rather, we eliminate intersections that have exactly two segments, where the segments are opposite each other
         * across the intersection.
         * Removed: ---O---
         * Merged: ----======O
         * Not removed: ---O
         *                 |
         * Also not removed: ---O---
         *                      |
         */
        ArrayList<IWireIntersection> toRemove = new ArrayList<IWireIntersection>();
        for (IWireIntersection intersection : intersections)
        {
            if (intersection instanceof WireIntersection && intersection.getSegments().size() == 2)
            {
                WireSegment seg1 = intersection.getSegments().get(0);
                WireSegment seg2 = intersection.getSegments().get(1);
                if (!(seg1.isVertical() ^ seg2.isVertical())) // Both vertical or both horizontal
                {
                    int direction1 = 0, direction2 = 0;
                    if (seg1.isVertical())
                    {
                        direction1 = seg1.otherEnd(intersection).getY() - intersection.getY();
                        direction2 = seg2.otherEnd(intersection).getY() - intersection.getY();
                    }
                    else
                    {
                        direction1 = seg1.otherEnd(intersection).getX() - intersection.getX();
                        direction2 = seg2.otherEnd(intersection).getX() - intersection.getX();
                    }

                    if (Math.signum(direction1) == Math.signum(direction2))
                    {
                        // Segments are going off in the same direction from the intersection.
                        WireSegment longerSegment = seg1.getLength() > seg2.getLength() ? seg1 : seg2;
                        WireSegment shorterSegment = longerSegment == seg1 ? seg2 : seg1;
                        IWireIntersection otherIntersection = shorterSegment.otherEnd(intersection);

                        longerSegment.replaceIntersection(intersection, otherIntersection);
                        otherIntersection.addSegment(longerSegment);
                        intersection.removeSegment(longerSegment);
                    }
                    else
                    {
                        // Segments are going off in opposite directions from the intersection.
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
