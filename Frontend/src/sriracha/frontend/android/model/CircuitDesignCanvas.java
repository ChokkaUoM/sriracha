package sriracha.frontend.android.model;

import android.graphics.*;
import android.view.*;
import sriracha.frontend.model.*;
import java.util.*;

public class CircuitDesignCanvas extends GestureDetector.SimpleOnGestureListener
        implements View.OnTouchListener, CircuitElementView.OnSelectedListener, CircuitElementView.OnDrawListener
{
    private CircuitDesigner circuitDesigner;
    private CircuitElementActivator activator;

    private GestureDetector gestureDetector;
    private ViewGroup canvasView;

    private ArrayList<CircuitElementView> elements;
    private ArrayList<CircuitWireView> wires;
    private CircuitElementView firstWireEndpoint;

    public CircuitDesignCanvas(View canvasView, CircuitDesigner circuitDesigner, CircuitElementActivator activator)
    {
        this.circuitDesigner = circuitDesigner;
        this.activator = activator;

        gestureDetector = new GestureDetector(this);
        this.canvasView = (ViewGroup) canvasView;
        canvasView.setOnTouchListener(this);

        elements = new ArrayList<CircuitElementView>();
        wires = new ArrayList<CircuitWireView>();
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
        deselectElements(null);

        CircuitElementView elementView = instantiateElement(motionEvent.getX(), motionEvent.getY());
        if (elementView != null)
        {
            elements.add(elementView);
            canvasView.addView(elementView);
            
            elementView.setOnSelectedListener(this);
            elementView.setOnDrawListener(this);
            elementView.updatePosition();
        }
        return true;
    }

    @Override
    public void onSelected(View view)
    {
        deselectElements(view);

        if (circuitDesigner.getCursor() == CircuitDesigner.CursorState.WIRE)
        {
            // The first endpoint of a wire
            if (circuitDesigner.getCanvasState() == CircuitDesigner.CanvasState.IDLE)
            {
                firstWireEndpoint = (CircuitElementView) view;
                circuitDesigner.setCanvasState(CircuitDesigner.CanvasState.DRAWING_WIRE);
            }
            else if (circuitDesigner.getCanvasState() == CircuitDesigner.CanvasState.DRAWING_WIRE)
            {
                if (firstWireEndpoint != view)
                {
                    CircuitWireView wire = new CircuitWireView(canvasView.getContext(), firstWireEndpoint, (CircuitElementView) view);
                    wires.add(wire);
                    canvasView.addView(wire);
                }
                deselectElements(null);
                circuitDesigner.setCanvasState(CircuitDesigner.CanvasState.IDLE);
            }
        }
    }
    
    private void deselectElements(View exceptFor)
    {
        for (CircuitElementView element : elements)
        {
            if (element != exceptFor)
                element.setElementSelected(false);
        }
    }

    public CircuitElementView instantiateElement(float positionX, float positionY)
    {
        return circuitDesigner.getCursor() == CircuitDesigner.CursorState.ELEMENT
                ? activator.instantiateElement(circuitDesigner.getSelectedElementId(), positionX, positionY)
                : null;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        for (CircuitWireView wire : wires)
            wire.invalidate();
    }
}
