package sriracha.frontend.android.designer;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import sriracha.frontend.NetlistGenerator;
import sriracha.frontend.R;
import sriracha.frontend.android.ElementSelector;
import sriracha.frontend.android.EpicTouchListener;
import sriracha.frontend.android.NodeSelector;
import sriracha.frontend.android.model.CircuitElementActivator;
import sriracha.frontend.android.model.CircuitElementPortView;
import sriracha.frontend.android.model.CircuitElementView;
import sriracha.frontend.android.results.IElementSelector;
import sriracha.frontend.model.CircuitElement;
import sriracha.frontend.model.CircuitElementManager;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class CircuitDesigner extends GestureDetector.SimpleOnGestureListener
        implements View.OnTouchListener, CircuitElementView.OnElementClickListener, CircuitElementView.OnInvalidateListener,
        CircuitElementView.OnDropListener, CircuitElementView.OnMoveListener
{
    public static final int GRID_SIZE = 40;

    public enum CursorState
    {
        ELEMENT, HAND, SELECTION, WIRE, SELECTING_ELEMENT
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
    private CircuitElementManager elementManager;

    private transient GestureDetector gestureDetector;
    private EpicTouchListener epicTouchListener;
    private CircuitDesignerCanvas canvasView;

    private ArrayList<CircuitElementView> elements;
    private WireManager wireManager;
    private IWireIntersection lastInsertedIntersection;

    private IElementSelector elementSelector;

    public CircuitDesigner(View canvasView, CircuitDesignerMenu circuitDesignerMenu, CircuitElementActivator activator)
    {
        this.circuitDesignerMenu = circuitDesignerMenu;
        this.activator = activator;
        elementManager = new CircuitElementManager();

        gestureDetector = new GestureDetector(this);
        epicTouchListener = new TouchListener();
        this.canvasView = (CircuitDesignerCanvas) canvasView;
        this.canvasView.setOnTouchListener(this);

        elements = new ArrayList<CircuitElementView>();
        wireManager = new WireManager(this.canvasView);

        setCursorToHand();
    }

    public CursorState getCursor()
    {
        return cursor;
    }

    public void setCursorToHand()
    {
        setCursor(CursorState.HAND);
    }

    public void setCursorToSelection()
    {
        setCursor(CursorState.SELECTION);
    }

    public void setCursorToWire()
    {
        setCursor(CursorState.WIRE);
    }

    public void setCursorToSelectingElement(IElementSelector elementSelector)
    {
        setElementSelector(elementSelector);
        setCursor(CursorState.SELECTING_ELEMENT);
        canvasView.invalidate();
    }

    private void setCursor(CursorState newCursor)
    {
        cursor = newCursor;

        int itemId = -1;
        switch (getCursor())
        {
            case ELEMENT:
                itemId = selectedItemId;
                canvasState = CanvasState.IDLE;
                break;
            case WIRE:
                itemId = R.id.wire;
                break;
            case HAND:
                itemId = R.id.hand;
                canvasState = CanvasState.IDLE;
                break;
        }

        if (itemId != -1)
            circuitDesignerMenu.setSelectedItem(itemId);
    }

    public CanvasState getCanvasState()
    {
        return canvasState;
    }

    public void setCanvasState(CanvasState canvasState)
    {
        this.canvasState = canvasState;
    }

    public int getSelectedItemId()
    {
        return selectedItemId;
    }

    public CircuitElementManager getElementManager()
    {
        return elementManager;
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

    private void setElementSelector(IElementSelector elementSelector)
    {
        this.elementSelector = elementSelector;
        canvasView.setElementSelector(elementSelector);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        gestureDetector.onTouchEvent(motionEvent);
        boolean toReturn = epicTouchListener.onTouch(view, motionEvent);
        if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
            wireManager.consolidateIntersections();

        //  ((LinearLayout)canvasView.getParent()).onTouchEvent(motionEvent);
        return toReturn;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent)
    {
        wireManager.selectSegment(null);

        int snappedX = snap(motionEvent.getX());
        int snappedY = snap(motionEvent.getY());

        if (getCursor() == CursorState.WIRE && lastInsertedIntersection != null)
        {
            // Possibilities: clicked near a port (but not on the element itself), clicked on empty space,
            // clicked on existing node, or clicked on existing wire segment.
            CircuitElementPortView port = getPortAt(snappedX, snappedY, null);
            if (port != null)
            {
                // Case 1: clicked a port
                onElementClick(port.getElement(), snappedX, snappedY);
            }
            else
            {
                WireSegment segment = wireManager.getSegmentByPosition(snappedX, snappedY);
                if (segment == null)
                {
                    // Case 2: empty space.
                    // Add new node
                    WireIntersection newIntersection = new WireIntersection(snappedX, snappedY);
                    replaceLastInsertedIntersection(snappedX, snappedY);
                    wireManager.connectNewIntersection(lastInsertedIntersection, newIntersection);
                    lastInsertedIntersection = newIntersection;
                }
                else
                {
                    // Case 3, 4: existing segment/node.
                    // Add a new node. If we clicked on a node instead of a segment, we let
                    // the node consolidator take care of it later.
                    WireIntersection newIntersection = wireManager.splitSegment(segment, snappedX, snappedY);
                    replaceLastInsertedIntersection(snappedX, snappedY);
                    wireManager.connectNewIntersection(lastInsertedIntersection, newIntersection);
                    // TODO: consolidate nodes now

                    // End the wire drawing now.
                    deselectElements(null);
                    lastInsertedIntersection = null;
                    setCanvasState(CanvasState.IDLE);
                }
            }

            return true;
        }
        else if (getCursor() == CursorState.HAND)
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
        else if (getCursor() == CursorState.SELECTING_ELEMENT && elementSelector instanceof NodeSelector)
        {
            WireSegment segment = wireManager.getSegmentByPosition(snappedX, snappedY);
            if (segment != null)
            {
                if (elementSelector.onSelect(segment))
                {
                    setCursorToHand();
                    setElementSelector(null);
                    canvasView.invalidate();
                }
            }
        }


        if (getCursor() != CursorState.ELEMENT)
            return false;

        deselectElements(null);

        CircuitElementView elementView = instantiateElement(snappedX, snappedY);
        if (elementView != null)
        {
            addElement(elementView);
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
                lastInsertedIntersection = ((CircuitElementView) view).getClosestPort(x, y, true);
                wireManager.addIntersection(lastInsertedIntersection);
                setCanvasState(CanvasState.DRAWING_WIRE);
            }
            else if (getCanvasState() == CanvasState.DRAWING_WIRE)
            {
                // Create new node, and end the wire at the new element
                CircuitElementPortView port = ((CircuitElementView) view).getClosestPort(lastInsertedIntersection.getX(), lastInsertedIntersection.getY(), false);
                replaceLastInsertedIntersection(port.getX(), port.getY());
                wireManager.connectNewIntersection(lastInsertedIntersection, port);

                deselectElements(null);
                lastInsertedIntersection = null;
                setCanvasState(CanvasState.IDLE);
            }
        }
        else if (getCursor() == CursorState.SELECTING_ELEMENT && elementSelector instanceof ElementSelector)
        {
            if (elementSelector.onSelect((CircuitElementView) view))
            {
                setCursorToHand();
                setElementSelector(null);
                canvasView.invalidate();
            }
        }
        else
        {
            setCursorToHand();
            circuitDesignerMenu.showElementPropertiesMenu(selectedElement, this);
        }
    }

    public void addElement(CircuitElementView elementView)
    {
        elementManager.addElement(elementView.getElement());
        elements.add(elementView);
        canvasView.addView(elementView);

        elementView.setOnElementClickListener(this);
        elementView.setOnInvalidateListener(this);
        elementView.setOnDropListener(this);
        elementView.setOnMoveListener(this);
        elementView.updatePosition();
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
        return activator.instantiateElement(getSelectedItemId(), positionX, positionY, elementManager, wireManager);
    }

    public CircuitElementView instantiateElement(int elementId)
    {
        return activator.instantiateElement(elementId, 0, 0, elementManager, wireManager);
    }

    public CircuitElementView instantiateElement(UUID elementUUID) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        return activator.instantiateElement(elementUUID, 0, 0, elementManager, wireManager);
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

        wireManager.removeElement(selectedElement);
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
    public void onInvalidate()
    {
        wireManager.invalidateAll();
        if (getCursor() == CursorState.SELECTING_ELEMENT)
        {
            canvasView.invalidate();
        }
    }

    @Override
    public void onDrop(CircuitElementView elementView)
    {
        for (CircuitElementPortView port : elementView.getElementPorts())
        {
            if (port.getSegments().isEmpty())
            {
                CircuitElementPortView portIntersection = getPortAt(port.getX(), port.getY(), port.getElement());
                if (portIntersection != null)
                {
                    wireManager.addIntersection(port);
                    wireManager.connectNewIntersection(port, portIntersection);
                }
            }
        }
    }

    private CircuitElementPortView getPortAt(int x, int y, CircuitElementView not)
    {
        for (CircuitElementView element : elements)
        {
            if (element == not)
                continue;

            for (CircuitElementPortView port : element.getElementPorts())
            {
                if (x == port.getX() && y == port.getY())
                {
                    return port;
                }
            }
        }
        return null;
    }

    private void replaceLastInsertedIntersection(int x, int y)
    {
        if (lastInsertedIntersection instanceof CircuitElementPortView)
        {
            CircuitElementPortView port = (CircuitElementPortView) lastInsertedIntersection;
            if (port.getElement().getAttachedSegmentCount() == 0)
            {
                lastInsertedIntersection = port.getElement().getClosestPort(x, y, false);
                wireManager.addIntersection(lastInsertedIntersection);
            }
        }
    }

    @Override
    public void onMove(CircuitElementView elementView)
    {
        if (getCursor() == CursorState.WIRE)
            setCursorToHand();
    }

    public String generateNetlist()
    {
        NetlistGenerator generator = new NetlistGenerator();
        String netlist = generator.generate(wireManager, elementManager);
        return netlist;
    }

    public CircuitElement getElementByName(String elementName)
    {
        return elementManager.getElementByName(elementName);
    }

    public ArrayList<CircuitElementView> getElements()
    {
        return elements;
    }

    public WireManager getWireManager()
    {
        return wireManager;
    }

    public void setWireManager(WireManager wireManager)
    {
        this.wireManager = wireManager;
    }

    public static int snap(float coord)
    {
        return (int) (coord / GRID_SIZE + 0.5f) * GRID_SIZE;
    }

    private class TouchListener extends EpicTouchListener
    {
        private WireSegment selectedSegment;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            // We want to record the selectedSegment outside the onSingleFingerMove()
            // method because otherwise it causes some issues.
            // As you move your finger, at some point before the segment snaps to the
            // next grid-line, the finger will leave its collision bounds, causing the
            // drag to end.
            switch (motionEvent.getActionMasked())
            {
                case MotionEvent.ACTION_DOWN:
                    selectedSegment = wireManager.getSegmentByPosition(motionEvent.getX(), motionEvent.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    selectedSegment = null;
                    break;
            }
            return super.onTouch(view, motionEvent);
        }

        @Override
        protected boolean onSingleFingerMove(float dX, float dY, float finalX, float finalY)
        {
            if (selectedSegment == null)
                return false;

            boolean canMoveHorizontally = selectedSegment.isVertical();
            if (canMoveHorizontally)
            {
                selectedSegment.moveX(snap(finalX));
            }
            else
            {
                selectedSegment.moveY(snap(finalY));
            }
            selectedSegment.invalidate();
            return true;
        }
    }
}
