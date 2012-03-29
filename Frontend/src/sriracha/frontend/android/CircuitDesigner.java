package sriracha.frontend.android;

import android.graphics.*;
import android.view.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.model.*;

import java.util.*;

public class CircuitDesigner extends GestureDetector.SimpleOnGestureListener
        implements View.OnTouchListener, CircuitElementView.OnElementClickListener, CircuitElementView.OnDrawListener
{
    public static final int GRID_SIZE = 40;
    private static final int INVALID_POINTER_ID = -1;

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

    private int activePointerId = INVALID_POINTER_ID;
    private float touchDownX;
    private float touchDownY;
    private WireSegment selectedSegment;
    private float selectedSegmentStart;

    private int selectedItemId;
    private CircuitElementView selectedElement;

    private CircuitDesignerMenu circuitDesignerMenu;
    private CircuitElementActivator activator;
    private CircuitElementManager elementManager;

    private GestureDetector gestureDetector;
    private ViewGroup canvasView;

    private ArrayList<CircuitElementView> elements;
    private WireManager wireManager;
    private IWireIntersection lastInsertedIntersection;

    public CircuitDesigner(View canvasView, CircuitDesignerMenu circuitDesignerMenu, CircuitElementActivator activator)
    {
        this.circuitDesignerMenu = circuitDesignerMenu;
        this.activator = activator;
        elementManager = new CircuitElementManager();

        gestureDetector = new GestureDetector(this);
        this.canvasView = (ViewGroup) canvasView;
        this.canvasView.setOnTouchListener(this);

        elements = new ArrayList<CircuitElementView>();
        wireManager = new WireManager(this.canvasView);

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

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                selectedSegment = wireManager.getSegmentByPosition(motionEvent.getX(), motionEvent.getY());
                if (selectedSegment == null)
                    return true;

                boolean canMoveVertically = !selectedSegment.isVertical();
                if (canMoveVertically)
                    selectedSegmentStart = selectedSegment.getStart().getY();
                else
                    selectedSegmentStart = selectedSegment.getStart().getX();

                touchDownX = motionEvent.getX();
                touchDownY = motionEvent.getY();

                // Save the ID of this pointer
                activePointerId = motionEvent.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (activePointerId == INVALID_POINTER_ID)
                    break;

                // Find the index of the active pointer and fetch its position
                int pointerIndex = motionEvent.findPointerIndex(activePointerId);

                boolean canMoveHorizontally = selectedSegment.isVertical();
                if (canMoveHorizontally)
                {
                    int deltaX = snap(motionEvent.getX(pointerIndex) - touchDownX);
                    selectedSegment.moveX((int) selectedSegmentStart + deltaX);
                }
                else
                {
                    int deltaY = snap(motionEvent.getY(pointerIndex) - touchDownY);
                    selectedSegment.moveY((int) selectedSegmentStart + deltaY);
                }
                selectedSegment.invalidate();

                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                activePointerId = INVALID_POINTER_ID;
                wireManager.consolidateIntersections();
                break;
        }

        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent)
    {
        wireManager.selectSegment(null);

        int snappedX = snap(motionEvent.getX());
        int snappedY = snap(motionEvent.getY());

        if (getCursor() == CursorState.WIRE && lastInsertedIntersection != null)
        {
            // Possibilities: clicked on empty space, clicked on existing node, or clicked on existing wire segment.
            WireSegment segment = wireManager.getSegmentByPosition(snappedX, snappedY);
            if (segment == null)
            {
                // Case 1: empty space.
                // Add new node
                WireIntersection newIntersection = new WireIntersection(snappedX, snappedY);
                wireManager.connectNewIntersection(lastInsertedIntersection, newIntersection);
                lastInsertedIntersection = newIntersection;
            }
            else
            {
                // Case 2, 3: existing segment/node.
                // Add a new node. If we clicked on a node instead of a segment, we let
                // the node consolidator take care of it later.
                WireIntersection newIntersection = wireManager.splitSegment(segment, snappedX, snappedY);
                wireManager.connectNewIntersection(lastInsertedIntersection, newIntersection);
                // TODO: consolidate nodes now

                // End the wire drawing now.
                deselectElements(null);
                lastInsertedIntersection = null;
                setCanvasState(CanvasState.IDLE);
            }

            return true;
        }

        if (getCursor() == CursorState.HAND)
        {
            WireSegment segment = wireManager.getSegmentByPosition(snappedX, snappedY);
            if (segment != null)
            {
                wireManager.selectSegment(segment);
                deselectElements(null);
                circuitDesignerMenu.showSubMenu(R.id.wire_properties);
                return true;
            }
        }

        if (getCursor() != CursorState.ELEMENT)
            return false;

        deselectElements(null);

        CircuitElementView elementView = instantiateElement(snappedX, snappedY);
        if (elementView != null)
        {
            elementManager.addElement(elementView.getElement());
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
        wireManager.selectSegment(null);
        deselectElements(view);

        if (getCursor() == CursorState.WIRE)
        {
            // The first endpoint of a wire
            if (getCanvasState() == CanvasState.IDLE)
            {
                lastInsertedIntersection = ((CircuitElementView) view).getClosestPort(x, y);
                wireManager.addIntersection(lastInsertedIntersection);
                setCanvasState(CanvasState.DRAWING_WIRE);
            }
            else if (getCanvasState() == CanvasState.DRAWING_WIRE)
            {
                // Create new node, and end the wire at the new element
                CircuitElementPortView port = ((CircuitElementView) view).getClosestPort(x, y);
                wireManager.connectNewIntersection(lastInsertedIntersection, port);

                deselectElements(null);
                lastInsertedIntersection = null;
                setCanvasState(CanvasState.IDLE);
            }
        }
        else if (getCursor() == CursorState.HAND)
        {
            circuitDesignerMenu.showElementPropertiesMenu(selectedElement);
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
            }
            else
                element.setElementSelected(false);
        }
    }

    public void deselectAllElements()
    {
        setCursorToHand();
        deselectElements(null);
        wireManager.selectSegment(null);
    }

    public CircuitElementView instantiateElement(float positionX, float positionY)
    {
        return activator.instantiateElement(getSelectedItemId(), positionX, positionY, elementManager);
    }

    public void rotateSelectedElement(boolean cw)
    {
        if (selectedElement != null)
        {
            int rotation = cw ? 90 : -90;
            float newOrientation = (selectedElement.getOrientation() + rotation) % 360;
            for (CircuitElementPortView port : selectedElement.getElementPorts())
            {
                for (WireSegment segment : port.getSegments())
                {
                    IWireIntersection otherIntersection = segment.getStart() != port ? segment.getStart() : segment.getEnd();
                    if (segment.isVertical())
                    {
                        int dx = (int) (selectedElement.getWidth() * (port.transformPosition(newOrientation)[0] - port.getTransformedPosition()[0]));
                        int newX = segment.getStart().getX() + dx;
                        if (dx != 0)
                        {
                            if (otherIntersection.duplicateOnMove(segment))
                            {
                                WireIntersection duplicate = otherIntersection.duplicate(segment, wireManager);
                                duplicate.x = newX;
                            }
                            else
                            {
                                ((WireIntersection) otherIntersection).x = newX;
                            }
                            segment.invalidate();
                        }
                    }
                    else
                    {
                        int dy = (int) (selectedElement.getHeight() * (port.transformPosition(newOrientation)[1] - port.getTransformedPosition()[1]));
                        int newY = segment.getStart().getY() + dy;
                        if (dy != 0)
                        {
                            if (otherIntersection.duplicateOnMove(segment))
                            {
                                WireIntersection duplicate = otherIntersection.duplicate(segment, wireManager);
                                duplicate.y = newY;
                            }
                            else
                            {
                                ((WireIntersection) otherIntersection).y = newY;
                            }
                            segment.invalidate();
                        }
                    }
                }
            }
            selectedElement.rotate(rotation);
            wireManager.consolidateIntersections();
        }
    }

    public void deleteSelectedElement()
    {
        if (getCursor() != CursorState.HAND || selectedElement == null)
            return;

        elementManager.removeElement(selectedElement.getElement());
        elements.remove(selectedElement);
        canvasView.removeView(selectedElement);
        canvasView.invalidate();

        deselectAllElements();
        circuitDesignerMenu.showSubMenu(R.id.circuit_menu);
    }

    public void deleteSelectedWire()
    {
        if (getCursor() != CursorState.HAND)
            return;

        wireManager.deleteSelectedSegment();
        canvasView.invalidate();

        deselectAllElements();
        circuitDesignerMenu.showSubMenu(R.id.circuit_menu);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        wireManager.invalidateAll();
    }

    public static int snap(float coord)
    {
        return (int) (coord / GRID_SIZE + 0.5f) * GRID_SIZE;
    }
}
