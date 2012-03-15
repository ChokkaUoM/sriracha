package sriracha.frontend.android.model;

import android.view.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

public class CircuitDesignCanvas extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener
{
    private CircuitDesigner circuitDesigner;
    private CircuitElementActivator activator;

    private GestureDetector gestureDetector;
    private ViewGroup canvasView;

    public CircuitDesignCanvas(View canvasView, CircuitDesigner circuitDesigner, CircuitElementActivator activator)
    {
        this.circuitDesigner = circuitDesigner;
        this.activator = activator;

        gestureDetector = new GestureDetector(this);
        this.canvasView = (ViewGroup) canvasView;
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
        CircuitElementView elementView = instantiateElement(motionEvent.getX(), motionEvent.getY());
        if (elementView != null)
        {
            canvasView.addView(elementView);
            elementView.updatePosition();
        }
        return true;
    }



    public CircuitElementView instantiateElement(float positionX, float positionY)
    {
        return circuitDesigner.getCursor() == CircuitDesigner.CursorState.ELEMENT
                ? activator.instantiateElement(circuitDesigner.getSelectedElementId(), positionX, positionY)
                : null;
    }
}
