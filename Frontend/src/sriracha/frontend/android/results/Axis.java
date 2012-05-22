package sriracha.frontend.android.results;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import sriracha.frontend.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Crucial component of the graphing package, the Axis class takes care of drawing labels and
 * maintaining pixel to coordinate mappings. Supports log and linear scales.
 */
class Axis extends LinearLayout
{

    private OnAxisChangedListener changedListener;

    /**
     * Constants representing scale type
     */
    static final int LOGSCALE = 1, LINEARSCALE = 0;

    /**
     * Linear Vertical Notch Space
     * space in pixels between each axis notch for
     * linear Vertical Axis - does not change
     */
    protected int linVNS = 60;

    /**
     * Linear Horizontal Notch Space
     * space in pixels between each axis notch for
     * linear Horizontal Axis - does not change
     */
    protected int linHNS = 100;

    /**
     * Logarithmic Decade Space
     * space in pixels between each label for
     * log scale Axis - dependent on range
     */
    protected int logDS = 150;

    protected int decadesPerLabel = 1;

    //number of pixels available along the orientation
    private float pixelRange;

    //number of pixels in the graph perpendicular to this axis
    private int graphCrossSpace;

    public static final int lineSpace = 20;

    private float axisOffset;

    private float edgeOffset;

    private int scaleType;

    private int logBase;

    private int pairedXSize;


    /**
     * Side of the axis the label is on 0 is (left, top), 1 is (right, bottom).
     */
    private int labelSide;

    private double edgeProximity()
    {
        return getOrientation() == HORIZONTAL ? 0.25 : 0.1;
    }

    ;

    //minimum value for axis
    private double minValue;

    private double maxValue;

    protected Paint linePaint;

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

    private float clamp(float value, float min, float max)
    {
        return value < min ? min : (value > max ? max : value);
    }

    public float getEdgeOffset()
    {
        return edgeOffset;
    }

    private void updateEdgeOffset()
    {
        int max = getOrientation() == VERTICAL ? graphCrossSpace - getMeasuredWidth() : graphCrossSpace - getMeasuredHeight();

        int edgeDist = lineSpace / 2;

        if (getOrientation() == VERTICAL)
        {
            float left = labelSide == 0 ? axisOffset - (getMeasuredWidth() - edgeDist) : axisOffset - edgeDist;
            edgeOffset = clamp(left, 0, max);
        } else
        {
            float top = labelSide == 0 ? axisOffset - (getMeasuredHeight() - edgeDist) : axisOffset - edgeDist;
            edgeOffset = clamp(top, 0, max);
        }
    }

    public void setOnAxisChangedListener(OnAxisChangedListener changedListener)
    {
        this.changedListener = changedListener;
    }

