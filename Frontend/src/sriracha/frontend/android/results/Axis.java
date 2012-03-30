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
import java.util.ArrayList;


class Axis extends LinearLayout
{
    
    static final int LOGSCALE = 1, LINEARSCALE = 0;

    /**
     * Linear Vertical Notch Space
     * space in pixels between each axis notch for
     * linear Vertical Axis
     */
    protected int linVNS = 80;

    /**
     * Linear Horizontal Notch Space
     * space in pixels between each axis notch for
     * linear Horizontal Axis
     */
    protected int linHNS = 120;

    /**
     * Logarithmic Vertical Decade Space
     * space in pixels between each decade notch fo
     * log scale Vertical Axis
     */
    protected int logVDS = 150;

    /**
     * Logarithmic Horizontal Decade Space
     * space in pixels between each decade notch fo
     * log scale Horizontal Axis
     */
    protected int logHDS = 270;

    private int preAxisLabelCount, postAxisLabelCount;
    //number of pixels available along the orientation
    private float pixelRange;
    
    //number of pixels in the graph perpendicular to this axis
    private int graphCrossSpace;
    
    private static int lineSpace = 20;

    private int axisOffset;

    private int edgeOffset;

    private int scaleType;

    private int logBase;


    /**
     * Side of the axis the label is on 0 is (left, top), 1 is (right, bottom).
     */
    private int labelSide;

    private double edgeProximity = 0.5;

    //minimum value for axis
    private double minValue;

    private double maxValue;

    protected Paint linePaint;

    protected LayoutInflater inflater;

    public Axis(Context context)
    {
        super(context);
        init();
    }

    public Axis(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }


