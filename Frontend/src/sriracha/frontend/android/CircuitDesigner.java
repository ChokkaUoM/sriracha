package sriracha.frontend.android;

import android.graphics.*;
import android.view.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;

import java.util.*;

public class CircuitDesigner extends GestureDetector.SimpleOnGestureListener
        implements View.OnTouchListener, CircuitElementView.OnElementClickListener, CircuitElementView.OnDrawListener
{
    public enum CursorState
    {
        ELEMENT, HAND, SELECTION, WIRE
    }

    public enum CanvasState
    {
        IDLE, DRAWING_WIRE
    }

    private CursorState cursor;
    private CanvasState canvasState = CanvasState.IDLE;

    private int selectedItemId;
    private CircuitElementView selectedElement;

    private CircuitDesignerMenu circuitDesignerMenu;
    private CircuitElementActivator activator;

    private GestureDetector gestureDetector;
    private ViewGroup canvasView;

    private ArrayList<CircuitElementView> elements;
    private ArrayList<CircuitWireView> wires;
    private CircuitElementPortView firstWirePort;

    public CircuitDesigner(View canvasView, CircuitDesignerMenu circuitDesignerMenu, CircuitElementActivator activator)
    {
        this.circuitDesignerMenu = circuitDesignerMenu;
        this.activator = activator;

        gestureDetector = new GestureDetector(this);
        this.canvasView = (ViewGroup) canvasView;
        canvasView.setOnTouchListener(this);

        elements = new ArrayList<CircuitElementView>();
        wires = new ArrayList<CircuitWireView>();

        setCursorToHand();
    }

    public CursorState getCursor() { return cursor; }
    public void setCursorToHand() { setCursor(CursorState.HAND); }
    public void setCursorToSelection() { setCursor(CursorState.SELECTION); }
    public void setCursorToWire() { setCursor(CursorState.WIRE); }

    private void setCursor(CursorState newCursor)
    {
        cursor = newCursor;

        int itemId = -1;
        switch (getCursor())
        {
            case ELEMENT:
                itemId = selectedItemId;
                break;
            case WIRE:
                itemId = R.id.wire;
                break;
            case HAND:
                itemId = R.id.hand;
                break;
        }

        if (itemId != -1)
            circuitDesignerMenu.setSelectedItem(itemId);
    }

    public CanvasState getCanvasState() { return canvasState; }
    public void setCanvasState(CanvasState canvasState)
    {
        this.canvasState = canvasState;
    }

    public int getSelectedItemId()
    {
        return selectedItemId;
    }

    /**
     * Set a circuit element as selected so that tapping on the canvas instantiates it.
     * Do not set cursor to ELEMENT anywhere else.
     *
     * @param circuitItemId
     */
    public void selectCircuitItem(int circuitItemId)
    {
        selectedItemId = circuitItemId;
        setCursor(CursorState.ELEMENT);
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
        if (getCursor() != CursorState.ELEMENT)
            return false;

        deselectElements(null);

        CircuitElementView elementView = instantiateElement(motionEvent.getX(), motionEvent.getY());
        if (elementView != null)
        {
            elements.add(elementView);
            canvasView.addView(elementView);

            elementView.setOnElementClickListener(this);
            elementView.setOnDrawListener(this);
            elementView.updatePosition();
        }
        return true;
    }

    @Override
    public void onElementClick(View view, float x, float y)
    {
        deselectElements(view);

        if (getCursor() == CursorState.WIRE)
        {
            // The first endpoint of a wire
            if (getCanvasState() == CanvasState.IDLE)
            {
                firstWirePort = ((CircuitElementView) view).getClosestPort(x, y);
                setCanvasState(CanvasState.DRAWING_WIRE);
            } else if (getCanvasState() == CanvasState.DRAWING_WIRE)
            {
                if (firstWirePort.getElement() != view)
                {
                    CircuitWireView wire = new CircuitWireView(canvasView.getContext(), firstWirePort, ((CircuitElementView) view).getClosestPort(x, y));
                    wires.add(wire);
                    canvasView.addView(wire);
                    deselectElements(null);
                }
                setCanvasState(CanvasState.IDLE);
            }
        } else if (getCursor() == CursorState.HAND)
        {
            circuitDesignerMenu.showSubMenu(R.id.element_properties);
        }
    }

    private void deselectElements(View exceptFor)
    {
        for (CircuitElementView element : elements)
        {
            if (element == exceptFor && !element.isElementSelected())
            {
                element.setElementSelected(true);
                selectedElement = element;
            } else
                element.setElementSelected(false);
        }
    }

    public void deselectAllElements()
    {
        setCursorToHand();
        deselectElements(null);
    }

    public CircuitElementView instantiateElement(float positionX, float positionY)
    {
        return activator.instantiateElement(getSelectedItemId(), positionX, positionY);
    }

    public void deleteSelectedElement()
    {
        if (getCursor() != CursorState.HAND || selectedElement == null)
            return;

        for (CircuitWireView wire : wires)
        {
            if (wire.getStart().getElement() == selectedElement || wire.getEnd().getElement() == selectedElement)
            {
                wires.remove(wire);
                canvasView.removeView(wire);
            }
        }
        
        elements.remove(selectedElement);
        canvasView.removeView(selectedElement);
        canvasView.invalidate();

        deselectAllElements();
        circuitDesignerMenu.showSubMenu(R.id.circuit_menu);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        for (CircuitWireView wire : wires)
            wire.invalidate();
    }
}
