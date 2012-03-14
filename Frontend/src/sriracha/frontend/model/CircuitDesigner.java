package sriracha.frontend.model;

import sriracha.frontend.android.model.*;

public class CircuitDesigner
{
    public enum CursorState
    {
        HAND, SELECTION, ELEMENT
    }

    private CursorState cursor = CursorState.HAND;
    private int selectedElementId;
    private CircuitElementActivator activator;

    public CircuitDesigner(CircuitElementActivator activator)
    {
        this.activator = activator;
    }

    public CursorState getCursor() { return cursor; }
    public void setCursorToHand() { cursor = CursorState.HAND; }
    public void setCursorToSelection() { cursor = CursorState.SELECTION; }

    /**
     * Set a circuit element as selected so that tapping on the canvas instantiates it.
     * Do not set cursor to ELEMENT anywhere else.
     * @param circuitElementId
     */
    public void selectCircuitElement(int circuitElementId)
    {
        selectedElementId = circuitElementId;
        cursor = CursorState.ELEMENT;
    }

    public CircuitElementView instantiateElement(float positionX, float positionY)
    {
        return cursor == CursorState.ELEMENT ? activator.instantiateElement(selectedElementId, positionX, positionY) : null;
    }
}
