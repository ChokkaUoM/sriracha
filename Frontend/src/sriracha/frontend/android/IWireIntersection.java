package sriracha.frontend.android;

import java.util.*;

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
}
