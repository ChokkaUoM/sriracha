package sriracha.frontend.android.model;

import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.android.*;
import sriracha.frontend.model.*;

import java.util.*;

abstract public class CircuitElementView extends ImageView implements View.OnTouchListener
{
    private static final int[] STATE_DRAGGABLE = {R.attr.state_draggable};
    private static final int[] STATE_SELECTED = {R.attr.state_selected};

    private static final int INVALID_POINTER_ID = -1;

    private CircuitElement element;
    private CircuitElementPortView ports[];
    private WireManager wireManager;

    private boolean isElementSelected;

    private OnElementClickListener onElementClickListener;
    private OnInvalidateListener onInvalidateListener;
    private OnMoveListener onMoveListener;

    private float positionX;
    private float positionY;
    private float orientation;

    private float touchDownDeltaX;
    private float touchDownDeltaY;
    private float touchDownRawX;
    private float touchDownRawY;

    private int activePointerId = INVALID_POINTER_ID;
    private int possibleClickPointerId = INVALID_POINTER_ID;

    abstract public int getDrawableId();

    abstract public CircuitElementPortView[] getElementPorts();

    public CircuitElementView(Context context, CircuitElement element, float positionX, float positionY, WireManager wireManager)
    {
        super(context);

        setBackgroundResource(R.drawable.circuitelement_background);
        setImageResource(getDrawableId());

        this.element = element;
        this.positionX = positionX;
        this.positionY = positionY;
        ports = getElementPorts();

        this.wireManager = wireManager;

        setOnTouchListener(this);
    }

    public CircuitElement getElement()
    {
        return element;
    }

    public float getPositionX() { return positionX; }
    public float getPositionY() { return positionY; }

    public float getOrientation() { return orientation; }
    public void setOrientation(float orientation)
    {
        this.orientation = orientation;
        invalidate();
        if (onInvalidateListener != null)
            onInvalidateListener.onInvalidate();
    }
    public void rotate(int degrees)
    {
        setOrientation((orientation + degrees) % 360);
    }

    public CircuitElementPortView getClosestPort(float x, float y)
    {
        CircuitElementPortView closestPort = null;
        float closestDistance = 0;
        for (CircuitElementPortView port : ports)
        {
            float portX = port.getRelativeX();
            float portY = port.getRelativeY();
            float distance = new PointF(portX - x, portY - y).length();
            if (closestPort == null || distance < closestDistance)
            {
                closestDistance = distance;
                closestPort = port;
            }
        }
        return closestPort;
    }

    public boolean isElementSelected() { return isElementSelected; }
    public void setElementSelected(boolean elementSelected)
    {
        isElementSelected = elementSelected;
        refreshDrawableState();
    }

    public void setOnElementClickListener(OnElementClickListener onElementClickListener)
    {
        this.onElementClickListener = onElementClickListener;
    }

    public void setOnInvalidateListener(OnInvalidateListener onInvalidateListener)
    {
        this.onInvalidateListener = onInvalidateListener;
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
                if (distance > 8)
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

                positionX = newPositionX;
                positionY = newPositionY;
                updatePosition();
                refreshDrawableState();

                if (hasMoved)
                    wireManager.consolidateIntersections();
                
                if (onMoveListener != null)
                    onMoveListener.onMove(this);

                break;
            }

            case MotionEvent.ACTION_UP:
                if (possibleClickPointerId != INVALID_POINTER_ID)
                {
                    possibleClickPointerId = INVALID_POINTER_ID;
                    onClick(motionEvent.getX(), motionEvent.getY());
                }
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
        if (onInvalidateListener != null)
            onInvalidateListener.onInvalidate();
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

    public interface OnInvalidateListener
    {
        public void onInvalidate();
    }
    
    public interface OnMoveListener
    {
        public void onMove(CircuitElementView elementView);
    }
}
