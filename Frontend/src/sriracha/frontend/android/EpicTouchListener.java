package sriracha.frontend.android;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Extend this class for simplified Touch event handling.
 */
public abstract class EpicTouchListener implements View.OnTouchListener
{
    /**
     * a value between 0 and 1
     * 0 -> only perfectly opposed 2 finger motions are considered a scale gesture
     * 1 -> any 2 finger motion is a scale gesture
     */
    protected double scaleTolerance = 0.12;

    /**
     * a value between 0 and 1
     * 0 -> only perfectly parallel 2 finger motions are considered a swipe gesture
     * 1 -> any 2 finger motion is a swipe gesture
     */
    protected double swipeTolerance = 0.3;

    private ArrayList<Finger> activeFingers;

    private static class Finger
    {
        int id;

        float x, y;

        float oldX, oldY;

        float startX, startY;

        boolean isTap = true;

        float distX()
        {
            return x - oldX;
        }

        float distY()
        {
            return y - oldY;
        }

        float dist()
        {
            return (float) Math.sqrt(Math.pow(distX(), 2) + Math.pow(distY(), 2));
        }

        double angle()
        {
            return Math.atan2(y - oldY, x - oldX);
        }
    }

    protected EpicTouchListener()
    {
        activeFingers = new ArrayList<Finger>();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        int actionIndex = motionEvent.getActionIndex();
        int actionId = motionEvent.getPointerId(actionIndex);
        int pointerCount = motionEvent.getPointerCount();

        boolean eatEvent = false;
        switch (motionEvent.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {

                Finger f1 = addFinger(actionId, motionEvent.getX(), motionEvent.getY());

                onSingleFingerDown(f1.x, f1.y);

                //always return true for ACTION_DOWN so that we get subsequent ACTION_MOVE events
                return true;

            }
            case MotionEvent.ACTION_POINTER_DOWN:
            {

                addFinger(actionId, motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));


                switch (pointerCount)
                {
                    case 2:
                    {
                        Finger f1 = activeFingers.get(0);
                        Finger f2 = activeFingers.get(1);
                        onTwoFingerDown(f1.x, f1.y, f2.x, f2.y);
                        break;
                    }
                    case 3:
                    {
                        Finger f1 = activeFingers.get(0);
                        Finger f2 = activeFingers.get(1);
                        Finger f3 = activeFingers.get(2);
                        onThreeFingerDown(f1.x, f1.y, f2.x, f2.y, f3.x, f3.y);
                        break;
                    }
                    //todo: extend to more fingers
                }


                //always return true for ACTION_POINTER_DOWN so that we get subsequent ACTION_MOVE events
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                eatEvent = onAllFingersUp(motionEvent.getX(), motionEvent.getY());

                Finger f = activeFingers.get(0);
                if (f.isTap)
                {
                    eatEvent |= onSingleFingerTap(f.startX, f.startY);
                }
            }//fall through intentional!
            case MotionEvent.ACTION_POINTER_UP:
                activeFingers.remove(findById(actionId));
                if (eatEvent) return true;
                break;

            case MotionEvent.ACTION_MOVE:
            {

                if (activeFingers.size() != pointerCount)
                {
                    fixPointerMissmatch(motionEvent);
                }


                for (int i = 0; i < pointerCount; i++)
                {
                    updateFinger(activeFingers.get(i), motionEvent);
                }


                switch (pointerCount)
                {
                    case 1:
                    {
                        Finger f = activeFingers.get(0);
                        return onSingleFingerMove(f.distX(), f.distY(), f.x, f.y);
                    }
                    case 2:
                    {
                        Finger f1 = activeFingers.get(0);
                        Finger f2 = activeFingers.get(1);


                        double angleDiff = centerRad(f2.angle() - f1.angle());

                        boolean isScale = Math.abs(angleDiff) >= (1 - scaleTolerance) * Math.PI;
                        boolean isSwipe = Math.abs(angleDiff) <= swipeTolerance * Math.PI;

                        boolean consumed = false;
                        if (isScale)
                        {
                            float oldDX = f2.oldX - f1.oldX,
                                    oldDY = f2.oldY - f1.oldY,
                                    dx = f2.x - f1.x,
                                    dy = f2.y - f1.y;


                            float xFactor = Math.abs(oldDX / dx),
                                    yFactor = Math.abs(oldDY / dy);

                            double xPerc = Math.abs(1 - xFactor);
                            double yPerc = Math.abs(1 - yFactor);

                            //if one factor is much greater than the other than a zoom
                            //along that axis was probably intended.
                            if (xPerc / yPerc > 10) yFactor = 1;
                            else if (yPerc / xPerc > 10) xFactor = 1;

                            //factors can easily get enormous if the two fingers start or end very close
                            xFactor = clamp(xFactor, 0.9f, 1.1f);
                            yFactor = clamp(yFactor, 0.9f, 1.1f);

                            if (xFactor != 1 || yFactor != 1)
                            {

                                float xCenter = (f1.oldX + f2.oldX + f1.x + f2.x) / 4;
                                float yCenter = (f1.oldY + f2.oldY + f1.y + f2.y) / 4;

                                consumed = onScale(xFactor, yFactor, xCenter, yCenter);
                            }

                        }

                        if (isSwipe)
                        {
                            consumed |= onTwoFingerSwipe((f1.distX() + f2.distX()) / 2, (f1.distY() + f2.distY()) / 2);
                        }

                        if (!consumed)
                        {
                            return onTwoFingerMove(f1.distX(), f1.distY(), f2.distX(), f2.distY());
                        }

                        return consumed;
                    }
                    case 3:
                    {//todo: extend to more fingers

                        Finger f1 = activeFingers.get(0);
                        Finger f2 = activeFingers.get(1);
                        Finger f3 = activeFingers.get(2);

                        double a1 = f1.angle();
                        double a2 = f2.angle();
                        double a3 = f3.angle();

                        double max = Math.max(a1, Math.max(a2, a3));
                        double min = Math.min(a1, Math.min(a2, a3));

                        boolean isSwipe = centerRad(max - min) <= swipeTolerance * Math.PI;
                        boolean consumed = false;
                        if (isSwipe)
                        {
                            consumed |= onThreeFingerSwipe((f1.distX() + f2.distX() + f3.distX()) / 3,
                                    (f1.distY() + f2.distY() + f3.distY()) / 3);
                        }

                        return consumed;
                    }

                }
                break;
            }
        }

