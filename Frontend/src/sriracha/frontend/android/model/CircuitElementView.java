package sriracha.frontend.android.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import sriracha.frontend.R;
import sriracha.frontend.android.designer.CircuitDesigner;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.designer.WireSegment;
import sriracha.frontend.model.CircuitElement;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

/**
 * Rather important View class that represents a circuit element on the circuit designer canvas.
 * The properties of the element itself is contained in a backing {@link CircuitElement} field,
 * but this object holds information such as position and orientation, and is responsible for
 * responding to events on the canvas.
 */
abstract public class CircuitElementView extends ImageView implements View.OnTouchListener
{
    private static final int[] STATE_DRAGGABLE = {R.attr.state_draggable};
    private static final int[] STATE_SELECTED = {R.attr.state_selected};

    private static final int INVALID_POINTER_ID = -1;

    private UUID uuid;
    private UUID[] portUUIDs;
    private CircuitElement element;
    private CircuitElementPortView ports[];
    private transient WireManager wireManager;

    private transient boolean isElementSelected;

    private transient OnElementClickListener onElementClickListener;
    //    private transient OnInvalidateListener onInvalidateListener;
    private transient OnMoveListener onMoveListener;
    private transient OnDropListener onDropListener;

    private float positionX;
    private float positionY;
    private float orientation;

    private transient float touchDownDeltaX;
    private transient float touchDownDeltaY;
    private transient float touchDownRawX;
    private transient float touchDownRawY;

    private transient int activePointerId = INVALID_POINTER_ID;
    private transient int possibleClickPointerId = INVALID_POINTER_ID;

    abstract public int getDrawableId();

    abstract public CircuitElementPortView[] getPortViews();

    abstract public UUID getTypeUUID();

    public CircuitElementView(Context context, CircuitElement element, float positionX, float positionY, WireManager wireManager)
    {
        super(context);

        uuid = UUID.randomUUID();

        setBackgroundResource(R.drawable.circuitelement_background);
        setImageResource(getDrawableId());

        this.element = element;
        this.positionX = positionX;
        this.positionY = positionY;
        ports = getPortViews();

        this.wireManager = wireManager;

        setOnTouchListener(this);

        portUUIDs = new UUID[ports.length];
        for (int i = 0; i < ports.length; i++)
        {
            portUUIDs[i] = ports[i].getUUID();
        }
    }

