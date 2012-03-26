package sriracha.frontend.android.results;

import android.content.Context;
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
import java.util.List;


public class Axis extends LinearLayout {

    private static int notchLength = 7;

    private int axisOffset;

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
    private int labelCount;

    private Paint linePaint;

    private LayoutInflater inflater;

    protected List<TextView> labelViews;

    protected Axis(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected Axis(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }

    
    private boolean shouldFlip(int oldOffset, int newOffset){
        ViewGroup parent = (ViewGroup) getParent();
        int mid = getOrientation() == VERTICAL ? parent.getWidth() / 2 :  parent.getHeight() / 2;       
        return (oldOffset < mid && newOffset > mid) || (newOffset < mid && oldOffset > mid);
    }

    /**
     * Tells the axis where the line should be drawn relative to the parent object
     * This call will reposition the axis wrt the parent object
     * @param offset offset from top or left of parent control for axis line positioning
     */
    public void setAxisLineOffset(int offset) {
        if(shouldFlip(axisOffset, offset)){
            flipLabelsSide();
        }

        ViewGroup parent = (ViewGroup) getParent();

        int max = getOrientation() == VERTICAL ? parent.getWidth() - getWidth() :  parent.getHeight() - getHeight();

        int edgeDist = (notchLength - 1) / 2;


        if (getOrientation() == VERTICAL) {
            int left = labelSide == 0 ? offset - (getWidth() - edgeDist) : offset - edgeDist;
            setLeft(clamp(left, 0, max));
        } else {
            int top = labelSide == 0 ? offset - (getHeight() - edgeDist) : offset - edgeDist;
            setTop(clamp(top, 0, max));
        }

        invalidate();
    }


    /**
     * Changes the labels from one side of the axis line to the other
     */
    private void flipLabelsSide() {
        labelSide = 1 - labelSide;
        for (int i = 0; i < getChildCount(); i++) {
            TextView view = (TextView) getChildAt(i);
            if (getOrientation() == VERTICAL) {
                view.setGravity(labelSide == 1 ? Gravity.RIGHT | Gravity.CENTER_VERTICAL :
                                                 Gravity.LEFT | Gravity.CENTER_VERTICAL );
            } else {
                view.setGravity(labelSide == 1 ? Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL :
                                                 Gravity.TOP | Gravity.CENTER_HORIZONTAL );
            }
        }
    }

    public Axis(Context context) {
        super(context);
        init();

    }

    private void init() {
        setWillNotDraw(false);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        labelViews = new ArrayList<TextView>();
        labelCount = 8;//default value
        setOrientation(VERTICAL);
        linePaint = new Paint();
        linePaint.setARGB(255, 255, 255, 255);
        linePaint.setStrokeWidth(2);
        minValue = -1000;
        maxValue = 1000;
        logscale = false;
        logbase = 10;
        initLabels();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int children = getChildCount();
        int desiredWidth, desiredHeight;
        if (getOrientation() == VERTICAL) {
            desiredHeight = parentHeight;
            desiredWidth = notchLength + 90;//change this !!
        } else {
            desiredWidth = parentWidth;
            desiredHeight = notchLength + 60;
        }
        setMeasuredDimension(desiredWidth, desiredHeight);
        for (int i = 0; i < children; i++) {
            int widthspec = MeasureSpec.makeMeasureSpec(desiredWidth, MeasureSpec.AT_MOST);
            int heightspec = MeasureSpec.makeMeasureSpec(desiredWidth, MeasureSpec.AT_MOST);
            getChildAt(i).measure(widthspec, heightspec);
        }
    }

  /*  @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        float spacing  = (top - bottom) / (labelCount - 1);
        for(int i = 0; i<labelCount; i++){
            TextView view = labelViews.get(i);
            LinearLayout l = new LinearLayout(getContext());

            view.setTop(i * );
        }
    }
    */

    public void setRange(double minValue, double maxValue) {
        if (minValue != this.minValue || maxValue != this.maxValue) {
            this.maxValue = maxValue;
            this.minValue = minValue;
            updateLabelContents();
            invalidate();
        }
    }

    private void updateLabelContents() {
        double interval = (maxValue - minValue) / (labelCount - 1);
        for (int i = 0; i < labelCount; i++) {
            labelViews.get(i).setText(axisNumFormat(minValue + interval * i));
        }
    }

    private TextView inflateLabel() {
        return (TextView) inflater.inflate(R.layout.results_axis_label, this, true);
    }


    protected void initLabels() {
        for (int i = 0; i < labelCount; i++) {
            TextView v = inflateLabel();
            labelViews.add(v);
            //  addView(v); //being added by the inflater
        }
    }

    public void setLabelCount(int labelCount) {
        int diff = labelCount - this.labelCount;

        if (diff < 0) {
            while (diff++ < 0) {
                TextView view = labelViews.remove(0);
                removeView(view);
            }
        } else if (diff > 0) {
            while (diff-- > 0) {
                TextView view = inflateLabel();
                labelViews.add(view);
                // addView(view);
            }
        }


        this.labelCount = labelCount;

        updateLabelContents();
        invalidate();
    }

    protected static String axisNumFormat(double val) {
        if (val <= 1000 && val >= -1000) {
            DecimalFormat format = new DecimalFormat("###.##");
            return format.format(val);
        } else return null;
    }

    /**
     * returns pixel offset from the start of this control to the location
     * corresponding to the axis value requested. If the axis value is outside of the
     * range, returns -1
     *
     * @param axisValue value you want to find the pixel offset for
     * @return pixel offset from start (left or top)
     */
    public int pixelsFromCoordinate(double axisValue) {
        return 0;//todo: implement this
    }

    /**
     * returns the coordinate corresponding to pixel offset from the start
     * of this control to the location requested. If the axis value is outside of the
     * range, returns -1
     *
     * @param pixelValue offset value you want to find the coordinate for
     * @return corresponding axis value
     */
    public int coordinateFromPixel(double pixelValue) {
        return 0;//todo: implement this
    }

}
