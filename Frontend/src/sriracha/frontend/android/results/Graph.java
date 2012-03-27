package com.example;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public class Graph extends FrameLayout
{

    private Axis yAxis;
    
    private Axis xAxis;


    public Graph(Context context) {
        super(context);
        init();
    }

    public Graph(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public Graph(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {

        int y0 = yAxis.pixelsFromCoordinate(0);
        int x0 = xAxis.pixelsFromCoordinate(0);

        xAxis.setAxisOffset(y0);
        yAxis.setAxisOffset(x0);

        int yleft = yAxis.getEdgeOffset();

        yAxis.layout(yleft, 0, yleft + yAxis.getMeasuredWidth(), yAxis.getMeasuredHeight());

        int xtop = xAxis.getEdgeOffset();

        xAxis.layout(0, xtop, xAxis.getMeasuredWidth(), xtop + xAxis.getMeasuredHeight());

    }


    private void init(){
        yAxis = new Axis(getContext());
        addView(yAxis, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        yAxis.setOrientation(LinearLayout.VERTICAL);
        xAxis = new Axis(getContext());
        addView(xAxis, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        xAxis.setOrientation(LinearLayout.HORIZONTAL);

        setOnTouchListener(new GraphGestureListener());


    }



    private class GraphGestureListener extends EpicTouchListener{

        @Override
        public boolean onSingleFingerMove(float distanceX, float distanceY)
        {
            double xPush = distanceX*xAxis.getPCR();
            double yPush = distanceY*yAxis.getPCR();
            yAxis.setRange(yAxis.getMinValue() + yPush, yAxis.getMaxValue() + yPush);
            xAxis.setRange(xAxis.getMinValue() - xPush, xAxis.getMaxValue() - xPush);
            requestLayout();

            return true;
        }
    }



    public void setYRange(double min, double max){
        yAxis.setRange(min, max);
        requestLayout();
    }

    public void setXRange(double min, double max){
        xAxis.setRange(min, max);
        requestLayout();
    }

    public double getYmin() {
        return yAxis.getMinValue();
    }

    public void setYmin(double ymin) {
        yAxis.setMinValue(ymin);
        requestLayout();
    }

    public double getYmax() {
        return yAxis.getMaxValue();
    }

    public void setYmax(double ymax) {
        yAxis.setMaxValue(ymax);
        requestLayout();
    }

    public double getXmin() {
        return xAxis.getMinValue();
    }

    public void setXmin(double xmin) {
        xAxis.setMinValue(xmin);
        requestLayout();
    }

    public double getXmax() {
        return xAxis.getMaxValue();
    }

    public void setXmax(double xmax) {
        xAxis.setMaxValue(xmax);
        requestLayout();
    }
}
