package sriracha.frontend.android.model;

import android.content.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.model.*;

public abstract class CircuitElementView extends ImageView implements View.OnTouchListener, View.OnLongClickListener
{
    private static final int[] STATE_DRAGGABLE = {R.attr.state_draggable};

    private static final int INVALID_POINTER_ID = -1;

    private CircuitElement element;

    private boolean isDraggable;

    private float positionX;
    private float positionY;

    private float touchDownDeltaX;
    private float touchDownDeltaY;
    private int activePointerId = INVALID_POINTER_ID;

    public abstract int getDrawableId();

    public CircuitElementView(Context context, CircuitElement element, float positionX, float positionY)
    {
        super(context);

        this.element = element;
        this.positionX = positionX;
        this.positionY = positionY;

        setFocusable(true);
        setImageResource(getDrawableId());
        setBackgroundResource(R.drawable.circuitelement_background);

        setOnTouchListener(this);
        setOnLongClickListener(this);
    }

    public boolean isDraggable() { return isDraggable; }
    public void setDraggable(boolean draggable)
    {
        isDraggable = draggable;
        if (!isDraggable)
            activePointerId = INVALID_POINTER_ID;
        refreshDrawableState();
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

                // Save the ID of this pointer
                activePointerId = motionEvent.getPointerId(0);

                if (!isDraggable())
                    return false; // Let the parent take care of the event.

                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (!isDraggable())
                    return false; // Let the parent take care of the event.

                if (activePointerId == INVALID_POINTER_ID)
                    break;

                // Find the index of the active pointer and fetch its position
                int pointerIndex = motionEvent.findPointerIndex(activePointerId);
                positionX += motionEvent.getX(pointerIndex) - touchDownDeltaX;
                positionY += motionEvent.getY(pointerIndex) - touchDownDeltaY;

                updatePosition();
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                setDraggable(false);
                break;
        }

        return true;
    }

    @Override
    public boolean onLongClick(View view)
    {
        setDraggable(true);
        return false;
    }

    public void updatePosition()
    {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        params.leftMargin = (int) positionX;
        params.topMargin = (int) positionY;
        setLayoutParams(params);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace)
    {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        if (isDraggable())
            mergeDrawableStates(drawableState, STATE_DRAGGABLE);
        return drawableState;
    }
}
