package sriracha.frontend.android.model;

import android.content.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.model.*;

public abstract class CircuitElementView extends ImageView
{
    private static final int INVALID_POINTER_ID = -1;

    private CircuitElement element;

    private float positionX;
    private float positionY;

    private float touchDownDeltaX;
    private float touchDownDeltaY;
    private int activePointerId;

    public abstract int getDrawableId();

    public CircuitElementView(Context context, CircuitElement element, float positionX, float positionY)
    {
        super(context);

        this.element = element;
        this.positionX = positionX;
        this.positionY = positionY;

        setImageResource(getDrawableId());
        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                touchDownDeltaX = event.getX();
                touchDownDeltaY = event.getY();

                // Save the ID of this pointer
                activePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (activePointerId == INVALID_POINTER_ID)
                    break;

                // Find the index of the active pointer and fetch its position
                int pointerIndex = event.findPointerIndex(activePointerId);
                positionX += event.getX(pointerIndex) - touchDownDeltaX;
                positionY += event.getY(pointerIndex) - touchDownDeltaY;

                updatePosition();
                break;
            }

            case MotionEvent.ACTION_UP:
                activePointerId = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_CANCEL:
                activePointerId = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                activePointerId = INVALID_POINTER_ID;
                break;
        }

        return true;
    }

    public void updatePosition()
    {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        params.leftMargin = (int) positionX;
        params.topMargin = (int) positionY;
        setLayoutParams(params);
    }
}
