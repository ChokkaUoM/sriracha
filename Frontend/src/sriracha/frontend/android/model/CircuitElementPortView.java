package sriracha.frontend.android.model;

import android.graphics.*;
import sriracha.frontend.android.*;

import java.util.*;

public class CircuitElementPortView implements IWireNode
{
    private CircuitElementView element;

    private float positionX;
    private float positionY;

    public CircuitElementPortView(CircuitElementView element, float positionX, float positionY)
    {
        this.element = element;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public CircuitElementView getElement() { return element; }

    public float getUntransformedPositionX() { return positionX; }
    public float getUntransformedPositionY() { return positionY; }

    public float[] getTransformedPosition()
    {
        Matrix matrix = new Matrix();
        matrix.setRotate(element.getOrientation());
        float[] transformed = new float[2];
        matrix.mapPoints(transformed, new float[]{positionX, positionY});
        return transformed;
    }

    @Override
    public int getX()
    {
        return (int) (element.getX() + element.getWidth() * getTransformedPosition()[0]);
    }

    @Override
    public int getY()
    {
        return (int) (element.getY() + element.getHeight() * getTransformedPosition()[1]);
    }

    @Override
    public void addSegment(WireSegment segment)
    {
    }

    @Override
    public void replaceSegment(WireSegment oldSegment, WireSegment newSegment)
    {
    }
    @Override
    public ArrayList<WireSegment> getSegments()
    {
        return new ArrayList<WireSegment>();
    }

    @Override
    public boolean duplicateOnMove(WireSegment segment)
    {
        return true;
    }
    @Override
    public WireNode duplicate(WireSegment segment, WireManager wireManager)
    {
        WireNode newNode = new WireNode(getX(), getY());
        wireManager.addNode(newNode);

        // Connect the segment that's being moved to the new node.
        segment.replaceNode(this, newNode);
        newNode.addSegment(segment);

        // Connect the old node and the new node with a brand new segment
        WireSegment newSegment = new WireSegment(segment.getContext(), this, newNode);
        wireManager.addSegment(newSegment);

        newNode.addSegment(newSegment);
        this.replaceSegment(segment, newSegment);

        return newNode;
    }
}
