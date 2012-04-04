package sriracha.frontend.android;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 03/04/12
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DebugListener extends EpicTouchListener
{

    /**
     * Called whenever there is only 1 finger on the screen and that it has moved.
     *
     * @param dX     X distance moved since last call to this method
     * @param dY     Y distance moved since last call to this method
     * @param finalX X resting place of finger after movement
     * @param finalY Y resting place of finger after movement
     * @return true if the the event should be consumed ie not passed on.
     */
    @Override
    protected boolean onSingleFingerMove(float dX, float dY, float finalX, float finalY) {
       System.out.println("onSingleFingerMove dX: "+dX+" dY: "+dY+" finalX: "+finalX+" finalY: " + finalY);
        return true;
    }

    /**
     * Called whenever there are 2 fingers on the screen and that they have moved
     * roughly in parallel
     *
     * @param dX average X distance over both fingers
     * @param dY average Y distance over both fingers
     * @return true if the the event should be consumed ie not passed on.
     */
    @Override
    protected boolean onTwoFingerSwipe(float dX, float dY) {
        System.out.println("onTwoFingerSwipe dX: "+dX+" dY: "+dY);
        return true;
    }

    /**
     * Called whenever A single finger first touches the screen.
     * (no other fingers can already be touching)
     *
     * @param x x position
     * @param y y position
     */
    @Override
    protected void onSingleFingerDown(float x, float y) {
        System.out.println("onSingleFingerDown x: "+x+" y: "+y);

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
    @Override
    protected boolean onScale(float xFactor, float yFactor, float xCenter, float yCenter) {
        System.out.println("onScale xFactor: "+xFactor+" yFactor: "+yFactor + " xCenter: " + xCenter + " yCenter: " + yCenter);
        return true;
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
    @Override
    protected boolean onTwoFingerMove(float dX1, float dY1, float dX2, float dY2) {
        System.out.println("onTwoFingerMove dX1: "+dX1+" dY1: "+dY1 + " dX2: " + dX2 + " dY2: " + dY2);

        return super.onTwoFingerMove(dX1, dY1, dX2, dY2);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Called When the Second finger first touches down on the screen
     *
     * @param x1 x position of first finger
     * @param y1 y position of first finger
     * @param x2 x position of second finger
     * @param y2 y position of second finger
     */
    @Override
    protected void onTwoFingerDown(float x1, float y1, float x2, float y2) {
        System.out.println("onTwoFingerDown x1: " + x1 + " y1: " + y1 + " x2: " +x2+ " y2: " + y2);
        super.onTwoFingerDown(x1, y1, x2, y2);    //To change body of overridden methods use File | Settings | File Templates.
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
    @Override
    protected void onThreeFingerDown(float x1, float y1, float x2, float y2, float x3, float y3) {
        System.out.println("onThreeFingerDown x1: " + x1 + " y1: " + y1 + " x2: " +x2+ " y2: " + y2 + " x3: " +x3+ " y3: " + y3);
        super.onThreeFingerDown(x1, y1, x2, y2, x3, y3);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
