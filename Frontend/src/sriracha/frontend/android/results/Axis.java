package sriracha.frontend.android.results;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import sriracha.frontend.R;

import java.text.DecimalFormat;


public class Axis extends LinearLayout {

    private static int lineSpace = 20;

    private int axisOffset;
    
    private int edgeOffset;

    private boolean logscale;

    private int logbase;

    /**
     * Side of the axis the label is on 0 is (left, top), 1 is (right, bottom).
     */
    private int labelSide;

    //minimum value for axis
    private double minValue;

    private double maxValue;

    /**
     * Represents either the total label Count, for a linear axis or
     * the number of labels per
     */
    protected int labelCount;

    protected Paint linePaint;

    protected LayoutInflater inflater;

    public Axis(Context context) {
        super(context);
        init();
    }

    public Axis(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public Axis(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected TextView getLabel(int i){
        return (TextView)getChildAt(i);
    }

    private int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }

    
    public void setAxisOffset(int axisOffset){
        this.axisOffset = axisOffset;

        updateLabelsSide();
        updateEdgeOffset();
    }

    public int getEdgeOffset()
    {
        return edgeOffset;
    }

    private void updateEdgeOffset(){
        ViewGroup parent = (ViewGroup) getParent();

        int max = getOrientation() == VERTICAL ? parent.getWidth() - getWidth() :  parent.getHeight() - getHeight();

        int edgeDist = lineSpace / 2;

        if (getOrientation() == VERTICAL) {
            int left = labelSide == 0 ? axisOffset - (getWidth() - edgeDist) : axisOffset - edgeDist;
            edgeOffset = clamp(left, 0, max);
        } else {
            int top = labelSide == 0 ? axisOffset - (getHeight() - edgeDist) : axisOffset - edgeDist;
            edgeOffset = clamp(top, 0, max);
        }
    }


    private void updateLabelAttributes(){
        for (int i = 0; i < getChildCount(); i++) {
            TextView view = (TextView) getChildAt(i);
            if (getOrientation() == VERTICAL) {
                view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
                view.setGravity(labelSide == 0 ? Gravity.RIGHT | Gravity.CENTER_VERTICAL :
                        Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else {
                view.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
                view.setGravity(labelSide == 0 ? Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL :
                        Gravity.TOP | Gravity.CENTER_HORIZONTAL );
            }
        }
    }

    /**
     * Changes the labels from one side of the axis line to the other
     */
    private void updateLabelsSide() {
        ViewGroup parent = (ViewGroup) getParent();
        int mid = getOrientation() == VERTICAL ? parent.getWidth() / 2 :  parent.getHeight() / 2;
        int side =  getOrientation() == VERTICAL ? axisOffset > mid ? 1 : 0 : axisOffset >= mid ? 1 : 0;
        if(labelSide != side){
            labelSide = side;
            updateLabelAttributes();
        }

    }



    private void init() {
        setWillNotDraw(false);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        labelCount = 8;//default value
        linePaint = new Paint();
        linePaint.setARGB(255, 255, 255, 255);
        linePaint.setStrokeWidth(2);
        minValue = -1000;
        maxValue = 1000;
        logscale = false;
        logbase = 10;
        for (int i = 0; i < labelCount; i++) {
            inflateLabel();
        }

        updateLabelContents();

        updateLabelAttributes();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getOrientation() == VERTICAL)
            setMeasuredDimension(getMeasuredWidth() + lineSpace, getMeasuredHeight());
        else
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + lineSpace);
    }


    @Override
    public void setOrientation(int orientation)
    {
        if(getOrientation() != orientation){
            super.setOrientation(orientation);
            updateLabelContents();
            updateLabelAttributes();
            requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int width = r - l;
        int height = b - t;
        
        if(getOrientation() == VERTICAL){
            int space =  height / getChildCount();
            int left = labelSide == 0 ? 0 : lineSpace;
            for(int i =0; i<getChildCount(); i++){
                TextView label = getLabel(i);
                int top = i*space;
                label.layout(left, top, left + width - lineSpace, top + label.getMeasuredHeight());
            }
        }else{
            int space =  width / getChildCount();
            int top = labelSide == 0 ? 0 : lineSpace;
            for(int i =0; i<getChildCount(); i++){
                TextView label = getLabel(i);
                int left = i*space;
                label.layout(left, top, left + label.getMeasuredWidth(), top + height - lineSpace);
            }
        }

        
    }


    public void setRange(double minValue, double maxValue) {
        if (minValue != this.minValue || maxValue != this.maxValue) {
            this.maxValue = maxValue;
            this.minValue = minValue;
            updateLabelContents();
            requestLayout();
        }
    }

    private void updateLabelContents() {
        double interval = (maxValue - minValue) / (labelCount);
        for (int i = 0; i < labelCount; i++) {
            int index = getOrientation() == VERTICAL ? labelCount - (i+1) : i;
            getLabel(index).setText(axisNumFormat(minValue + interval * (i+0.5)));
        }
    }

    private void inflateLabel() {
        inflater.inflate(R.layout.results_axis_label, this, true);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);   
        
        //draw line:
        int sx=0, sy=0, ex=0, ey=0;
        if(getOrientation() == VERTICAL){
            sy = 0;
            ey = getHeight();
            sx = ex = labelSide == 0 ? getWidth() - lineSpace/2 : lineSpace/2;
        }else{
            sx = 0;
            ex = getWidth();
            sy = ey = labelSide == 0 ? getHeight() - lineSpace/2 : lineSpace/2;
        }
        canvas.drawLine(sx, sy, ex, ey, linePaint);
        
        
        int nlen = (lineSpace -4)/2;
        //draw notches vis-a-vis the labels
        for(int i = 0; i < getChildCount(); i++){
            TextView label = getLabel(i);
            if(getOrientation() == VERTICAL){
                sx = labelSide == 0 ? getWidth() - lineSpace + (lineSpace - nlen)/2 : (lineSpace - nlen)/2;
                ex = sx + nlen;
                sy = ey = label.getTop() + label.getHeight()/2;
            }else {
                sy =  labelSide == 0 ? getHeight() - lineSpace + (lineSpace - nlen)/2 : (lineSpace - nlen)/2;
                ey = sy + nlen;
                sx = ex = label.getLeft() + label.getWidth()/2;
            }
            canvas.drawLine(sx, sy, ex, ey, linePaint);
        }
        

    }




    public void setLabelCount(int labelCount) {
        int diff = labelCount - this.labelCount;

        if (diff < 0) {
            while (diff++ < 0) {
                removeViewAt(-diff);
            }
        } else if (diff > 0) {
            while (diff-- > 0) {
                inflateLabel();
            }
        }


        this.labelCount = labelCount;

        updateLabelContents();
        requestLayout();
    }

    protected static String axisNumFormat(double val) {
        if (val <= 1000 && val >= -1000 && Math.abs(val) > 1./1000.) {
            DecimalFormat format = new DecimalFormat("#.##");
            return format.format(val);
        }
        DecimalFormat format = new DecimalFormat("0.00E00");
        return format.format(val);
    }

    /**
     * returns pixel offset from the start of this control to the location
     * corresponding to the axis value requested.
     *
     * @param axisValue value you want to find the pixel offset for
     * @return pixel offset from start (left or top)
     */
    public float pixelsFromCoordinate(double axisValue) {
        double percnt = (axisValue-minValue)/(maxValue - minValue);
        if(getOrientation() == VERTICAL) percnt = 1-percnt;
        return (float) (percnt * getPixelRange());
    }

    /**
     * returns the coordinate corresponding to pixel offset from the start
     * of this control to the location requested.
     *
     * @param pixelValue offset value you want to find the coordinate for
     * @return corresponding axis value
     */
    public double coordinateFromPixel(float pixelValue) {
        double range = maxValue - minValue;
        double percnt = pixelValue / getPixelRange();
        if(getOrientation() == VERTICAL) percnt = 1-percnt;

        return percnt * range + minValue;
    }

    private double getPixelRange(){
        return getOrientation() == VERTICAL ? getHeight()  : getWidth();
    }

    /**
     *
     * @return Pixel to coordinate ratio
     */
    public double getPCR(){
        return (maxValue - minValue) / getPixelRange();
    }


    public boolean isAlignedStart(){
        return getOrientation() == VERTICAL ? getLeft() == 0 : getTop() == 0;
    }

    public boolean isAlignedEnd(){
        ViewGroup parent = (ViewGroup)getParent();
        return getOrientation() == VERTICAL ? getRight() == parent.getWidth() : getBottom() == parent.getHeight();
    }

    public double getMinValue()
    {
        return minValue;
    }

    public void setMinValue(double minValue)
    {
        setRange(minValue, maxValue);
    }

    public double getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(double maxValue)
    {
        setRange(minValue, maxValue);
    }

    //todo: invalidate proper stuff
    public boolean isLogscale()
    {
        return logscale;
    }

    //todo: invalidate proper stuff
    public void setLogscale(boolean logscale)
    {
        this.logscale = logscale;
    }

    public int getLogbase()
    {
        return logbase;
    }

    public int getAxisOffset()
    {
        return axisOffset;
    }

    //todo: invalidate proper stuff
    public void setLogbase(int logbase)
    {
        this.logbase = logbase;
    }
}
