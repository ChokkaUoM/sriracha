package sriracha.frontend.android.model;

import android.graphics.Matrix;
import sriracha.frontend.android.designer.IWireIntersection;
import sriracha.frontend.android.designer.WireIntersection;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.designer.WireSegment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a circuit element's port on the circuit designer canvas.
 * Position information is defined with respect to the center of the CircuitElementView,
 * and must be transformed based on the element's size and orientation to get the absolute
 * position of the port.
 */
public class CircuitElementPortView implements IWireIntersection, Serializable
{
    private UUID uuid;
    private UUID elementUUID;
    private transient CircuitElementView element;

    private transient ArrayList<WireSegment> segments = new ArrayList<WireSegment>(4);

    private float positionX;
    private float positionY;

    public CircuitElementPortView(CircuitElementView element, float positionX, float positionY)
    {
        uuid = UUID.randomUUID();
        this.element = element;
        elementUUID = element.getUUID();
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public CircuitElementView getElement()
    {
        return element;
    }

    public void setElement(CircuitElementView element)
    {
        this.element = element;
        elementUUID = element.getUUID();
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public UUID getElementUUID()
    {
        return elementUUID;
    }

    public float getUntransformedPositionX()
    {
        return positionX;
    }

    public float getUntransformedPositionY()
    {
        return positionY;
    }

    public float[] getTransformedPosition()
    {
        return transformPosition(element.getOrientation());
    }

    public float[] transformPosition(float degrees)
    {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        float[] transformed = new float[2];
        matrix.mapPoints(transformed, new float[]{positionX, positionY});
        return transformed;
    }

    public float getRelativeX()
    {
        return element.getWidth() / 2 + element.getWidth() * getTransformedPosition()[0];
    }

    public float getRelativeY()
    {
        return element.getHeight() / 2 + element.getHeight() * getTransformedPosition()[1];
    }

    @Override
    public int getX()
    {
        return (int) (element.getPositionX() + element.getWidth() / 2 + element.getWidth() * getTransformedPosition()[0]);
    }

    @Override
    public int getY()
    {
        return (int) (element.getPositionY() + element.getHeight() / 2 + element.getHeight() * getTransformedPosition()[1]);
    }

    @Override
    public void addSegment(WireSegment segment)
    {
        if (segments == null)
            segments = new ArrayList<WireSegment>(4);

        if (!segments.contains(segment)) // TODO: Needed?
            segments.add(segment);
    }

    @Override
    public void replaceSegment(WireSegment oldSegment, WireSegment newSegment)
    {
        if (!segments.remove(oldSegment))
            throw new IllegalArgumentException("Segment not found in collection");
        segments.add(newSegment);
    }

    @Override
    public void removeSegment(WireSegment segment)
    {
        segments.remove(segment);
    }

    @Override
    public ArrayList<WireSegment> getSegments()
    {
        return segments;
    }

    @Override
    public boolean duplicateOnMove(WireSegment segment)
    {
        return true;
    }

    @Override
    public WireIntersection duplicate(WireSegment segment, WireManager wireManager)
    {
        WireIntersection newIntersection = new WireIntersection(getX(), getY());
        wireManager.addIntersection(newIntersection);

        // Connect the segment that's being moved to the new node.
        segment.replaceIntersection(this, newIntersection);
        newIntersection.addSegment(segment);

        // Connect the old node and the new node with a brand new segment
        WireSegment newSegment = new WireSegment(segment.getContext(), wireManager, this, newIntersection);
        wireManager.addSegment(newSegment);

        newIntersection.addSegment(newSegment);
        this.replaceSegment(segment, newSegment);

        return newIntersection;
    }

    @Override
    public void setPosition(int snappedX, int snappedY)
    {
        positionX = snappedX;
        positionY = snappedY;
    }

    @Override
    public String toString()
    {
        return String.format("Port(%d, %d)", getX(), getY());
    }
}
