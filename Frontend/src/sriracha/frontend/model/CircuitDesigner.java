package sriracha.frontend.model;

public class CircuitDesigner
{
    public enum CursorState
    {
        ELEMENT, HAND, SELECTION, WIRE
    }

    public enum CanvasState
    {
        IDLE, DRAWING_WIRE
    }

    private CursorState cursor = CursorState.HAND;
    private CanvasState canvasState = CanvasState.IDLE;
    private int selectedElementId;

    public CursorState getCursor() { return cursor; }
    public void setCursorToHand() { cursor = CursorState.HAND; }
    public void setCursorToSelection() { cursor = CursorState.SELECTION; }
    public void setCursorToWire() { cursor = CursorState.WIRE; }

    public CanvasState getCanvasState() { return canvasState; }
    public void setCanvasState(CanvasState canvasState)
    {
        this.canvasState = canvasState;
    }

    public int getSelectedElementId()
    {
        return selectedElementId;
    }

    /**
     * Set a circuit element as selected so that tapping on the canvas instantiates it.
     * Do not set cursor to ELEMENT anywhere else.
     *
     * @param circuitElementId
     */
    public void selectCircuitElement(int circuitElementId)
    {
        selectedElementId = circuitElementId;
        cursor = CursorState.ELEMENT;
    }

}
