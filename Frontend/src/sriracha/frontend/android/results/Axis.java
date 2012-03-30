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


public class Axis extends LinearLayout
{

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


    private static int lineSpace = 20;

    private int axisOffset;

    private int edgeOffset;

    private boolean logScale;

    private int logBase;


    /**
     * Side of the axis the label is on 0 is (left, top), 1 is (right, bottom).
     */
    private int labelSide;

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


    public void setAxisOffset(int axisOffset)
    {
        this.axisOffset = axisOffset;
        updateLabels();
        updateLabelsSide();
        updateEdgeOffset();
    }

    public int getEdgeOffset()
    {
        return edgeOffset;
    }

    private void updateEdgeOffset()
    {
        ViewGroup parent = (ViewGroup) getParent();

        int max = getOrientation() == VERTICAL ? parent.getWidth() - getWidth() : parent.getHeight() - getHeight();

        int edgeDist = lineSpace / 2;

        if (getOrientation() == VERTICAL)
        {
            int left = labelSide == 0 ? axisOffset - (getWidth() - edgeDist) : axisOffset - edgeDist;
            edgeOffset = clamp(left, 0, max);
        }
        else
        {
            int top = labelSide == 0 ? axisOffset - (getHeight() - edgeDist) : axisOffset - edgeDist;
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
        ViewGroup parent = (ViewGroup) getParent();
        int mid = getOrientation() == VERTICAL ? parent.getWidth() / 2 : parent.getHeight() / 2;
        int side = getOrientation() == VERTICAL ? axisOffset > mid ? 1 : 0 : axisOffset >= mid ? 1 : 0;
        if (labelSide != side)
        {
            labelSide = side;
            updateLabelAttributes();
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
        if (logScale)
        {
            //todo: pan the decades
        }
        else
        {
            double push = pixels * (maxValue - minValue) / getPixelRange();
            setRange(minValue + push, maxValue + push);
        }
    }


    private void updateLabels()
    {
        if (getMeasuredHeight() == 0 && getMeasuredWidth() == 0)
        {
            //this has been called to early by some setup procedure
            return;
        }

        int labelCount = 0;
        preAxisLabelCount = 0;
        postAxisLabelCount = 0;
        if (logScale)
        {
            //todo:
        }
        else
        {
            float step = getOrientation() == HORIZONTAL ? linHNS : linVNS;
            float max = getOrientation() == HORIZONTAL ? getMeasuredWidth() : getMeasuredHeight();

            for (float i = 0; i <= max; i += step)
            {
                if (i >= 0.5 * step && i <= axisOffset - step)
                {
                    preAxisLabelCount++;
                    labelCount++;
                }
                else if (i >= axisOffset + step && i <= max)
                {
                    postAxisLabelCount++;
                    labelCount++;
                }
            }

        }

        adjustLabelCount(labelCount);

        updateLabelContents();
        updateLabelAttributes();
    }

    private void adjustLabelCount(int count)
    {
        int cc = getChildCount();
        if (cc < count)
        {
            while (cc++ < count) inflateLabel();
        }
        else if (cc > count)
        {
            while (cc-- > count) removeViewAt(cc);
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
        logScale = false;
        logBase = 10;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() == 0)
        {
            //this is first time we know our size, initialize labels
            updateLabels();
            super.onMeasure(widthMeasureSpec, heightMeasureSpec); //need to remeasure after labels are created
        }

        //add space for line
        if (getOrientation() == VERTICAL)
            setMeasuredDimension(getMeasuredWidth() + lineSpace, getMeasuredHeight());
        else
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + lineSpace);


    }


    @Override
    public void setOrientation(int orientation)
    {
        if (getOrientation() != orientation)
        {
            super.setOrientation(orientation);
            updateLabels();
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        if (logScale)
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
        int width = right - left;
        int height = bottom - top;


        if (getOrientation() == VERTICAL)
        {
            int labelIndex = 0;

            int labelLeft = labelSide == 0 ? 0 : lineSpace;
            for (float mid = axisOffset - linVNS; mid >= 0.5 * linVNS; mid -= linVNS)
            {
                int labelTop = (int) (mid - linVNS / 2);
                getLabel(preAxisLabelCount - (++labelIndex)).layout(labelLeft, labelTop, labelLeft + width - lineSpace, labelTop + linVNS);
            }

            for (float mid = axisOffset + linVNS; mid <= height - 0.5 * linVNS; mid += linVNS)
            {
                int labelTop = (int) (mid - linVNS / 2);
                getLabel(labelIndex++).layout(labelLeft, labelTop, labelLeft + width - lineSpace, labelTop + linVNS);
            }

            /* int space = height / getChildCount();
            int labelLeft = labelSide == 0 ? 0 : lineSpace;
            for (int i = 0; i < getChildCount(); i++)
            {
                TextView label = getLabel(i);
                int labelTop = i * space;
                label.layout(labelLeft, labelTop, labelLeft + width - lineSpace, labelTop + label.getMeasuredHeight());
            }*/
        }
        else
        {


            int labelIndex = 0;

            int labelTop = labelSide == 0 ? 0 : lineSpace;
            for (float mid = axisOffset - linHNS; mid >= 0.5 * linHNS; mid -= linHNS)
            {
                int labelLeft = (int) (mid - linHNS / 2);
                getLabel(preAxisLabelCount - (++labelIndex)).layout(labelLeft, labelTop, labelLeft + linHNS, labelTop + height - lineSpace);
            }

            for (float mid = axisOffset + linHNS; mid <= height - 0.5 * linHNS; mid += linHNS)
            {
                int labelLeft = (int) (mid - linHNS / 2);
                getLabel(labelIndex++).layout(labelLeft, labelTop, labelLeft + linHNS, labelTop + height - lineSpace);
            }


           /* int space = width / getChildCount();
            int labelTop = labelSide == 0 ? 0 : lineSpace;
            for (int i = 0; i < getChildCount(); i++)
            {
                TextView label = getLabel(i);
                int labelLeft = i * space;
                label.layout(labelLeft, labelTop, labelLeft + label.getMeasuredWidth(), labelTop + height - lineSpace);
            }*/
        }
    }


    public void setRange(double minValue, double maxValue)
    {
        if (minValue != this.minValue || maxValue != this.maxValue)
        {
            this.maxValue = maxValue;
            this.minValue = minValue;
            updateLabels();
        }
    }

    private void updateLabelContents()
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            TextView label = getLabel(i);
            int mid = getOrientation() == VERTICAL ? label.getTop() + label.getMeasuredHeight() / 2 :
                    label.getLeft() + label.getMeasuredWidth() / 2;
            label.setText(axisNumFormat(coordinateFromPixel(mid)));
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
        for (int i = 0; i < getChildCount(); i++)
        {
            TextView label = getLabel(i);
            if (getOrientation() == VERTICAL)
            {
                sx = labelSide == 0 ? getWidth() - lineSpace + (lineSpace - nlen) / 2 : (lineSpace - nlen) / 2;
                ex = sx + nlen;
                sy = ey = label.getTop() + label.getHeight() / 2;
            }
            else
            {
                sy = labelSide == 0 ? getHeight() - lineSpace + (lineSpace - nlen) / 2 : (lineSpace - nlen) / 2;
                ey = sy + nlen;
                sx = ex = label.getLeft() + label.getWidth() / 2;
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
        if (logScale)
        {
            //todo:
            return -1;
        }
        else
        {
            double percent = (axisValue - minValue) / (maxValue - minValue);
            if (getOrientation() == VERTICAL) percent = 1 - percent;
            return (float) (percent * getPixelRange());
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

        if (logScale)
        {
            //todo:
            return -1;
        }
        else
        {
            double range = maxValue - minValue;
            double percnt = pixelValue / getPixelRange();
            if (getOrientation() == VERTICAL) percnt = 1 - percnt;

            return percnt * range + minValue;
        }


    }

    private double getPixelRange()
    {
        return getOrientation() == VERTICAL ? getHeight() : getWidth();
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

    public boolean isLogScale()
    {
        return logScale;
    }

    public void setLogScale(boolean logScale)
    {
        this.logScale = logScale;
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
        if (logScale)
        {
            //todo:
        }
        else
        {
            setRange(factor * minValue, factor * maxValue);
        }
    }
}
