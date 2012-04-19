package sriracha.frontend.android.designer;

import java.util.ArrayList;

/**
 * Represents a place in the circuit where {@link WireSegment}s meet.
 * Has a position, as well as a list of segments that are coming out of it.
 * There is a bit of redundancy here, since each segment is also aware of the
 * intersection at its endpoints, but this extra information is quite convenient
 * in a lot of situations.
 * <p/>
 * Note that any time a segment or intersection is manipulated in such a way as
 * to break the current segment-intersection relationship, the place in the
 * code where this happens is responsible for ensuring that the appropriate
 * segments and intersections get replaced, removed or added.
 * <p/>
 * For example, in {@link WireManager#removeElement(CircuitElementView element)},
 * if the removed element has segments coming out of it, these segments are then
 * attached to a {@link WireIntersection} that replaces the element. The code in
 * {@code removeElement} is responsible for calling {@link WireSegment#replaceIntersection()}
 * and {@link WireIntersection#addSegment()}, thereby ensuring that the new intersection
 * knows about the segments and vice-versa.
 */
public interface IWireIntersection
{
    public int getX();

    public int getY();

    public void addSegment(WireSegment segment);

    public void replaceSegment(WireSegment oldSegment, WireSegment newSegment);

    public void removeSegment(WireSegment segment);

    public ArrayList<WireSegment> getSegments();

    public boolean duplicateOnMove(WireSegment segment);

    public WireIntersection duplicate(WireSegment segment, WireManager wireManager);

    void setPosition(int snappedX, int snappedY);
}
