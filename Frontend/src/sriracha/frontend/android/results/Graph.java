package sriracha.frontend.android.results;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import sriracha.frontend.android.EpicTouchListener;
import sriracha.frontend.android.results.functions.Function;
import sriracha.frontend.resultdata.Plot;

import java.util.ArrayList;
import java.util.List;


public class Graph extends FrameLayout {

    Axis yAxis;

    Axis xAxis;


    private List<PlotView> plots;

    public Graph(Context context) {
        super(context);
        init();

    }


    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int xrange = width, yrange = height;

        xAxis.setPixelRange(xrange);
        yAxis.setPixelRange(yrange);

        xAxis.preMeasure(Math.round(yAxis.pixelsFromCoordinate(0)), height);
        yAxis.preMeasure(Math.round(xAxis.pixelsFromCoordinate(0)), width);

        //do first measure to determine axis width.

        yAxis.measure(widthMeasureSpec, heightMeasureSpec);

        xAxis.measure(widthMeasureSpec, heightMeasureSpec);

        int yWidth = yAxis.getMeasuredWidth();
        int xHeight = xAxis.getMeasuredHeight();

        yAxis.setPairedXSize(xHeight);
        xAxis.setPairedXSize(yWidth);

        float x0 = xAxis.pixelsFromCoordinate(0);
        float y0 = yAxis.pixelsFromCoordinate(0);

        if (x0 < yWidth || x0 > width - yWidth) {
            yrange -= yWidth;
            yAxis.setPixelRange(yrange);
        }

        if (y0 < xHeight || y0 > height - xHeight) {
            xrange -= xHeight;
            xAxis.setPixelRange(xrange);
        }


        int internalWidthSpec = MeasureSpec.makeMeasureSpec(xrange, MeasureSpec.AT_MOST);
        int internalHeightSpec = MeasureSpec.makeMeasureSpec(yrange, MeasureSpec.AT_MOST);

        //measure all children with internal spec
        super.onMeasure(internalWidthSpec, internalHeightSpec);


        //graph takes all initial available size.
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int yWidth = yAxis.getMeasuredWidth();
        int xHeight = xAxis.getMeasuredHeight();

        float x0 = xAxis.pixelsFromCoordinate(0);
        float y0 = yAxis.pixelsFromCoordinate(0);

        //internal edges
        int iLeft = x0 < yWidth ? yWidth : 0;
        int iRight = x0 > width - yWidth ? width - yWidth : width;
        int iTop = y0 < xHeight ? xHeight : 0;
        int iBottom = y0 < height - xHeight ? height - xHeight : height;

        int yleft = iLeft != 0 ? 0 :yAxis.getEdgeOffset();

        yAxis.layout(yleft, iTop, yleft + yAxis.getMeasuredWidth(), iBottom);

        int xtop = iTop == 0 ? xAxis.getEdgeOffset() : 0;

        xAxis.layout(iLeft, xtop, iRight, xtop + xAxis.getMeasuredHeight());

        for (PlotView pv : plots) {
            pv.layout(iLeft, iTop, iRight, iBottom);
        }

    }


    private void init() {
        yAxis = new Axis(getContext());
        addView(yAxis, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        yAxis.setOrientation(LinearLayout.VERTICAL);
        xAxis = new Axis(getContext());
        addView(xAxis, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        xAxis.setOrientation(LinearLayout.HORIZONTAL);
        plots = new ArrayList<PlotView>();
        setOnTouchListener(new GraphGestureListener());


    }


    private class GraphGestureListener extends EpicTouchListener {

        @Override
        public boolean onSingleFingerMove(float distanceX, float distanceY) {

            // yAxis.pan(distanceY);
            // xAxis.pan(distanceX);

            double ymin = yAxis.coordinateFromPixel(distanceY);
            double xmin = xAxis.coordinateFromPixel(-distanceX);


            double ymax = yAxis.coordinateFromPixel(yAxis.getHeight() + distanceY);
            double xmax = xAxis.coordinateFromPixel(xAxis.getWidth() - distanceX);

            xAxis.setRange(xmin, xmax);
            yAxis.setRange(ymin, ymax);

            requestLayout();
            invalidate();
            return true;
        }


        @Override
        protected boolean onScale(float xFactor, float yFactor, float xCenter, float yCenter) {

            double xMinSize = xCenter/xAxis.getWidth();
            double xMaxSize = xAxis.getWidth() - xMinSize;
            double yMaxSize = yCenter/yAxis.getHeight();
            double yMinSize = yAxis.getHeight() - yMaxSize;

            double xMin = xAxis.coordinateFromPixel((float) (xCenter - xFactor * xMinSize));
            double xMax = xAxis.coordinateFromPixel((float) (xCenter + xFactor * xMaxSize));
            double yMin = xAxis.coordinateFromPixel((float) (yCenter + yFactor * yMinSize));
            double yMax = xAxis.coordinateFromPixel((float) (yCenter - yFactor * yMaxSize));

            xAxis.setRange(xMin, xMax);
            yAxis.setRange(yMin, yMax);


            requestLayout();
            invalidate();
            return true;
        }
    }


    public void addPlot(Plot plot, int color) {
        addPlot(plot, color, null);

    }

    public void addPlot(Plot plot, int color, Function f) {
        PlotView plotView = new PlotView(this, getContext());
        plotView.setColor(color);
        plotView.setPlot(plot);
        plotView.setFunc(f);
        plots.add(plotView);
        addView(plotView);

    }

    public void clearPlots() {
        for (PlotView pv : plots) {
            removeView(pv);
        }
        plots.clear();
    }


    public float[] pixelsFromCoords(double x, double y) {
        return new float[]{xAxis.pixelsFromCoordinate(x), yAxis.pixelsFromCoordinate(y)};
    }


    public void setYRange(double min, double max) {
        yAxis.setRange(min, max);
        requestLayout();
        invalidate();
    }

    public void setXRange(double min, double max) {
        xAxis.setRange(min, max);
        requestLayout();
        invalidate();
    }

    public double getYmin() {
        return yAxis.getMinValue();
    }

    public double getYmax() {
        return yAxis.getMaxValue();
    }

    public double getXmin() {
        return xAxis.getMinValue();
    }

    public double getXmax() {
        return xAxis.getMaxValue();
    }
}