    /**
     * Updates internal text Alignment for the TextViews that hold the axis labels
     * alignment changes depending on which side of the axis line the labels are drawn and
     * on axis orientation.
     */
    private void updateLabelAttributes()
    {
        int start = 0;

        if (hasStartNotch())
        {
            TextView view = getLabel(0);
            if (getOrientation() == VERTICAL)
            {
                view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
                view.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
            } else
            {
                view.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
                view.setGravity(Gravity.TOP | Gravity.LEFT);
            }
            start = 1;
        }


        for (int i = start; i < getChildCount(); i++)
        {
            TextView view = (TextView) getChildAt(i);
            if (getOrientation() == VERTICAL)
            {
                view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
                view.setGravity(labelSide == 0 ? Gravity.RIGHT | Gravity.CENTER_VERTICAL :
                        Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else
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

    private void updateLabels()
    {

        updateLabelCount();

        updateLabelContents();

        updateLabelsSide();

        updateLabelAttributes();
    }

    private ArrayList<Float> getNotchPositions()
    {
        double intersectCoord = scaleType == LINEARSCALE ? 0 : minValue;
        //list of notch positions
        ArrayList<Float> positions = new ArrayList<Float>();

        //distance between notches in pixels
        float spacing = scaleType == LINEARSCALE ? getOrientation() == HORIZONTAL ? linHNS : linVNS : logDS;

        if (isPairAlignedStart())
        {
            float startPos = getOrientation() == HORIZONTAL ? 0f : pixelRange;
            positions.add(startPos);
        }

        //for each valid midpoint after the intersection with the paired axis
        for (float pos = Math.max(pixelsFromCoordinate(intersectCoord) + spacing, spacing); pos <= pixelRange - edgeProximity() * spacing; pos += spacing)
        {
            positions.add(pos);
        }
        //for each valid midpoint before the intersection with the paired axis
        for (float pos = Math.min(pixelsFromCoordinate(intersectCoord) - spacing, pixelRange - spacing); pos >= edgeProximity() * spacing; pos -= spacing)
        {
            positions.add(pos);
        }


        return positions;
    }

    private boolean isPairAlignedStart()
    {
        double intersectCoord = scaleType == LINEARSCALE ? 0 : minValue;
        double intersect = pixelsFromCoordinate(intersectCoord);
        return getOrientation() == HORIZONTAL ? intersect <= pairedXSize : intersect >= pixelRange;
    }


    private void updateLabelCount()
    {
        int labelCount = getNotchPositions().size();
        //create/destroy labels according to count
        int cc = getChildCount();
        if (cc < labelCount)
        {
            while (cc++ < labelCount) inflateLabel();
        } else if (cc > labelCount)
        {
            while (cc-- > labelCount) removeViewAt(cc);
        }
    }

    private void init()
    {
        setWillNotDraw(false);
        linePaint = new Paint();
        linePaint.setARGB(255, 255, 255, 255);
        linePaint.setStrokeWidth(2);
        minValue = -1000;
        maxValue = 1000;
        scaleType = LINEARSCALE;
        logBase = 10;

    }

    private void updateLogSpacing()
    {
        if (scaleType != LOGSCALE) return;
        int count = 0;

        int artificialBase = (int) Math.pow(logBase, decadesPerLabel);

        for (double notch = minValue; notch <= maxValue; notch *= artificialBase)
        {
            count++;
        }

        double endDecPercent = log(maxValue / (Math.pow(artificialBase, count - 1) * minValue), artificialBase);

        logDS = (int) (pixelRange / (count - 1 + endDecPercent));

        int logthresh = (getOrientation() == VERTICAL ? linVNS : linHNS);

        while (logDS < logthresh)
        {
            logDS *= 2;
            decadesPerLabel *= 2;
        }

        while (logDS > 2 * logthresh && decadesPerLabel > 1)
        {
            logDS /= 2;
            decadesPerLabel /= 2;
        }

    }

    private boolean hasStartNotch()
    {
        float startPos = getOrientation() == HORIZONTAL ? 0f : pixelRange;
        ArrayList<Float> notchPositions = getNotchPositions();
        return notchPositions.size() > 0 && notchPositions.get(0) == startPos;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        //add space for line
        if (getOrientation() == VERTICAL)
        {
            int height = scaleType == LINEARSCALE ? linVNS : logDS;
            int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
            int start = 0;


            if (hasStartNotch())
            {
                //we have a special notch at start
                int hhspec = MeasureSpec.makeMeasureSpec(height / 2, MeasureSpec.EXACTLY);
                getLabel(0).measure(widthSpec, hhspec);
                start = 1;

            }

            for (int i = start; i < getChildCount(); i++)
            {
                getLabel(i).measure(widthSpec, heightSpec);
            }


            setMeasuredDimension(getMeasuredWidth() + lineSpace, parentHeight);

        } else
        {
            int width = scaleType == LINEARSCALE ? linHNS : logDS;
            int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
            int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

            int start = 0;

            if (hasStartNotch())
            {
                //we have a special notch at start
                int hwspec = MeasureSpec.makeMeasureSpec(widthMeasureSpec / 2, MeasureSpec.EXACTLY);
                getLabel(0).measure(hwspec, heightSpec);
                start = 1;

            }

            for (int i = start; i < getChildCount(); i++)
            {
                getLabel(i).measure(widthSpec, heightSpec);
            }
            setMeasuredDimension(parentWidth, getMeasuredHeight() + lineSpace);

        }


        updateEdgeOffset(); // now that we know cross section size (measured width for y )
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {


        int hspace = getScaleType() == LINEARSCALE ? linHNS : logDS;
        int vspace = getScaleType() == LINEARSCALE ? linVNS : logDS;

        float spacing = getOrientation() == HORIZONTAL ? hspace : vspace;

        int start = 0;

        if (hasStartNotch())
        {
            TextView label = getLabel(start);
            int ltop = getOrientation() == VERTICAL ? (int) (pixelRange - vspace / 2) : labelSide == 0 ? 0 : lineSpace;
            int lleft = getOrientation() == HORIZONTAL ? 1 : labelSide == 0 ? 0 : lineSpace;
            int lwidth = getOrientation() == VERTICAL ? label.getMeasuredWidth() : hspace / 2;
            int lheight = getOrientation() == HORIZONTAL ? label.getMeasuredHeight() : vspace / 2;
            label.layout(lleft, ltop, lleft + lwidth, ltop + lheight);
            start++;

        }


        ArrayList<Float> notches = getNotchPositions();
        for (int i = start; i < getChildCount(); i++)
        {
            TextView label = getLabel(i);
            float mid = notches.get(i);
            int labelStart = (int) (mid - spacing / 2);
            int ltop = getOrientation() == VERTICAL ? labelStart : labelSide == 0 ? 0 : lineSpace;
            int lleft = getOrientation() == HORIZONTAL ? labelStart : labelSide == 0 ? 0 : lineSpace;
            int lwidth = getOrientation() == VERTICAL ? label.getMeasuredWidth() : hspace;
            int lheight = getOrientation() == HORIZONTAL ? label.getMeasuredHeight() : vspace;
            label.layout(lleft, ltop, lleft + lwidth, ltop + lheight);
        }

    }


    public void setRange(double minValue, double maxValue)
    {
        double minDiff = 0.00001;

        //one of the values is NAN do nothing.
        if (minValue == Double.NaN || maxValue == Double.NaN) return;

        if (maxValue - minValue < minDiff)
        {
            maxValue = (maxValue + minValue + minDiff) / 2;
            minValue = (maxValue + minValue - minDiff) / 2;
        }

        //cant have such values on a log scale
        if (scaleType == LOGSCALE && minValue <= 0) minValue = 1;


        double maxmax = Math.pow(10, 50);
        double minmin = scaleType == LOGSCALE ? Math.pow(10, -50) : -maxmax;

        maxValue = Math.min(maxValue, maxmax);
        minValue = Math.max(minValue, minmin);


        if (minValue != this.minValue || maxValue != this.maxValue)
        {
            this.maxValue = maxValue;
            this.minValue = minValue;

            if (changedListener != null)
            {
                changedListener.onAxisRangeChanged(minValue, maxValue);
            }
        }
    }


    private void updateLabelContents()
    {
        int i = 0;
        for (float mid : getNotchPositions())
        {
            getLabel(i++).setText(axisNumFormat(coordinateFromPixel(mid)));
        }
    }

    private void inflateLabel()
    {
        inflate(getContext(), R.layout.results_axis_label, this);
    }

    /**
     * Used for drawing dashed preview lines
     *
     * @return offset in Graph where the axis is actually drawn
     */
    public int getDrawnAxisOffset()
    {
        if (getOrientation() == VERTICAL)
        {
            return getLeft() + (labelSide == 0 ? getWidth() - lineSpace / 2 : lineSpace / 2);
        } else
        {
            return getTop() + (labelSide == 0 ? getHeight() - lineSpace / 2 : lineSpace / 2);
        }

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
        } else
        {
            sx = 0;
            ex = getWidth();
            sy = ey = labelSide == 0 ? getHeight() - lineSpace / 2 : lineSpace / 2;
        }
        canvas.drawLine(sx, sy, ex, ey, linePaint);


        int nlen = (lineSpace - 4) / 2;
        //draw notches vis-a-vis the labels

        int i = 0;

        for (float mid : getNotchPositions())
        {

            if (getOrientation() == VERTICAL)
            {
                sx = labelSide == 0 ? getWidth() - lineSpace + (lineSpace - nlen) / 2 : (lineSpace - nlen) / 2;
                ex = sx + nlen;
                sy = ey = (int) mid;
            } else
            {
                sy = labelSide == 0 ? getHeight() - lineSpace + (lineSpace - nlen) / 2 : (lineSpace - nlen) / 2;
                ey = sy + nlen;
                sx = ex = (int) mid;
            }

            canvas.drawLine(sx, sy, ex, ey, linePaint);
        }

    }


    protected String axisNumFormat(double val)
    {
        if (((val <= 1000 && val >= -1000 && Math.abs(val) > 1. / 1000.) || val == 0) && scaleType == LINEARSCALE)
        {
            DecimalFormat format = new DecimalFormat("#.##");
            return format.format(val);
        }

        DecimalFormat format = new DecimalFormat("#.##E00");
        return format.format(val);
    }

    public double log(double val, double base)
    {
        return Math.log(val) / Math.log(base);
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
            if (axisValue <= 0) return getOrientation() == HORIZONTAL ? -1 : pixelRange + 1;//log(x) > 0
            int fakeBase = (int) Math.pow(logBase, decadesPerLabel);

            double offset = logDS * log(axisValue / minValue, fakeBase);

            return (float) (getOrientation() == HORIZONTAL ? offset : pixelRange - offset);

        } else
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
            double offset = getOrientation() == HORIZONTAL ? pixelValue : pixelRange - pixelValue;

            int fakeBase = (int) Math.pow(logBase, decadesPerLabel);

            return Math.pow(fakeBase, offset / logDS) * minValue;

        } else
        {
            double range = maxValue - minValue;
            double percnt = pixelValue / pixelRange;
            if (getOrientation() == VERTICAL) percnt = 1 - percnt;

            return percnt * range + minValue;
        }


    }


    public void setPixelRange(float pixelRange)
    {
        this.pixelRange = pixelRange;
        //update spacing for logscales
        updateLogSpacing();
    }

    /**
     * passes information the the axis that it will need before calling measure
     *
     * @param axisOffset      distance from edge of graph that the paired axis recommends
     *                        for this axis line. can be out of bounds
     * @param graphCrossSpace total available space in the graph perpendicular to this axis,
     *                        used to limit axis placement and determine which side of the axis line the label is drawn.
     */
    public void preMeasure(float axisOffset, int graphCrossSpace)
    {
        this.axisOffset = axisOffset;
        this.graphCrossSpace = graphCrossSpace;

        //update labels with new information
        updateLabels();

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

    /**
     * sets the type of scale linear or log
     *
     * @param scale
     */
    public void setScaleType(int scale)
    {
        if (scaleType != scale)
        {
            scaleType = scale;

            if (changedListener != null)
            {
                changedListener.onAxisScaleTypeChanged(scale);
            }

            //just to make sure in case user did not change, otherwise infinite loops happen
            if (scaleType == LOGSCALE && minValue <= 0)
            {
                setMinValue(1);
            }
        }


    }

    public int getLogBase()
    {
        return logBase;
    }

    public void setLogBase(int logBase)
    {
        if (this.logBase != logBase)
        {
            this.logBase = logBase;
            if (changedListener != null)
            {
                changedListener.onAxisLogBaseChanged(logBase);
            }
        }

    }

    interface OnAxisChangedListener
    {
        public void onAxisRangeChanged(double minValue, double maxValue);

        public void onAxisScaleTypeChanged(int scaleType);

        public void onAxisLogBaseChanged(int logBase);
    }

    public void setPairedXSize(int pairedXSize)
    {
        this.pairedXSize = pairedXSize;
    }
}


