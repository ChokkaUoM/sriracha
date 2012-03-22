package sriracha.frontend.android.model;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class CircuitWireView extends View
{
    private CircuitElementPortView start;
    private CircuitElementPortView end;

    public CircuitWireView(Context context, CircuitElementPortView start, CircuitElementPortView end)
    {
        super(context);

        this.start = start;
        this.end = end;
    }

    public CircuitElementPortView getStart() { return start; }
    public CircuitElementPortView getEnd() { return end; }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);

        CircuitElementView startElement = start.getElement();
        CircuitElementView endElement = end.getElement();

        float[] startPosition = start.getTransformedPosition();
        float[] endPosition = end.getTransformedPosition();

        canvas.drawLine(
                startElement.getPositionX() + startElement.getWidth() * startPosition[0],
                startElement.getPositionY() + startElement.getHeight() * startPosition[1],
                endElement.getPositionX() + endElement.getWidth() * endPosition[0],
                endElement.getPositionY() + endElement.getHeight() * endPosition[1],
                paint);
    }
}
