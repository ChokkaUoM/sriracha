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


public class Graph extends FrameLayout
{

    Axis yAxis;
    
    Axis xAxis;

    
    private List<PlotView> plots;

    public Graph(Context context) {
        super(context);
        init();

    }


    public Graph(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

   /* @Override
    public void invalidate() {
        super.invalidate();   
        for(PlotView pv : plots){
            pv.invalidate();
        }
    }*/

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

        int y0 = Math.round(yAxis.pixelsFromCoordinate(0));
        int x0 =  Math.round(xAxis.pixelsFromCoordinate(0));

        xAxis.setAxisOffset(y0);
        yAxis.setAxisOffset(x0);

        int yleft = yAxis.getEdgeOffset();

        yAxis.layout(yleft, 0, yleft + yAxis.getMeasuredWidth(), yAxis.getMeasuredHeight());

        int xtop = xAxis.getEdgeOffset();

        xAxis.layout(0, xtop, xAxis.getMeasuredWidth(), xtop + xAxis.getMeasuredHeight());
        
        for(PlotView pv : plots){
            pv.layout(0, 0, pv.getMeasuredWidth(),  pv.getMeasuredHeight());
        }

    }


    private void init(){
        yAxis = new Axis(getContext());
        addView(yAxis, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        yAxis.setOrientation(LinearLayout.VERTICAL);
        xAxis = new Axis(getContext());
        addView(xAxis, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        xAxis.setOrientation(LinearLayout.HORIZONTAL);
        plots =  new ArrayList<PlotView>();
        setOnTouchListener(new GraphGestureListener());


    }



    private class GraphGestureListener extends EpicTouchListener
    {

        @Override
        public boolean onSingleFingerMove(float distanceX, float distanceY)
        {
            double xPush = distanceX*xAxis.getPCR();
            double yPush = distanceY*yAxis.getPCR();
            yAxis.setRange(yAxis.getMinValue() + yPush, yAxis.getMaxValue() + yPush);
            xAxis.setRange(xAxis.getMinValue() - xPush, xAxis.getMaxValue() - xPush);
            requestLayout();
            invalidate();
            return true;
        }


        @Override
        protected boolean onScale(float xFactor, float yFactor)
        {
            yAxis.setRange(yAxis.getMinValue() * yFactor, yAxis.getMaxValue() * yFactor);
            xAxis.setRange(xAxis.getMinValue() * xFactor, xAxis.getMaxValue() * xFactor);
            requestLayout();
            invalidate();
            return true;
        }
    }


    public void addPlot(Plot plot, int color){
        addPlot(plot, color, null);
        
    }

    public void addPlot(Plot plot, int color, Function f){
        PlotView plotView = new PlotView(this, getContext());
        plotView.setColor(color);
        plotView.setPlot(plot);
        plotView.setFunc(f);
        plots.add(plotView);
        addView(plotView);

    }
    
    public void clearPlots(){
        for(PlotView pv : plots){
            removeView(pv);
        }
        plots.clear();
    }
    
    
    public float[] pixelsFromCoords(double x, double y){
        return new float[]{xAxis.pixelsFromCoordinate(x), yAxis.pixelsFromCoordinate(y)};
    }


    public void setYRange(double min, double max){
        yAxis.setRange(min, max);
        requestLayout();
        invalidate();
    }

    public void setXRange(double min, double max){
        xAxis.setRange(min, max);
        requestLayout();
        invalidate();
    }

    public double getYmin() {
        return yAxis.getMinValue();
    }

    public void setYmin(double ymin) {
        yAxis.setMinValue(ymin);
        requestLayout();
        invalidate();
    }

    public double getYmax() {
        return yAxis.getMaxValue();
    }

    public void setYmax(double ymax) {
        yAxis.setMaxValue(ymax);
        requestLayout();
        invalidate();
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
        invalidate();
    }
}
