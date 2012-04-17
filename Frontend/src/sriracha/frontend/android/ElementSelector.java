package sriracha.frontend.android;

import android.graphics.*;
import android.widget.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.android.results.*;

import java.util.*;

/**
 * An IElementSelector for choosing circuit elements from the circuit designer.
 * The constructor takes a list of {@link CircuitElementView}s that are allowed
 * to be selected.
 * When this is the active selected in the circuit designer, the this class's
 * {@link ElementSelector#onDraw(Canvas) onDraw} method will be called to highlight
 * the allowed elements appropriately.
 */
public class ElementSelector implements IElementSelector<CircuitElementView>
{
    private TextView textView;
    private ArrayList<CircuitElementView> selectableElements;
    private IElementSelector.OnSelectListener onSelectListener;

    public ElementSelector(TextView textView, ArrayList<CircuitElementView> selectableElements)
    {
        this.textView = textView;
        this.selectableElements = selectableElements;
    }

    @Override
    public void setOnSelectListener(IElementSelector.OnSelectListener onSelectListener)
    {
        this.onSelectListener = onSelectListener;
    }

    @Override
    public boolean onSelect(CircuitElementView selectedElement)
    {
        if (selectableElements.contains(selectedElement))
        {
            textView.setText(selectedElement.getElement().getName());
            if (onSelectListener != null)
                onSelectListener.onSelect(selectedElement);
            return true;
        }
        return false;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(0xAA, 0xCC, 0x00, 0x00));

        for (CircuitElementView element : selectableElements)
        {
            RectF rect = new RectF(element.getLeft(), element.getTop(), element.getRight(), element.getBottom());
            canvas.drawRoundRect(rect, 5, 5, paint);
        }
    }
}
