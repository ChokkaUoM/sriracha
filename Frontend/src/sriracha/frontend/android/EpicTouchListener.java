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
    protected double scaleTolerance = 0.2;

    /**
     * a value between 0 and 1
     * 0 -> only perfectly parallel 2 finger motions are considered a swipe gesture
     * 1 -> any 2 finger motion is a swipe gesture
     */
    protected double swipeTolerance = 0.2;
    
    private ArrayList<Finger> activeFingers;
    
    private static class Finger{       
        int id;
        
        float x;
        
        float y;             
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
                
                
                switch (motionEvent.getPointerCount()){
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
            case MotionEvent.ACTION_POINTER_UP:
            {
                int deadIndex = -1;
                for(int i =0; i< activeFingers.size(); i++)
                {
                    if(activeFingers.get(i).id == actionId) 
                    {
                        deadIndex = i;
                        break;
                    }
                }
                
                activeFingers.remove(deadIndex);                        
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {

                switch (motionEvent.getPointerCount()){
                    case 1:
                    {
                        Finger f = activeFingers.get(0);
                        float oldX = f.x, oldY = f.y;
                        f.x = motionEvent.getX(actionIndex);
                        f.y = motionEvent.getY(actionIndex);
                        return onSingleFingerMove(f.x - oldX, f.y - oldY);
                    }
                    case 2:
                    {
                        Finger f1 = activeFingers.get(0);
                        Finger f2 = activeFingers.get(1);
                        
                        
                        float oldx1 = f1.x, oldx2 = f2.x, oldy1 = f1.y, oldy2 = f2.y;

                        int i1 = motionEvent.findPointerIndex(f1.id), i2 = motionEvent.findPointerIndex(f2.id);

                        f1.x = motionEvent.getX(i1);
                        f1.y = motionEvent.getY(i1);
                        f2.x = motionEvent.getX(i2);
                        f2.y = motionEvent.getY(i2);


                        float dX1 = f1.x - oldx1, dX2 = f2.x - oldx2, dY1 = f1.y - oldy1, dY2 = f2.y - oldy2;
                        
                        double angleDiff = centerRad(Math.atan2(dY1, dX1) - Math.atan2(dY2, dX2));

                        boolean isScale = Math.abs(angleDiff) >= (1-scaleTolerance) * Math.PI;
                        boolean isSwipe = Math.abs(angleDiff) <= swipeTolerance * Math.PI;

                        
                        float oldDX = oldx2 - oldx1, oldDY = oldy2 - oldy1, dx =f2.x-f1.x, dy = f2.y - f1.y;
                        
                        
                        boolean consumed = false;
                        if(isScale){
                            float xFactor = clamp(Math.abs(oldDX / dx), 0.1f, 10),
                                  yFactor = clamp(Math.abs(oldDY / dy), 0.1f, 10);
                            
                           // xFactor = (float) Math.sqrt(xFactor);
                           // yFactor = (float) Math.sqrt(yFactor);
                            
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
                    case 3:
                    {//todo: extend to more fingers
                        break;
                    }
                    
                }
                break;
            }
        }

        return false;
    }
    
    private Finger addFinger(int id, float x, float y)
    {
        Finger f = new Finger();
        f.id = id;
        f.x = x;
        f.y = y;
        activeFingers.add(f);
        return f;
    }
    
    private float clamp(float value, float min, float max)
    {
        return value < min ? min : value > max ? max : value;
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

    /**
     * Called When the Second finger first touches down on the screen
     * @param x1 x position of first finger
     * @param y1 y position of first finger
     * @param x2 x position of second finger
     * @param y2 y position of second finger
     * @param x3 x position of third finger
     * @param y3 y position of third finger
     */
    protected void onThreeFingerDown(float x1, float y1, float x2, float y2, float x3, float y3) { }


}
