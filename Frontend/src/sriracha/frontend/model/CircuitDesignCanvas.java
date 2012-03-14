package sriracha.frontend.model;

import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;

public class CircuitDesignCanvas extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener
{
    private ViewGroup canvasView;
    private CircuitDesigner circuitDesigner;
    private GestureDetector gestureDetector;

    private int elementCount;

    public CircuitDesignCanvas(View canvasView, CircuitDesigner circuitDesigner)
    {
        this.canvasView = (ViewGroup)canvasView;
        this.circuitDesigner = circuitDesigner;

        gestureDetector = new GestureDetector(this);
        canvasView.setOnTouchListener(this);
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
        CircuitElementView elementView = circuitDesigner.instantiateElement(motionEvent.getX(), motionEvent.getY());
        if (elementView != null)
        {
            canvasView.addView(elementView);
            elementView.updatePosition();
        }
        return true;
    }
}
