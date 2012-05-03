package sriracha.frontend.android;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import sriracha.frontend.R;
import sriracha.frontend.android.results.Graph;
import sriracha.frontend.android.results.GraphController;

public class MainLayout extends LinearLayout
{


    private ObjectAnimator anim1, anim2, anim3;

    private int layoutOffset = 0;

    private double percentSmall = 0.2;

    private MainLayoutListener listener;

    private boolean fingersStillDown = false;

    private boolean isAnimating = false;

    public MainLayout(Context context)
    {
        super(context);
    }


    public int getLayoutMode()
    {
        int[] offsets = getOffsets();

        if(layoutOffset == 0) return 0;

        for (int i = 0; i < offsets.length; i++)
        {
            if (Math.abs(layoutOffset - offsets[i]) < 2)
                return i+1;
        }

        return -1;

    }

    public MainLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MainLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();    //To change body of overridden methods use File | Settings | File Templates.
        init();
    }


    private void init()
    {
        percentSmall = ((LayoutParams) getChildAt(0).getLayoutParams()).weight / getWeightSum();

        int width = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
        int a1Start = 0, a1End = (int) (percentSmall * width);
        int a2Start = a1End, a2End = width;
        int a3Start = a2End, a3End = width + a1End;

        anim1 = ObjectAnimator.ofInt(this, "layoutOffset", a1Start, a1End);
        anim2 = ObjectAnimator.ofInt(this, "layoutOffset", a2Start, a2End);
        anim3 = ObjectAnimator.ofInt(this, "layoutOffset", a3Start, a3End);
        anim1.setDuration(110);
        anim2.setDuration(220);
        anim3.setDuration(110);

        GraphController gController = (GraphController) findViewById(R.id.tab_graph);
        gController.setGraph((Graph) findViewById(R.id.graph));

        listener = new MainLayoutListener();

        anim1.addListener(listener);
        anim1.addUpdateListener(listener);
        anim2.addListener(listener);
        anim2.addUpdateListener(listener);
        anim3.addListener(listener);
        anim3.addUpdateListener(listener);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        boolean ret = listener.onTouch(this, ev);
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heighSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);


        int leftWMS = MeasureSpec.makeMeasureSpec((int) (width * percentSmall), MeasureSpec.EXACTLY);
        int rightWMS = MeasureSpec.makeMeasureSpec((int) (width * (1 - percentSmall)), MeasureSpec.EXACTLY);

        getChildAt(0).measure(leftWMS, heighSpec);
        getChildAt(2).measure(leftWMS, heighSpec);
        getChildAt(4).measure(leftWMS, heighSpec);

        getChildAt(1).measure(rightWMS, heighSpec);
        getChildAt(3).measure(rightWMS, heighSpec);

        setMeasuredDimension((int) ((2 + percentSmall) * width), height);

    }


    private int[] getOffsets()
    {
        View c0 = getChildAt(0);
        View c1 = getChildAt(1);
        View c2 = getChildAt(2);
        View c3 = getChildAt(3);
        View c4 = getChildAt(4);

        int offset0 = c0.getMeasuredWidth();
        int offset1 = offset0 + c1.getMeasuredWidth();
        int offset2 = offset1 + c2.getMeasuredWidth();
        int offset3 = offset2 + c3.getMeasuredWidth();
        int offset4 = offset3 + c4.getMeasuredWidth();

        return new int[]{offset0, offset1, offset2, offset3, offset4};

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        View c0 = getChildAt(0);
        View c1 = getChildAt(1);
        View c2 = getChildAt(2);
        View c3 = getChildAt(3);
        View c4 = getChildAt(4);

        int offset0 = c0.getMeasuredWidth();
        int offset1 = offset0 + c1.getMeasuredWidth();
        int offset2 = offset1 + c2.getMeasuredWidth();
        int offset3 = offset2 + c3.getMeasuredWidth();
        int offset4 = offset3 + c4.getMeasuredWidth();

        c0.layout(-layoutOffset, 0, offset0 - layoutOffset, c0.getMeasuredHeight());
        c1.layout(offset0 - layoutOffset, 0, offset1 - layoutOffset, c1.getMeasuredHeight());
        c2.layout(offset1 - layoutOffset, 0, offset2 - layoutOffset, c2.getMeasuredHeight());
        c3.layout(offset2 - layoutOffset, 0, offset3 - layoutOffset, c3.getMeasuredHeight());
        c4.layout(offset3 - layoutOffset, 0, offset4 - layoutOffset, c4.getMeasuredHeight());

    }

    public boolean shiftLeft()
    {
        View c0 = getChildAt(0);
        View c1 = getChildAt(1);
        View c2 = getChildAt(2);

        int offset0 = c0.getMeasuredWidth();
        int offset1 = offset0 + c1.getMeasuredWidth();
        int offset2 = offset1 + c2.getMeasuredWidth();

        if (Math.abs(layoutOffset - offset0) < 2)
        {
            anim1.reverse();
            return true;
        }
        if (Math.abs(layoutOffset - offset1) < 2)
        {
            anim2.reverse();
//            tabHost.setCurrentTab(0);
            return true;
        }
        if (Math.abs(layoutOffset - offset2) < 2)
        {
            anim3.reverse();
        }
        return false;
    }

    public boolean shiftRight()
    {
        View c0 = getChildAt(0);
        View c1 = getChildAt(1);
        int offset0 = c0.getMeasuredWidth();
        int offset1 = offset0 + c1.getMeasuredWidth();


        if (layoutOffset == 0)
        {
            anim1.start();
            return true;
        }
        if (Math.abs(layoutOffset - offset0) < 2)
        {
            anim2.start();
//            tabHost.setCurrentTab(1);
            return true;
        }
        if (Math.abs(layoutOffset - offset1) < 2)
        {
            anim3.start();
            return true;
        }


        return false;
    }

    private class MainLayoutListener extends EpicTouchListener implements ValueAnimator.AnimatorListener, ValueAnimator.AnimatorUpdateListener
    {

        protected MainLayoutListener()
        {

        }

        @Override
        protected boolean onAllFingersUp(float x, float y)
        {
            fingersStillDown = false;
            return false;
        }

        @Override
        protected boolean onThreeFingerSwipe(float dX, float dY)
        {
            if (isAnimating || fingersStillDown) return false;

            boolean shifted = false;

            if (Math.abs(dX) > 2 * Math.abs(dY))
            {
                if (dX < 0)
                    shifted |= shiftRight();
                else
                    shifted |= shiftLeft();

            }

            fingersStillDown = shifted;

            return shifted;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator)
        {
            requestLayout();
            invalidate();
        }

        @Override
        public void onAnimationStart(Animator animator)
        {
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animator animator)
        {
            isAnimating = false;
        }

        @Override
        public void onAnimationCancel(Animator animator)
        {
            isAnimating = false;
        }

        @Override
        public void onAnimationRepeat(Animator animator)
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public int getLayoutOffset()
    {
        return layoutOffset;
    }

    public void setLayoutOffset(int layoutOffset)
    {
        this.layoutOffset = layoutOffset;
    }

    public double getPercentSmall()
    {
        return percentSmall;
    }

    public void setPercentSmall(double percentSmall)
    {
        this.percentSmall = percentSmall;
    }
}
