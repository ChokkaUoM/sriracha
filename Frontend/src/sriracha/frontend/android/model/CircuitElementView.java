package sriracha.frontend.android.model;

import android.content.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.model.*;

public abstract class CircuitElementView extends ImageView implements View.OnTouchListener/*, View.OnClickListener*/, View.OnLongClickListener
{
    private static final int[] STATE_DRAGGABLE = {R.attr.state_draggable};
    private static final int[] STATE_SELECTED = {R.attr.state_selected};

    private static final int INVALID_POINTER_ID = -1;

    private CircuitElement element;

    private boolean isDraggable;
    private boolean isElementSelected;
    private OnSelectedListener onSelectedListener;

    private float positionX;
    private float positionY;

    private float touchDownDeltaX;
    private float touchDownDeltaY;

    private int activePointerId = INVALID_POINTER_ID;
    private int possibleClickPointerId = INVALID_POINTER_ID;
    private long lastClickTime;

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
        //setOnClickListener(this);
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

    public boolean isElementSelected() { return isElementSelected; }
    public void setElementSelected(boolean elementSelected)
    {
        isElementSelected = elementSelected;
        if (elementSelected && onSelectedListener != null)
            onSelectedListener.onSelected(this);
        refreshDrawableState();
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener)
    {
        this.onSelectedListener = onSelectedListener;
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
                possibleClickPointerId = activePointerId;

                if (!isDraggable())
                    return false;

                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                possibleClickPointerId = INVALID_POINTER_ID;

                if (!isDraggable())
                    return false;

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
                setDraggable(false);
                if (possibleClickPointerId != INVALID_POINTER_ID)
                {
                    possibleClickPointerId = INVALID_POINTER_ID;
                    onClick(this);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                setDraggable(false);
                break;
        }

        return true;
    }

    //@Override
    public void onClick(View view)
    {
        lastClickTime = System.currentTimeMillis();
        setElementSelected(!isElementSelected());
    }

    @Override
    public boolean onLongClick(View view)
    {
        if (System.currentTimeMillis() - lastClickTime < 600)
            return false;

        setDraggable(true);
        setElementSelected(false);
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
        if (isElementSelected())
            mergeDrawableStates(drawableState, STATE_SELECTED);
        return drawableState;
    }

    public interface OnSelectedListener
    {
        public void onSelected(View view);
    }
}
