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

public class MainLayout extends LinearLayout {


    private ObjectAnimator anim1, anim2;

    private int layoutOffset = 0;

    private double percentSmall = 0.2;

    private MainLayoutListener listener;

    public MainLayout(Context context) {
        super(context);
    }

    public MainLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();    //To change body of overridden methods use File | Settings | File Templates.
        init();
    }

    private void init() {
        percentSmall = ((LayoutParams) getChildAt(0).getLayoutParams()).weight / getWeightSum();

        int width = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
        int a1Start = 0, a1End = (int) (percentSmall * width);
        int a2Start = a1End, a2End = width;

        anim1 = ObjectAnimator.ofInt(this, "layoutOffset", a1Start, a1End);
        anim2 = ObjectAnimator.ofInt(this, "layoutOffset", a2Start, a2End);
        anim1.setDuration(100);
        anim2.setDuration(250);


        listener = new MainLayoutListener();

        anim1.addListener(listener);
        anim1.addUpdateListener(listener);
        anim2.addListener(listener);
        anim2.addUpdateListener(listener);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = listener.onTouch(this, ev);
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heighSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);


        int leftWMS = MeasureSpec.makeMeasureSpec((int) (width * percentSmall), MeasureSpec.EXACTLY);
        int rightWMS = MeasureSpec.makeMeasureSpec((int) (width * (1 - percentSmall)), MeasureSpec.EXACTLY);

        getChildAt(0).measure(leftWMS, heighSpec);
        getChildAt(2).measure(leftWMS, heighSpec);

        getChildAt(1).measure(rightWMS, heighSpec);
        getChildAt(3).measure(rightWMS, heighSpec);

        setMeasuredDimension(2 * width, height);

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View c0 = getChildAt(0);
        View c1 = getChildAt(1);
        View c2 = getChildAt(2);
        View c3 = getChildAt(3);

        int offset0 = c0.getMeasuredWidth();
        int offset1 = offset0 + c1.getMeasuredWidth();
        int offset2 = offset1 + c2.getMeasuredWidth();
        int offset3 = offset2 + c3.getMeasuredWidth();

        c0.layout(-layoutOffset, 0, offset0 - layoutOffset, c0.getMeasuredHeight());
        c1.layout(offset0 - layoutOffset, 0, offset1 - layoutOffset, c1.getMeasuredHeight());
        c2.layout(offset1 - layoutOffset, 0, offset2 - layoutOffset, c2.getMeasuredHeight());
        c3.layout(offset2 - layoutOffset, 0, offset3 - layoutOffset, c2.getMeasuredHeight());

    }

    private class MainLayoutListener extends EpicTouchListener implements ValueAnimator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

        protected MainLayoutListener() {

        }

        private boolean isAnimating = false;

        @Override
        protected boolean onThreeFingerSwipe(float dX, float dY) {
            if (isAnimating || Math.abs(dX) < 5) return false;

            View c0 = getChildAt(0);
            View c1 = getChildAt(1);

            int offset0 = c0.getMeasuredWidth();
            int offset1 = offset0 + c1.getMeasuredWidth();


            if (Math.abs(dX) > 1.5 * Math.abs(dY)) {
                if (layoutOffset == 0) {
                    if (dX < 0) {
                        anim1.start();
                        return true;
                    }
                } else if (Math.abs(layoutOffset - offset0) < 2) {
                    if (dX < 0) anim2.start();
                    else anim1.reverse();
                    return true;
                } else if (Math.abs(layoutOffset - offset1) < 2) {
                    if (dX > 0) {
                        anim2.reverse();
                        return true;
                    }
                }

            }
            return false;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            requestLayout();
            invalidate();
        }

        @Override
        public void onAnimationStart(Animator animator) {
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            isAnimating = false;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            isAnimating = false;
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public int getLayoutOffset() {
        return layoutOffset;
    }

    public void setLayoutOffset(int layoutOffset) {
        this.layoutOffset = layoutOffset;
    }

    public double getPercentSmall() {
        return percentSmall;
    }

    public void setPercentSmall(double percentSmall) {
        this.percentSmall = percentSmall;
    }
}