        return false;
    }

    private void fixPointerMissmatch(MotionEvent motionEvent)
    {
        for (int i = activeFingers.size() - 1; i >= 0; i--)
        {
            int index = motionEvent.findPointerIndex(activeFingers.get(i).id);
            if (index == -1)
            {
                activeFingers.remove(i);

            }
        }
    }

    private Finger findById(int id)
    {
        for (int i = 0; i < activeFingers.size(); i++)
        {
            Finger f = activeFingers.get(i);
            if (f.id == id) return f;
        }
        return null;
    }

    private void updateFinger(Finger f, MotionEvent e)
    {
        int index = e.findPointerIndex(f.id);
        f.oldX = f.x;
        f.oldY = f.y;
        f.x = e.getX(index);
        f.y = e.getY(index);

        float dx = f.x - f.startX, dy = f.y - f.startY;

        if (dx * dx + dy * dy > 400)
        {
            f.isTap = false;
        }

    }

    private Finger addFinger(int id, float x, float y)
    {
        Finger f = new Finger();
        f.id = id;
        f.x = x;
        f.y = y;
        f.startX = x;
        f.startY = y;
        activeFingers.add(f);
        return f;
    }

    private float clamp(float value, float min, float max)
    {
        return value < min ? min : value > max ? max : value;
    }


    private double centerRad(double radians)
    {
        radians = radians % (2 * Math.PI);
        if (radians < -Math.PI) return radians + 2 * Math.PI;
        if (radians > Math.PI) return radians - 2 * Math.PI;
        return radians;

    }

    /**
     * Called whenever there is only 1 finger on the screen and that it has moved.
     *
     * @param dX     X distance moved since last call to this method
     * @param dY     Y distance moved since last call to this method
     * @param finalX X resting place of finger after movement
     * @param finalY Y resting place of finger after movement
     * @return true if the the event should be consumed ie not passed on.
     */
    protected boolean onSingleFingerMove(float dX, float dY, float finalX, float finalY)
    {
        return false;
    }

    /**
     * Called whenever there are 2 fingers on the screen and that they have moved
     * roughly in parallel
     *
     * @param dX average X distance over both fingers
     * @param dY average Y distance over both fingers
     * @return true if the the event should be consumed ie not passed on.
     */
    protected boolean onTwoFingerSwipe(float dX, float dY)
    {
        return false;
    }

    /**
     * Called whenever there are 3 fingers on the screen and that they have moved
     * roughly in parallel
     *
     * @param dX average X distance over all fingers
     * @param dY average Y distance over all fingers
     * @return true if the the event should be consumed ie not passed on.
     */
    protected boolean onThreeFingerSwipe(float dX, float dY)
    {
        return false;
    }


    /**
     * Called whenever A single finger first touches the screen.
     * (no other fingers can already be touching)
     *
     * @param x x position
     * @param y y position
     */
    protected void onSingleFingerDown(float x, float y)
    {
    }

    /**
     * Called whenever A single finger taps the screen. down and up without moving
     * (no other fingers can already be touching)
     *
     * @param x x position
     * @param y y position
     */
    protected boolean onSingleFingerTap(float x, float y)
    {
        return false;
    }


    /**
     * Called if the user has used a pinch-zoom like gesture.
     * This call is followed by a call to OnTwoFingerMove if it returns false
     *
     * @param xFactor scaling factor along x axis
     * @param yFactor scaling factor along y axis
     * @param xCenter x scale center
     * @param yCenter y scale center
     * @return true if the the event should be consumed ie not passed on.
     */
    protected boolean onScale(float xFactor, float yFactor, float xCenter, float yCenter)
    {
        return false;
    }


    /**
     * This is invoked when there are exactly two fingers touching and at at least one of them has moved.
     * This method is only called if a preceding call to a more specific 2 finger gesture handler returns true
     * for example:
     *
     * @param dX1 x delta from first finger
     * @param dY1 y delta from first finger
     * @param dX2 x delta from second finger
     * @param dY2 y delta from second finger
     * @return true if the the event should be consumed ie not passed on.
     * @see #onScale(float, float, float, float)
     * @see #onTwoFingerSwipe(float, float)
     */
    protected boolean onTwoFingerMove(float dX1, float dY1, float dX2, float dY2)
    {
        return false;
    }

    /**
     * Called When the Second finger first touches down on the screen
     *
     * @param x1 x position of first finger
     * @param y1 y position of first finger
     * @param x2 x position of second finger
     * @param y2 y position of second finger
     */
    protected void onTwoFingerDown(float x1, float y1, float x2, float y2)
    {
    }

    /**
     * Called When the Second finger first touches down on the screen
     *
     * @param x1 x position of first finger
     * @param y1 y position of first finger
     * @param x2 x position of second finger
     * @param y2 y position of second finger
     * @param x3 x position of third finger
     * @param y3 y position of third finger
     */
    protected void onThreeFingerDown(float x1, float y1, float x2, float y2, float x3, float y3)
    {
    }


    /**
     * Called when the last finger touching has just been lifted up
     *
     * @param x x position of last touch
     * @param y y position of last touch
     * @return true if the the event should be consumed ie not passed on.
     */
    protected boolean onAllFingersUp(float x, float y)
    {
        return false;
    }

}