    public Axis(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    protected TextView getLabel(int i)
    {
        return (TextView) getChildAt(i);
    }

    private int clamp(int value, int min, int max)
    {
        return value < min ? min : (value > max ? max : value);
    }

    public int getEdgeOffset()
    {
        return edgeOffset;
    }

    private void updateEdgeOffset()
    {
        int max = getOrientation() == VERTICAL ? graphCrossSpace - getMeasuredWidth() : graphCrossSpace - getMeasuredHeight();

        int edgeDist = lineSpace / 2;

        if (getOrientation() == VERTICAL)
        {
            int left = labelSide == 0 ? axisOffset - (getMeasuredWidth() - edgeDist) : axisOffset - edgeDist;
            edgeOffset = clamp(left, 0, max);
        }
        else
        {
            int top = labelSide == 0 ? axisOffset - (getMeasuredHeight() - edgeDist) : axisOffset - edgeDist;
            edgeOffset = clamp(top, 0, max);
        }
    }


    private void updateLabelAttributes()
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            TextView view = (TextView) getChildAt(i);
            if (getOrientation() == VERTICAL)
            {
                view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
                view.setGravity(labelSide == 0 ? Gravity.RIGHT | Gravity.CENTER_VERTICAL :
                                        Gravity.LEFT | Gravity.CENTER_VERTICAL);
            }
            else
            {
                view.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
                view.setGravity(labelSide == 0 ? Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL :
                                        Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    /**
     * Changes the labels from one side of the axis line to the other
     */
    private void updateLabelsSide()
    {
        float mid = graphCrossSpace / 2;
        int side = getOrientation() == VERTICAL ? axisOffset > mid ? 1 : 0 : axisOffset >= mid ? 1 : 0;
        if (labelSide != side)
        {
            labelSide = side;
        }

    }

    /**
     * pans the axis by the specified number of pixels
     * by changing the max and min values of the axis
     *
     * @param pixels number of pixels to pan
     */
    public void pan(float pixels)
    {
        if (scaleType == LOGSCALE)
        {
            //todo: pan the decades
        }
        else
        {
            double push = pixels * (maxValue - minValue) / pixelRange;
            setRange(minValue + push, maxValue + push);
        }
    }


    private void updateLabels()
    {
        
        updateLabelCount();

        updateLabelContents();
        
        updateLabelsSide();
        
        updateLabelAttributes();
    }
    
    private ArrayList<Float> getNotchPositions(){
        
        double intersectCoord = scaleType == LINEARSCALE ? 0: minValue;

        ArrayList<Float> mids = new ArrayList<Float>();
        
        //distance between notches in pixels
        float spacing = scaleType == LINEARSCALE ?  getOrientation() == HORIZONTAL ? linHNS : linVNS :
                getOrientation() == HORIZONTAL ? logHDS : logVDS;
        for(float mid = pixelsFromCoordinate(intersectCoord) + spacing; mid <= pixelRange - edgeProximity*spacing; mid += spacing)
        {
            mids.add(mid);
        }

        for(float mid = pixelsFromCoordinate(intersectCoord) - spacing; mid >= edgeProximity*spacing; mid -= spacing){
            mids.add(mid);
        }
        
        
        
        
        return mids;
    }

    private void updateLabelCount()
    {
        int labelCount = getNotchPositions().size();
        //create/destroy labels according to count
        int cc = getChildCount();
        if (cc < labelCount)
        {
            while (cc++ < labelCount) inflateLabel();
        }
        else if (cc > labelCount)
        {
            while (cc-- > labelCount) removeViewAt(cc);
        }
    }

    private void init()
    {
        setWillNotDraw(false);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linePaint = new Paint();
        linePaint.setARGB(255, 255, 255, 255);
        linePaint.setStrokeWidth(2);
        minValue = -1000;
        maxValue = 1000;
        scaleType = LINEARSCALE;
        logBase = 10;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        //add space for line
        if (getOrientation() == VERTICAL)
        {
            int heightSpec = MeasureSpec.makeMeasureSpec(linVNS, MeasureSpec.EXACTLY);
            int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
            for(int i =0; i<getChildCount(); i++){
                getLabel(i).measure(widthSpec, heightSpec);
            }
            
            
            setMeasuredDimension(getMeasuredWidth() + lineSpace, getMeasuredHeight());

        }
        else
        {
            int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
            int widthSpec = MeasureSpec.makeMeasureSpec(linHNS, MeasureSpec.EXACTLY);
            for(int i =0; i<getChildCount(); i++){
                getLabel(i).measure(widthSpec, heightSpec);
            }
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + lineSpace);
            
        }
        
        
        updateEdgeOffset(); // now that we know cross section size
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        if (scaleType == LOGSCALE)
        {
            //todo:
        }
        else
        {
            onLayoutLinear(left, top, right, bottom);
        }

    }

    private void onLayoutLinear(int left, int top, int right, int bottom)
    {
        float spacing =  getOrientation() == HORIZONTAL ? linHNS : linVNS;

        int i =0;

        for(float mid : getNotchPositions())
        {
            TextView label = getLabel(i++);
            int labelStart = (int) (mid - spacing/2);
            int ltop = getOrientation() == VERTICAL ? labelStart : labelSide == 0 ? 0 : lineSpace;
            int lleft = getOrientation() == HORIZONTAL ? labelStart : labelSide == 0 ? 0 : lineSpace;
            int lwidth = getOrientation() == VERTICAL ? label.getMeasuredWidth() : linHNS;
            int lheight = getOrientation() == HORIZONTAL ? label.getMeasuredHeight() : linVNS;
            label.layout(lleft, ltop, lleft + lwidth, ltop + lheight );
        }
    }


    public void setRange(double minValue, double maxValue)
    {
        if (minValue != this.minValue || maxValue != this.maxValue)
        {
            this.maxValue = maxValue;
            this.minValue = minValue;
        }
    }


    private void updateLabelContents()
    {
        if(scaleType == LINEARSCALE)
        {
            int i =0;
            for(float mid : getNotchPositions()){
                getLabel(i++).setText(axisNumFormat(coordinateFromPixel(mid)));
            }

        }
        else   
        {
            //todo:
        }
    }

    private void inflateLabel()
    {
        inflater.inflate(R.layout.results_axis_label, this, true);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //draw line:
        int sx = 0, sy = 0, ex = 0, ey = 0;
        if (getOrientation() == VERTICAL)
        {
            sy = 0;
            ey = getHeight();
            sx = ex = labelSide == 0 ? getWidth() - lineSpace / 2 : lineSpace / 2;
        }
        else
        {
            sx = 0;
            ex = getWidth();
            sy = ey = labelSide == 0 ? getHeight() - lineSpace / 2 : lineSpace / 2;
        }
        canvas.drawLine(sx, sy, ex, ey, linePaint);


        int nlen = (lineSpace - 4) / 2;
        //draw notches vis-a-vis the labels
        
        int i =0;

        for(float mid : getNotchPositions())
        {

            if (getOrientation() == VERTICAL)
            {
                sx = labelSide == 0 ? getWidth() - lineSpace + (lineSpace - nlen) / 2 : (lineSpace - nlen) / 2;
                ex = sx + nlen;
                sy = ey = (int) mid;
            }
            else
            {
                sy = labelSide == 0 ? getHeight() - lineSpace + (lineSpace - nlen) / 2 : (lineSpace - nlen) / 2;
                ey = sy + nlen;
                sx = ex = (int) mid;
            }

            canvas.drawLine(sx, sy, ex, ey, linePaint);
        }

    }


    protected static String axisNumFormat(double val)
    {
        if (val <= 1000 && val >= -1000 && Math.abs(val) > 1. / 1000.)
        {
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
    public float pixelsFromCoordinate(double axisValue)
    {
        if (scaleType == LOGSCALE)
        {
            //todo: cannot depend on crossSize or label layout
            return -1;
        }
        else
        {
            double percent = (axisValue - minValue) / (maxValue - minValue);
            if (getOrientation() == VERTICAL) percent = 1 - percent;
            return (float) (percent * pixelRange);
        }


    }

    /**
     * returns the coordinate corresponding to pixel offset from the start
     * of this control to the location requested.
     *
     * @param pixelValue offset value you want to find the coordinate for
     * @return corresponding axis value
     */
    public double coordinateFromPixel(float pixelValue)
    {

        if (scaleType == LOGSCALE)
        {
            //todo:
            return -1;
        }
        else
        {
            double range = maxValue - minValue;
            double percnt = pixelValue / pixelRange;
            if (getOrientation() == VERTICAL) percnt = 1 - percnt;

            return percnt * range + minValue;
        }


    }
    
    
    public void setPixelRange(float pixelRange){
        this.pixelRange = pixelRange;
    }

    
    public void preMeasure(int axisOffset, int graphCrossSpace)
    {
        this.axisOffset = axisOffset;
        this.graphCrossSpace = graphCrossSpace;
        
        updateLabels();

    }

    public boolean isAlignedStart()
    {
        return getOrientation() == VERTICAL ? getLeft() == 0 : getTop() == 0;
    }

    public boolean isAlignedEnd()
    {
        ViewGroup parent = (ViewGroup) getParent();
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

    public int getScaleType()
    {
        return scaleType;
    }

    public void setScaleType(int scale)
    {
        scaleType = scale;
        updateLabels();
    }

    public int getLogBase()
    {
        return logBase;
    }

    public int getAxisOffset()
    {
        return axisOffset;
    }

    public void setLogBase(int logBase)
    {
        this.logBase = logBase;
        updateLabelContents();
    }

    public void scale(float factor)
    {
        if (scaleType == LOGSCALE)
        {
            //todo:
        }
        else
        {
            setRange(factor * minValue, factor * maxValue);
        }
    }
}