    public CircuitElement getElement()
    {
        return element;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public UUID[] getPortUUIDs()
    {
        return portUUIDs;
    }

    public float getPositionX()
    {
        return positionX;
    }

    public float getPositionY()
    {
        return positionY;
    }

    public float getOrientation()
    {
        return orientation;
    }

    public void setOrientation(float orientation)
    {
        this.orientation = orientation;
        ((View) getParent()).invalidate();
//        invalidate();
//        if (onInvalidateListener != null)
//            onInvalidateListener.InvalidateDesigner();
    }

    public void setPort(int index, CircuitElementPortView port)
    {
        ports[index] = port;
    }

    public void rotate(int degrees)
    {
        setOrientation((orientation + degrees + 360) % 360);
    }

    /**
     * Gets the port closest to the specified position.
     *
     * @param x
     * @param y
     * @param useRelative
     * @return
     */
    public CircuitElementPortView getClosestPort(float x, float y, boolean useRelative)
    {
        CircuitElementPortView closestPort = null;
        float closestDistance = 0;
        for (CircuitElementPortView port : ports)
        {
            float portX = useRelative ? port.getRelativeX() : port.getX();
            float portY = useRelative ? port.getRelativeY() : port.getY();
            float distance = new PointF(portX - x, portY - y).length();
            if (closestPort == null || distance < closestDistance)
            {
                closestDistance = distance;
                closestPort = port;
            }
        }
        return closestPort;
    }

    public int getAttachedSegmentCount()
    {
        int count = 0;
        for (CircuitElementPortView port : ports)
        {
            count += port.getSegments().size();
        }
        return count;
    }

    public boolean isElementSelected()
    {
        return isElementSelected;
    }

    public void setElementSelected(boolean elementSelected)
    {
        isElementSelected = elementSelected;
        refreshDrawableState();
    }

    public void setOnElementClickListener(OnElementClickListener onElementClickListener)
    {
        this.onElementClickListener = onElementClickListener;
    }

    public void setOnDropListener(OnDropListener onDropListener)
    {
        this.onDropListener = onDropListener;
    }

    public void setOnMoveListener(OnMoveListener onMoveListener)
    {
        this.onMoveListener = onMoveListener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                touchDownDeltaX = motionEvent.getX();
                touchDownDeltaY = motionEvent.getY();

                touchDownRawX = motionEvent.getRawX();
                touchDownRawY = motionEvent.getRawY();

                // Save the ID of this pointer
                activePointerId = motionEvent.getPointerId(0);
                possibleClickPointerId = activePointerId;

                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                float distance = new PointF(motionEvent.getRawX() - touchDownRawX, motionEvent.getRawY() - touchDownRawY).length();
                if (distance > 12)
                    possibleClickPointerId = INVALID_POINTER_ID;

                if (activePointerId == INVALID_POINTER_ID)
                    break;

                // Find the index of the active pointer and fetch its position
                int pointerIndex = motionEvent.findPointerIndex(activePointerId);
                float newPositionX = CircuitDesigner.snap(positionX + motionEvent.getX(pointerIndex) - touchDownDeltaX);
                float newPositionY = CircuitDesigner.snap(positionY + motionEvent.getY(pointerIndex) - touchDownDeltaY);

                boolean hasMoved = false;
                for (CircuitElementPortView port : ports)
                {
                    int portX = (int) (newPositionX + getWidth() / 2 + getWidth() * port.getTransformedPosition()[0]);
                    int portY = (int) (newPositionY + getHeight() / 2 + getHeight() * port.getTransformedPosition()[1]);

                    for (WireSegment segment : port.getSegments())
                    {
                        if (segment.isVertical())
                            hasMoved |= segment.moveX(portX, segment.otherEnd(port));
                        else
                            hasMoved |= segment.moveY(portY, segment.otherEnd(port));
                    }
                }

                if (positionX != newPositionX || positionY != newPositionY)
                {
                    positionX = newPositionX;
                    positionY = newPositionY;
                    updatePosition();
                    refreshDrawableState();

                    //    wireManager.consolidateIntersections();

                    if (onMoveListener != null)
                        onMoveListener.onMove(this);
                }

                break;
            }

            case MotionEvent.ACTION_UP:
                if (possibleClickPointerId != INVALID_POINTER_ID)
                {
                    possibleClickPointerId = INVALID_POINTER_ID;
                    onClick(motionEvent.getX(), motionEvent.getY());
                } else
                {
                    if (onDropListener != null)
                        onDropListener.onDrop(this);
                }

                wireManager.consolidateIntersections();
                // Fall through

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                activePointerId = INVALID_POINTER_ID;
                refreshDrawableState();
                break;
        }

        return true;
    }

    public void onClick(float x, float y)
    {
        if (onElementClickListener != null)
            onElementClickListener.onElementClick(this, x, y);
    }

    public void updatePosition()
    {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        params.leftMargin = (int) positionX;
        params.topMargin = (int) positionY;
        setLayoutParams(params);
        ((View) getParent()).invalidate();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace)
    {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isElementSelected())
            mergeDrawableStates(drawableState, STATE_SELECTED);
        else if (activePointerId != INVALID_POINTER_ID)
            mergeDrawableStates(drawableState, STATE_DRAGGABLE);
        return drawableState;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.rotate(orientation, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setColor(Color.BLACK);
        canvas.drawText(element.getName(), 5, 14, paint);
    }


    public interface OnElementClickListener
    {
        public void onElementClick(View view, float x, float y);
    }

//    public interface OnInvalidateListener
//    {
//        public void onInvalidate();
//    }

    public interface OnMoveListener
    {
        public void onMove(CircuitElementView elementView);
    }

    public interface OnDropListener
    {
        public void onDrop(CircuitElementView elementView);
    }

    public void serialize(ObjectOutputStream out) throws IOException
    {
        out.writeObject(uuid);
        out.writeObject(portUUIDs);
        out.writeObject(element);
        out.writeFloat(positionX);
        out.writeFloat(positionY);
        out.writeFloat(orientation);
    }

    public void deserialize(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        uuid = (UUID) in.readObject();
        portUUIDs = (UUID[]) in.readObject();
        element = (CircuitElement) in.readObject();
        positionX = in.readFloat();
        positionY = in.readFloat();
        orientation = in.readFloat();
    }
}
