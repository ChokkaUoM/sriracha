package sriracha.frontend.android.designer;

import android.content.Context;
import android.graphics.Point;
import android.view.ViewGroup;
import sriracha.frontend.NetlistNode;
import sriracha.frontend.NodeCrawler;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.android.model.CircuitElementView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages all {@link WireSegment}s and {@link IWireIntersection}s in the circuit
 * designer.
 * This class houses the all-important {@link WireManager#consolidateIntersections()}
 * method, which is responsible for all that is good and evil in the world.
 */
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
        for (CircuitElementPortView port : element.getPortViews())
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
            // If we're adding an intermediate section from a port, we want to extend
            // the wire in the same direction that the port's element goes.
            // Unless, of course, this will cause the wire to go through the element.
            boolean extendVertically;
            if (from instanceof CircuitElementPortView)
            {
                CircuitElementPortView port = (CircuitElementPortView) from;
                float orientation = port.getElement().getOrientation();
                extendVertically = orientation % 180 == 0;

                boolean passingThroughElement = false;
                if (extendVertically)
                {
                    passingThroughElement |= orientation == 0 && port.getTransformedPosition()[1] < 0 && to.getY() > from.getY();
                    passingThroughElement |= orientation == 180 && port.getTransformedPosition()[1] > 0 && to.getY() < from.getY();
                } else
                {
                    passingThroughElement |= orientation == 90 && port.getTransformedPosition()[0] < 0 && to.getX() > from.getX();
                    passingThroughElement |= orientation == 270 && port.getTransformedPosition()[0] > 0 && to.getX() < from.getX();
                }
                if (passingThroughElement)
                    extendVertically = !extendVertically;
            } else
            {
                WireIntersection intersection = (WireIntersection) from;
                if (intersection.getSegments().size() != 1)
                    throw new RuntimeException("Wut?");

                WireSegment segment = intersection.getSegments().get(0);
                extendVertically = segment.isVertical();
            }

            WireIntersection intermediate;

            if (extendVertically)
                intermediate = new WireIntersection(from.getX(), to.getY());
            else
                intermediate = new WireIntersection(to.getX(), from.getY());

            connectNewIntersection(from, intermediate);
            from = intermediate;
        }

        addIntersection(to);

        WireSegment segment = new WireSegment(context, this, from, to);
        from.addSegment(segment);
        to.addSegment(segment);
        addSegment(segment);
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
         * Use node crawler to assign intersections to nodes
         * Go through every pairing of intersections that we currently have. If they have the same location, then we add
         * them to a hashmap. This hashmap is indexed first by location and each entry is a list of intersections at this
         * location.
         */
        HashMap<Point, HashMap<NetlistNode, ArrayList<IWireIntersection>>> toConsolidate = new HashMap<Point, HashMap<NetlistNode, ArrayList<IWireIntersection>>>();

        NodeCrawler crawler = new NodeCrawler(this);
        crawler.computeMappings();


        for (int i = 0; i < intersections.size(); i++)
        {
            IWireIntersection intersection = intersections.get(i);
            Point point = new Point(intersection.getX(), intersection.getY());

            if (!toConsolidate.containsKey(point))
                toConsolidate.put(point, new HashMap<NetlistNode, ArrayList<IWireIntersection>>());

            NetlistNode node = crawler.nodeFromIntersection(intersection);

            if (!toConsolidate.get(point).containsKey(node))
                toConsolidate.get(point).put(node, new ArrayList<IWireIntersection>());

            toConsolidate.get(point).get(node).add(intersection);
        }

        /**
         * Step 2:
         * Iterate through the hashmap.
         * For each list of intersections, there are three cases:
         *
         *
         * - Case 1: One or more of the intersections is a port.
         *           In this case, we consolidate the other intersections, adding their segments to the port intersection.
         *
         * - Case 2: None of the above.
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
                ArrayList<CircuitElementPortView> ports = new ArrayList<CircuitElementPortView>();
                for (IWireIntersection intersection : intersectionList)
                {
                    // Counting ports for case 1.
                    if (intersection instanceof CircuitElementPortView && !ports.contains(intersection))
                    {
                        portCount++;
                        port = (CircuitElementPortView) intersection;
                        ports.add(port);
                    }
                }

                IWireIntersection newIntersection = null;

                if (portCount >= 1)
                {
                    // Case 1.
                    newIntersection = ports.get(0);

                    if (portCount == 2)
                    {
                        //create extra segment linking them in case they
                        //are currently linked by 0 length segments, through another stacked intersection 
                        //if we do not do this the element will detach since 0 length segments are deleted
                        //if they are not between 2 elements
                        addNewSegment(new WireSegment(getContext(), this, ports.get(0), ports.get(1)));
                    }

                } else
                {
                    // Case 2. Create a new intersection to replace all the old ones.
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
                            if (!(segment.getEnd() instanceof CircuitElementPortView && segment.getStart() instanceof CircuitElementPortView && segment.getEnd() != segment.getStart()))
                            {
                                // If it's zero-length
                                // then we have to make sure that the segment gets removed
                                // from the intersection in case its a port, since the port won't be removed.
                                segment.getStart().removeSegment(segment);
                                segment.getEnd().removeSegment(segment);
                                removeSegment(segment);
                            }
                        } else
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
         * First make a hashmap mapping nodes to lists of segments
         * then iterate over every pair of segments in the node list and
         * merge the ones that overlap
         */
        HashMap<NetlistNode, ArrayList<WireSegment>> nodeSegmentMap;

        nodeSegmentMap = new HashMap<NetlistNode, ArrayList<WireSegment>>();
        for (WireSegment s : segments)
        {

            NetlistNode node = crawler.nodeFromSegment(s);
            if (!nodeSegmentMap.containsKey(node))
            {
                nodeSegmentMap.put(node, new ArrayList<WireSegment>());
            }

            nodeSegmentMap.get(node).add(s);
        }

        //for each node consolidate segments
        for (ArrayList<WireSegment> segList : nodeSegmentMap.values())
        {
            boolean extraPass;
            int passCount = 0;
            do
            {
                passCount++;
                extraPass = false;
                //iterate over all pairs of segments backwards so we can remove as we go
                for (int i = segList.size() - 1; i > 0; i = Math.min(i - 1, segList.size() - 1))
                {
                    WireSegment segA = segList.get(i);
                    for (int j = i - 1; j >= 0; j--)
                    {
                        WireSegment segB = segList.get(j);
                        //always try and remove segB if you can, if you must remove segA, then
                        // break from inner loop

                        if (segA.isColinearAndTouching(segB))
                        {
                            //segments overlap
                            if (segA.getStart() == segB.getStart() && segA.getEnd() == segB.getEnd() ||
                                    segA.getStart() == segB.getEnd() && segA.getEnd() == segB.getStart())
                            {
                                //perfectly overlapping segments delete one
                                // o-=-=-=-=o
                                detachSegment(segB);
                                removeSegment(segB);
                                segList.remove(j);

                            } else if (segA.getStart() == segB.getStart() || segA.getEnd() == segB.getEnd() ||
                                    segA.getStart() == segB.getEnd() || segA.getEnd() == segB.getStart())
                            {
                                //segments share an intersection
                                IWireIntersection intersection;
                                if (segA.getStart() == segB.getStart() || segA.getStart() == segB.getEnd())
                                    intersection = segA.getStart();
                                else
                                    intersection = segA.getEnd();
                                //check how they connect with direction variable
                                int direction1 = 0, direction2 = 0;
                                if (segA.isVertical())
                                {
                                    direction1 = segA.otherEnd(intersection).getY() - intersection.getY();
                                    direction2 = segB.otherEnd(intersection).getY() - intersection.getY();
                                } else
                                {
                                    direction1 = segA.otherEnd(intersection).getX() - intersection.getX();
                                    direction2 = segB.otherEnd(intersection).getX() - intersection.getX();
                                }
                                //if same direction, correctly ignores case with 0 length
                                if (Math.signum(direction1) == Math.signum(direction2))
                                {
                                    // Segments are going off in the same direction from the intersection.
                                    // o------o-=-=-=-=o
                                    WireSegment longerSegment = segA.getLength() > segB.getLength() ? segA : segB;
                                    WireSegment shorterSegment = longerSegment == segA ? segB : segA;
                                    IWireIntersection midIntersection = shorterSegment.otherEnd(intersection);
                                    //partial overlap reconnect longer seg to midpoint
                                    longerSegment.replaceIntersection(intersection, midIntersection);
                                    midIntersection.addSegment(longerSegment);
                                    intersection.removeSegment(longerSegment);

                                    extraPass = true;
                                } else if (direction1 != 0 && direction2 != 0)
                                {
                                    // Segments are going off in opposite directions from the intersection. and none of
                                    //them is 0 length
                                    // o------o=======o
                                    if (intersection instanceof WireIntersection)
                                    {
                                        if (intersection.getSegments().size() == 2)
                                        {
                                            //single intersection between two opposing wires nothing else attached
                                            if (segB.getStart() == intersection)
                                            {
                                                segB.getEnd().replaceSegment(segB, segA);
                                                segA.replaceIntersection(intersection, segB.getEnd());
                                            } else
                                            {
                                                segB.getStart().replaceSegment(segB, segA);
                                                segA.replaceIntersection(intersection, segB.getStart());
                                            }

                                            detachIntersection((WireIntersection) intersection);
                                            intersections.remove(intersection);
                                            detachSegment(segB);
                                            removeSegment(segB);
                                            segList.remove(j);
                                        } else
                                        {
                                            //intersection between 2 opposing wires with another wire attached
                                            //do nothing, should already be fine ... i think
                                        }

                                    } else
                                    {
                                        //common intersection is a port, wires are in opposing directions
                                        //and are not of 0 length nothing to be done ...
                                    }


                                }


                            } else if (segA.covers(segB) || segB.covers(segA))
                            {
                                //one segment completely covers the other
                                // o-----o-=-=-=o------o
                                WireSegment longSeg = segA.getLength() > segB.getLength() ? segA : segB;
                                WireSegment shortSeg = longSeg == segA ? segB : segA;

                                IWireIntersection midFar = shortSeg.getMax();
                                IWireIntersection midClose = shortSeg.getMin();
                                //move shorter segment down
                                shortSeg.replaceIntersection(midFar, longSeg.getMax());
                                shortSeg.replaceIntersection(midClose, midFar);
                                midClose.removeSegment(shortSeg);
                                longSeg.getMax().addSegment(shortSeg);
                                //attach long segment to top
                                longSeg.replaceIntersection(longSeg.getMax(), midClose);
                                longSeg.getMax().removeSegment(longSeg);
                                midClose.addSegment(longSeg);
                                //create new segment for middle
                                WireSegment segment = new WireSegment(getContext(), this, midClose, midFar);
                                midClose.addSegment(segment);
                                midFar.addSegment(segment);
                                addSegment(segment);
                                segList.add(segment);//wont be checked in this pass since added at end
                                extraPass = true;
                            } else
                            {
                                // general partial overlap
                                // o----o=-=-o======o

                                //same concept as above with new seg for middle section and second pass takes care of rest
                                WireSegment segMin = segA.getMinPos() < segB.getMinPos() ? segA : segB;
                                WireSegment segMax = segMin == segA ? segB : segA;


                                IWireIntersection midFar = segMin.getMax();
                                IWireIntersection midClose = segMax.getMin();
                                //connect min to min point.
                                segMin.replaceIntersection(midFar, midClose);
                                midFar.removeSegment(segMin);
                                midClose.addSegment(segMin);
                                //connect max to max mid point
                                segMax.replaceIntersection(midClose, midFar);
                                midClose.removeSegment(segMax);
                                midFar.addSegment(segMax);
                                //create new segment for middle
                                WireSegment segment = new WireSegment(getContext(), this, midClose, midFar);
                                midClose.addSegment(segment);
                                midFar.addSegment(segment);
                                addSegment(segment);
                                segList.add(segment); // wont be checked in this pass since added at end
                                extraPass = true;
                            }
                        }
                    }
                }
                //while here
            } while (extraPass && passCount < 5);

        }


    }

    public void addSegment(WireSegment segment)
    {
        segments.add(segment);
        canvasView.addView(segment);
    }

    public void addNewSegment(WireSegment segment)
    {
        segment.getStart().addSegment(segment);
        segment.getEnd().addSegment(segment);
        segments.add(segment);
        canvasView.addView(segment);
    }

    private void detachIntersection(WireIntersection intersection)
    {
        for (WireSegment seg : intersection.getSegments())
        {
            try
            {
                seg.replaceIntersection(intersection, null);
            } catch (IllegalArgumentException e)
            {
                //do nothing this intersection had already been disconnected from the 
                //segment previously
            }

        }
        intersection.detachSegments();
    }

    private void detachSegment(WireSegment segment)
    {
        if (segment.getStart() != null)
        {
            segment.getStart().removeSegment(segment);
            segment.replaceIntersection(segment.getStart(), null);
        }

        if (segment.getEnd() != null)
        {
            segment.getEnd().removeSegment(segment);
            segment.replaceIntersection(segment.getEnd(), null);
        }


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
