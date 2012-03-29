package sriracha.frontend.android;

import android.view.MotionEvent;
import android.view.View;

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
    protected double scaleTolerance = 0.2;

    /**
     * a value between 0 and 1
     * 0 -> only perfectly parallel 2 finger motions are considered a swipe gesture
     * 1 -> any 2 finger motion is a swipe gesture
     */
    protected double swipeTolerance = 0.2;

    private float x1, y1, x2, y2;

    private int id1, id2;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        int index = motionEvent.getActionIndex();
        switch (motionEvent.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {



                if(motionEvent.getPointerCount() == 1){
                    x1 = motionEvent.getX(index);
                    y1 = motionEvent.getY(index);
                    id1 = motionEvent.getPointerId(index);

                    onSingleFingerDown(x1, y1);
                }
                
                
                //always return true for ACTION_DOWN so that we get subsequent ACTION_MOVE events
                return true;
                
            }
            case MotionEvent.ACTION_POINTER_DOWN:
            {
                if (motionEvent.getPointerCount() == 2){
                    x2 = motionEvent.getX(index);
                    y2 = motionEvent.getY(index);
                    id2 = motionEvent.getPointerId(index);

                    onTwoFingerDown(x1, y1, x2, y2);

                }
                return true;
            }
            case MotionEvent.ACTION_POINTER_UP:
            {
                int id = motionEvent.getPointerId(index);

                if(id == id1){
                    id1 = id2;
                    x1 = x2;
                    y1 = y2;
                }
            }
            case MotionEvent.ACTION_MOVE:
            {

                if(motionEvent.getPointerCount() == 1){
                    float oldx = x1, oldy = y1;
                    x1 = motionEvent.getX(index);
                    y1 = motionEvent.getY(index);
                    return onSingleFingerMove(x1 - oldx, y1 - oldy);
                } else if(motionEvent.getPointerCount() == 2){

                    float oldx1 = x1, oldx2 = x2, oldy1 = y1, oldy2 = y2;
                   
                    int i1 = motionEvent.findPointerIndex(id1), i2 = motionEvent.findPointerIndex(id2);
                   
                    x1 = motionEvent.getX(i1);
                    y1 = motionEvent.getY(i1);
                    y2 = motionEvent.getY(i2);
                    x2 = motionEvent.getX(i2);
                    
                    
                    float dX1 = x1 - oldx1, dX2 = x2 - oldx2, dY1 = y1 - oldy1, dY2 = y2 - oldy2;
                    
                    double angle1 = centerRad(Math.atan2(dY1, dX1)), angle2 = centerRad(Math.atan2(dY2, dX2));
                    boolean isScale = Math.abs(centerRad(angle1 - angle2)) >= (1-scaleTolerance) * Math.PI;
                    boolean isSwipe = Math.abs(centerRad(angle1 - angle2)) <= swipeTolerance * Math.PI;
                    
                    boolean consumed = false;
                    
                    if(isScale){
                        float xFactor =  Math.abs((x2 - x1) / (oldx2 - oldx1)), yFactor = Math.abs((y2 - y1) / (oldy2 - oldy1));
                        consumed |= onScale(xFactor, yFactor);
                    }
                    
                    if(isSwipe){
                        consumed |= onTwoFingerSwipe((dX1 + dX2)/2, (dY1 + dY1)/2);
                    }
                    
                    if(!consumed) {
                        return onTwoFingerMove(dX1, dY1, dX2, dY2);
                    }

                    return consumed;
                }

                break;
            }
        }

        return false;
    }
    
    
    private double centerRad(double radians){
        radians = radians % (2* Math.PI);
        if(radians < -Math.PI) return radians + 2 * Math.PI;
        if(radians > Math.PI) return radians - 2 * Math.PI;
        return radians;
        
    }

    /**
     * Called whenever there is only 1 finger on the screen and that it has moved.
     * @param dX X distance moved since last call to this method
     * @param dY Y distance moved since last call to this method
     * @return true if the the event should be consumed ie not passed on.
     */
    protected boolean onSingleFingerMove(float dX, float dY) {return false;}

    /**
     * Called whenever there are 2 fingers on the screen and that they have moved
     * roughly in parallel
     * @param dX average X distance over both fingers
     * @param dY average Y distance over both fingers
     * @return true if the the event should be consumed ie not passed on.
     */
    protected boolean onTwoFingerSwipe(float dX, float dY) {return false;}
    
    


    /**
     * Called whenever A single finger first touches the screen.
     * (no other fingers can already be touching)
     * @param x x position
     * @param y y position
     */
    protected void onSingleFingerDown(float x, float y){}

    /**
     * Called if the user has used a pinch-zoom like gesture.
     * This call is followed by a call to OnTwoFingerMove if it returns false
     * @param xFactor scaling factor along x axis
     * @param yFactor scaling factor along y axis
     * @return true if the the event should be consumed ie not passed on.
     */
    protected boolean onScale(float xFactor, float yFactor) {return false;}


    /**
     * This is invoked when there are exactly two fingers touching and at at least one of them has moved.
     * This method is only called if a preceding call to a more specific 2 finger gesture handler returns true
     * for example:
     * @see #onScale(float, float) 
     * @see #onTwoFingerSwipe(float, float) 
     *
     * @param dX1 x delta from first finger
     * @param dY1 y delta from first finger
     * @param dX2 x delta from second finger
     * @param dY2 y delta from second finger
     * @return true if the the event should be consumed ie not passed on.
     */
    protected boolean onTwoFingerMove(float dX1, float dY1, float dX2, float dY2) { return false;}

    /**
     * Called When the Second finger first touches down on the screen
     * @param x1 x position of first finger
     * @param y1 y position of first finger
     * @param x2 x position of second finger
     * @param y2 y position of second finger
     */
    protected void onTwoFingerDown(float x1, float y1, float x2, float y2) { }


}
