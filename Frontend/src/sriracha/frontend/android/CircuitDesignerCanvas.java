package sriracha.frontend.android;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.widget.*;
import sriracha.frontend.android.*;

public class CircuitDesignerCanvas extends RelativeLayout
{
    private boolean showGrid = true;
    private Paint paint;

    public CircuitDesignerCanvas(Context context)
    {
        super(context);
        initPaint();
    }
    public CircuitDesignerCanvas(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initPaint();
}
    public CircuitDesignerCanvas(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initPaint();
    }

    private void initPaint()
    {
        paint = new Paint();
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(1);
    }

    public boolean isShowGrid() { return showGrid; }
    public void setShowGrid(boolean showGrid)
    {
        this.showGrid = showGrid;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (showGrid)
        {
            for (int i = 0; i < getWidth(); i += CircuitDesigner.GRID_SIZE)
                canvas.drawLine(i, 0, i, getHeight(), paint);
            for (int i = 0; i < getHeight(); i += CircuitDesigner.GRID_SIZE)
                canvas.drawLine(0, i, getWidth(), i, paint);
        }
    }
}
