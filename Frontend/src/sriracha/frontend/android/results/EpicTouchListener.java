package com.example;

import android.view.MotionEvent;
import android.view.View;

public abstract class EpicTouchListener implements View.OnTouchListener
{
    


   private float sfx, sfy;


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        switch (motionEvent.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {

                if(motionEvent.getPointerCount() == 1){
                    sfx = motionEvent.getX();
                    sfy = motionEvent.getY();

                    if(sfx == 0 && sfy == 0){
                        int jj = 34;
                        jj++;
                    }


                    onSingleFingerDown(sfx, sfy);
                    return true;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE:
            {

                if(motionEvent.getPointerCount() == 1){

                    if(sfy == 0 && sfx == 0){
                        int jj = 34;
                        jj++;
                    }

                    float oldx = sfx, oldy = sfy;
                    sfx = motionEvent.getX();
                    sfy = motionEvent.getY();
                    return onSingleFingerMove(sfx - oldx, sfy - oldy);
                }

                break;
            }
        }

        return false;
    }

    protected boolean onSingleFingerMove(float distanceX, float distanceY) {return false;}

    protected boolean onSingleFingerDown(float x, float y){return false;}


}
