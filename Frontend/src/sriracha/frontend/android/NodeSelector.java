package sriracha.frontend.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.TextView;
import sriracha.frontend.android.designer.WireSegment;
import sriracha.frontend.android.results.IElementSelector;

import java.util.ArrayList;

/**
 * An IElementSelector for choosing nodes from the circuit designer.
 * The constructor takes a list of {@link WireSegment}s
 * and the onSelectListener is responsible for figuring out which node the selected
 * segment belongs to.
 * When this is the active selected in the circuit designer, the this class's
 * {@link NodeSelector#onDraw(Canvas) onDraw} method will be called to highlight
 * the segments appropriately.
 */
public class NodeSelector implements IElementSelector<WireSegment>
{
    private TextView textView;
    private ArrayList<WireSegment> wireSegments;
    private IElementSelector.OnSelectListener onSelectListener;

    public NodeSelector(TextView textView, ArrayList<WireSegment> wireSegments)
    {
        this.textView = textView;
        this.wireSegments = wireSegments;
    }

    @Override
    public void setOnSelectListener(IElementSelector.OnSelectListener onSelectListener)
    {
        this.onSelectListener = onSelectListener;
    }

    @Override
    public boolean onSelect(WireSegment selectedSegment)
    {
        if (wireSegments.contains(selectedSegment))
        {
            if (onSelectListener != null)
                onSelectListener.onSelect(selectedSegment);
            return true;
        }
        return false;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setColor(Color.argb(0xAA, 0xCC, 0x00, 0x00));

        for (WireSegment segment : wireSegments)
        {
            RectF rect = new RectF(segment.getBounds());
            canvas.drawRoundRect(rect, 5, 5, paint);
        }
    }
}
