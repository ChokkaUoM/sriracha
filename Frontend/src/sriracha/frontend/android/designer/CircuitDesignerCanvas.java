package sriracha.frontend.android.designer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import sriracha.frontend.R;
import sriracha.frontend.android.results.IElementSelector;

/**
 * This is the layout for the circuit designer itself, the one that holds all the
 * circuit element, the wire segment, and the intersections.
 */
public class CircuitDesignerCanvas extends RelativeLayout
{
    private boolean showGrid = true;

    private IElementSelector elementSelector;

    public CircuitDesignerCanvas(Context context)
    {
        super(context);
    }

    public CircuitDesignerCanvas(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CircuitDesignerCanvas(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public boolean isShowGrid()
    {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid)
    {
        this.showGrid = showGrid;
        invalidate();
    }

    public void setElementSelector(IElementSelector elementSelector)
    {
        this.elementSelector = elementSelector;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.CanvasLines));
        paint.setStrokeWidth(1);

        if (showGrid)
        {
            for (int i = 0; i < getWidth(); i += CircuitDesigner.GRID_SIZE)
                canvas.drawLine(i, 0, i, getHeight(), paint);
            for (int i = 0; i < getHeight(); i += CircuitDesigner.GRID_SIZE)
                canvas.drawLine(0, i, getWidth(), i, paint);
        }


        for (int i = 0; i < getChildCount(); i++)
        {
            View child = getChildAt(i);
            if (child instanceof WireSegment)
            {
                child.bringToFront();
            }
        }

        if (elementSelector != null)
        {
            elementSelector.onDraw(canvas);
        }
    }


}
