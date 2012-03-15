package sriracha.frontend.android.model;

import android.view.*;
import sriracha.frontend.model.*;
import java.util.*;

public class CircuitDesignCanvas extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener, CircuitElementView.OnSelectedListener
{
    private CircuitDesigner circuitDesigner;
    private CircuitElementActivator activator;

    private GestureDetector gestureDetector;
    private ViewGroup canvasView;

    private ArrayList<CircuitElementView> elements;

    public CircuitDesignCanvas(View canvasView, CircuitDesigner circuitDesigner, CircuitElementActivator activator)
    {
        this.circuitDesigner = circuitDesigner;
        this.activator = activator;

        gestureDetector = new GestureDetector(this);
        this.canvasView = (ViewGroup) canvasView;
        canvasView.setOnTouchListener(this);

        elements = new ArrayList<CircuitElementView>();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        gestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent)
    {
        onSelected(null);

        CircuitElementView elementView = instantiateElement(motionEvent.getX(), motionEvent.getY());
        if (elementView != null)
        {
            elements.add(elementView);
            canvasView.addView(elementView);
            elementView.setOnSelectedListener(this);
            elementView.updatePosition();
        }
        return true;
    }

    @Override
    public void onSelected(View view)
    {
        for (CircuitElementView element : elements)
        {
            if (element != view)
                element.setElementSelected(false);
        }
    }

    public CircuitElementView instantiateElement(float positionX, float positionY)
    {
        return circuitDesigner.getCursor() == CircuitDesigner.CursorState.ELEMENT
                ? activator.instantiateElement(circuitDesigner.getSelectedElementId(), positionX, positionY)
                : null;
    }
}
