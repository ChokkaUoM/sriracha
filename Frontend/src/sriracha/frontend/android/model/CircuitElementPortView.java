package sriracha.frontend.android.model;

import android.graphics.*;

public class CircuitElementPortView
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
}
