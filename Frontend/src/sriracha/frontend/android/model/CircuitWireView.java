package sriracha.frontend.android.model;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class CircuitWireView extends View
{
    private CircuitElementView start;
    private CircuitElementView end;

    public CircuitWireView(Context context, CircuitElementView start, CircuitElementView end)
    {
        super(context);
        this.start = start;
        this.end = end;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        canvas.drawLine(start.getPositionX(), start.getPositionY(), end.getPositionX(), end.getPositionY(), paint);
    }
}
