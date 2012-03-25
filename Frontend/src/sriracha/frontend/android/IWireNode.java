package sriracha.frontend.android;

public interface IWireNode
{
    public int getX();
    public int getY();

    public void addSegment(WireSegment segment);
    public void replaceSegment(WireSegment oldSegment, WireSegment newSegment);

    public boolean duplicateOnMove(WireSegment segment);
    public WireNode duplicate(WireSegment segment, WireManager wireManager);
}
