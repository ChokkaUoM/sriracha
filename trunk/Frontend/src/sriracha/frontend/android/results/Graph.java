package sriracha.frontend.android.results;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import sriracha.frontend.R;
import sriracha.frontend.android.EpicTouchListener;
import sriracha.frontend.android.results.functions.Function;
import sriracha.frontend.resultdata.Plot;
import sriracha.frontend.resultdata.Point;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class Graph extends FrameLayout {

    private Axis yAxis;

    private Axis xAxis;
    
    private TextView previewBox;

    private Point previewPoint;
    /**
     * number of dips from the nearest point where 
     * a touch is considered to be requesting a preview
     */
    private double previewThreshold = 20;

    private boolean deferInvalidate = false;


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

    /**
     * Measures the graph, makes 2 passes on Axes in order to determine relative placement
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //get available width and height
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        //measure spec for first pass on axes in order to determine their cross section sizes and relative placement
        int startWSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
        int startHSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        //for first pass we assume axis range is entire graph is no snapping
        int xrange = width, yrange = height;
        //tell axes about their available range so they can estimate pixel to coordinate mapping
        xAxis.setPixelRange(xrange);
        yAxis.setPixelRange(yrange);

        //tell axes about where they intersect the other axis so that they can estimate label size and position
        //also takes care of counting and filling labels
        xAxis.preMeasure(yAxis.pixelsFromCoordinate(0), height);
        yAxis.preMeasure(xAxis.pixelsFromCoordinate(0), width);

        //do first measure pass
        yAxis.measure(startWSpec, startHSpec);
        xAxis.measure(startWSpec, startHSpec);

        //now find out how large the cross section of each axis is, (from start of label to where axis line is drawn)
        // still only an estimate because we cant be 100% sure of label contents 
        int yAxisOffset = yAxis.getMeasuredWidth() - Axis.lineSpace / 2 + 1;
        int xAxisOffset = xAxis.getMeasuredHeight() - Axis.lineSpace / 2 + 1;

        //tell them about their pairs offset so they can tell if the other is snapped to start
        // in which case add a label at the very start of the axis. (this also ensures the first label on logscale is always present)
        yAxis.setPairedXSize(xAxisOffset);
        xAxis.setPairedXSize(yAxisOffset);

        //get estimates for desired axis intersect positions.
        float x0 = xAxis.pixelsFromCoordinate(0);
        float y0 = yAxis.pixelsFromCoordinate(0);
        
        //if the y axis will end up aligned to the start or end then it is considered snapped and we
        //reduce the range of the xAxis so it no longer overlaps with the yAxis
        if (x0 < yAxisOffset || x0 > width - yAxisOffset) {
            xrange -= yAxisOffset;
            xAxis.setPixelRange(xrange);
            xAxis.preMeasure(y0, height);
        }

        //Similarly, if the x axis will end up aligned to the start or end then it is considered snapped and we
        //reduce the range of the yAxis so it no longer overlaps with the xAxis
        if (y0 < xAxisOffset || y0 > height - xAxisOffset) {
            yrange -= xAxisOffset;
            yAxis.setPixelRange(yrange);
            yAxis.preMeasure(x0, width);
        }


        //create internal size specs for plot children and axes with correct x and y ranges
        int internalWidthSpec = MeasureSpec.makeMeasureSpec(xrange, MeasureSpec.AT_MOST);
        int internalHeightSpec = MeasureSpec.makeMeasureSpec(yrange, MeasureSpec.AT_MOST);

        //measure all children with internal spec including axes since they their pixel 
        // ranges might have changed as a result of snapping
        for(int i =0; i< getChildCount(); i++)
        {
            getChildAt(i).measure(internalWidthSpec, internalHeightSpec);
        }


        //graph takes all initial available size.
        setMeasuredDimension(width, height);
    }
    
    private int[] internalEdges(int width, int height)
    {
        int yAxisOffset = yAxis.getMeasuredWidth() - Axis.lineSpace /2 + 1;
        int xAxisOffset = xAxis.getMeasuredHeight() - Axis.lineSpace /2 + 1;

        // the pixels where x and y are = 0 (for placing the paired axis)
        float x0 = xAxis.pixelsFromCoordinate(0);
        float y0 = yAxis.pixelsFromCoordinate(0);

        //internal edges of the graph (everything if no edges are snapped)
        //internal left edge is 0 unless the y axis is snapped to the left
        int iLeft = x0 < yAxisOffset ? yAxisOffset : 0;
        //internal right edge is 0 unless y axis is snapped to the right
        int iRight = x0 > yAxisOffset && xAxis.getMeasuredWidth() < width ? width - yAxisOffset : width;
        int iTop = y0 < xAxisOffset ? xAxisOffset : 0;
        int iBottom = y0 > xAxisOffset && yAxis.getMeasuredHeight() < height ? height - xAxisOffset : height;
        
        return new int[]{iLeft, iTop, iRight, iBottom};
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        
        int []edges = internalEdges(width, height);

        int yleft = edges[0] != 0 ? 0 : (int) yAxis.getEdgeOffset();

        yAxis.layout(yleft, edges[1], yleft + yAxis.getMeasuredWidth(), edges[3]);

        int xtop = edges[1] == 0 ? (int) xAxis.getEdgeOffset() : 0;

            xAxis.layout(edges[0], xtop, edges[2], xtop + xAxis.getMeasuredHeight());

        for (PlotView pv : plots) {
            pv.layout(edges[0], edges[1], edges[2], edges[3]);
        }
        
        //layout preview if applicable
        if(previewBox.getVisibility() == VISIBLE)
        {
            int iWidth = edges[2] - edges[0];
            int mWidth = previewBox.getMeasuredWidth();
            int mHeight = previewBox.getMeasuredHeight();
            int pLeft = edges[0] + (iWidth - mWidth)/2;
            int pTop = edges[3] - (20 + mHeight);
            previewBox.layout(pLeft, pTop, pLeft + mWidth, pTop + mHeight);
            
        }
        

    }

    private void init() {
        
        setWillNotDraw(false);
        
        yAxis = new Axis(getContext());
        addView(yAxis, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        yAxis.setOrientation(LinearLayout.VERTICAL);
        xAxis = new Axis(getContext());
        addView(xAxis, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        xAxis.setOrientation(LinearLayout.HORIZONTAL);
        plots = new ArrayList<PlotView>();

        setOnTouchListener(new GraphGestureListener());

        //inflate and fetch preview text view
        inflate(getContext(), R.layout.graph_preview, this);
        previewBox = (TextView) getChildAt(getChildCount() - 1);
        previewBox.setVisibility(INVISIBLE);


    }

    /**
     * Takes into account internal edges and is only valid after layout
     * @return
     */
    public Point pixelsToCoordinate(Point pixel)
    {
        
        int internalEdges[] = internalEdges(getWidth(), getHeight());
        
        double cx = xAxis.coordinateFromPixel((float) (pixel.getX() - internalEdges[0]));
        double cy = yAxis.coordinateFromPixel((float) (pixel.getY() - internalEdges[1]));
        
        return new Point(cx, cy);
        
    }
    
    public Point pixelsFromCoordinate(Point coord)
    {
        int internalEdges[] = internalEdges(getWidth(), getHeight());
        
        float px = xAxis.pixelsFromCoordinate(coord.getX());
        float py = yAxis.pixelsFromCoordinate(coord.getY());
        
        return new Point(px + internalEdges[0], py + internalEdges[1]);
    }

    private boolean updatePreview(float x, float y)
    {
        
        Point coordinates = pixelsToCoordinate(new Point(x, y));

        ArrayList<Point> closestPoints = new ArrayList<Point>();
        
        for(PlotView plot : plots)
        {
            
            Point nearest = plot.findNearestPoint(coordinates.getX(), coordinates.getY());
            if (nearest != null)
            {
                closestPoints.add(nearest);
            }
            
        }
        boolean wasVisible = previewPoint != null;
        previewPoint = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for(Point p : closestPoints)
        {
            Point pix = pixelsFromCoordinate(p);
            double dipDistance = Math.sqrt(Math.pow(pix.getX() - x, 2) + Math.pow(pix.getY() - y, 2));
            if(dipDistance < minDistance && dipDistance < previewThreshold)
            {
                previewPoint = p;
            }
        }
        
        
        if(previewPoint != null)
        {
            previewBox.setVisibility(VISIBLE);
            previewBox.setText(previewFormat(previewPoint));
        }
        else
        {
            previewBox.setVisibility(INVISIBLE);
        }
        
        boolean changed = previewPoint != null || wasVisible;
        
        if(changed)
        {
            requestLayout();
            invalidate();
        }

        
        return changed;
    }
    
    private String numFormat(double val)
    {
        if ((val <= 1000 && val >= -1000 && Math.abs(val) > 1. / 1000.) || val == 0)
        {
            DecimalFormat format = new DecimalFormat("#.##");
            return format.format(val);
        }

        DecimalFormat format = new DecimalFormat("#.##E00");
        return format.format(val);
    }
    
    private String previewFormat(Point p){
        return "(" + numFormat(p.getX()) + ", " + numFormat(p.getY()) + ")";
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);    
        
        
        
        if(previewBox.getVisibility() == VISIBLE)
        {
            int dashlength = 10;
            
            
            int edges[] = internalEdges(getWidth(), getHeight());
            //draw dashed lines to axes
            int xp1 = yAxis.getDrawnAxisOffset();
            int yp1 = xAxis.getDrawnAxisOffset();
            Point pix = pixelsFromCoordinate(previewPoint);
            int xp2 = (int) pix.getX();
            int yp2 = (int) pix.getY();
            int startx = Math.min(xp1, xp2), endx = Math.max(xp1, xp2);
            int starty = Math.min(yp1, yp2), endy = Math.max(yp1, yp2);

            Paint p = new Paint();
            p.setStrokeWidth(1.8f);
            p.setARGB(255, 180, 180, 180);
            
            
            for(int i = startx; i < endx; i+= 2*dashlength)
            {
                canvas.drawLine(i, yp2, Math.min(i + dashlength, endx), yp2, p);
            }

            for(int i = starty; i < endy; i+= 2*dashlength)
            {
                canvas.drawLine(xp2, i, xp2, Math.min(i + dashlength, endy), p);
            }
            p.setStrokeWidth(2.5f);
            p.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(xp2, yp2, 5f, p);



            //redraw preview box at the end so that it is on top of lines
            previewBox.bringToFront();
        }
        
        
    }

    private class GraphGestureListener extends EpicTouchListener {


        

        

        @Override
        protected void onSingleFingerDown(float x, float y)
        {
//            updatePreview(x, y);
            onScale(1.1f, 1.1f, getWidth()/2, getHeight()/2);
        }

        @Override
        public boolean onSingleFingerMove(float distanceX, float distanceY, float finalX, float finalY)
        {
            return updatePreview(finalX, finalY);
        }
        
        
        
        @Override
        public boolean onTwoFingerSwipe(float distanceX, float distanceY) {

            double ymin = yAxis.coordinateFromPixel(yAxis.getHeight() - distanceY);
            double xmin = xAxis.coordinateFromPixel(-distanceX);


            double ymax = yAxis.coordinateFromPixel(-distanceY);
            double xmax = xAxis.coordinateFromPixel(xAxis.getWidth() - distanceX);
            
            //little hack to make sure there is no scaling going on during panning of a linear axis
            if(xAxis.getScaleType() == Axis.LINEARSCALE)
            {
                double minshift = xmin - xAxis.getMinValue();
                double maxshift = xmax - xAxis.getMaxValue();
                double avg = (minshift + maxshift) / 2;
                xmin = xAxis.getMinValue() + avg;
                xmax = xAxis.getMaxValue() + avg;
            }
            if(yAxis.getScaleType() == Axis.LINEARSCALE)
            {
                double minshift = ymin - yAxis.getMinValue();
                double maxshift = ymax - yAxis.getMaxValue();
                double avg = (minshift + maxshift) / 2;
                ymin = yAxis.getMinValue() + avg;
                ymax = yAxis.getMaxValue() + avg;


            }

            xAxis.setRange(xmin, xmax);
            yAxis.setRange(ymin, ymax);
            
            requestLayout();
            invalidate();
            return true;
        }


        @Override
        protected boolean onScale(float xFactor, float yFactor, float xCenter, float yCenter) {

            int []edges = internalEdges(getWidth(), getHeight());
            xCenter -= edges[0];
            yCenter -= edges[1];
            if(xCenter < 0) xCenter = 0;
            if(yCenter < 0) yCenter = 0;


            double xMinSize = xCenter/xAxis.getWidth() * xAxis.getWidth();
            double xMaxSize = xAxis.getWidth() - xMinSize;
            double yMaxSize = yCenter/yAxis.getHeight() * yAxis.getHeight();
            double yMinSize = yAxis.getHeight() - yMaxSize;

            double xMin = xAxis.coordinateFromPixel((float) (xCenter - xFactor * xMinSize));
            double xMax = xAxis.coordinateFromPixel((float) (xCenter + xFactor * xMaxSize));
            double yMin = yAxis.coordinateFromPixel((float) (yCenter + yFactor * yMinSize));
            double yMax = yAxis.coordinateFromPixel((float) (yCenter - yFactor * yMaxSize));

            if(xFactor != 1)xAxis.setRange(xMin, xMax);
            if(yFactor != 1)yAxis.setRange(yMin, yMax);

            requestLayout();
            invalidate();

            return true;
        }
    }

    public void beginEdit()
    {
        deferInvalidate = true;
    }

    public void endEdit()
    {
        deferInvalidate = false;
        requestLayout();
        invalidate();
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
        
        if(!deferInvalidate)
        {
            requestLayout();
            invalidate();
        }

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

    
    public void setXLogScale(boolean islogscale)
    {
        int newscale = islogscale? Axis.LOGSCALE : Axis.LINEARSCALE;
        if(xAxis.getScaleType() != newscale)
        {
            xAxis.setScaleType(newscale);
            if(!deferInvalidate)
            {
                requestLayout();
                invalidate();
            }
            
        }
    }

    public void setYLogScale(boolean islogscale)
    {
        int newscale = islogscale? Axis.LOGSCALE : Axis.LINEARSCALE;
        if(yAxis.getScaleType() != newscale)
        {
            yAxis.setScaleType(newscale);
            if(!deferInvalidate)
            {
                requestLayout();
                invalidate();
            }
            
        }
    }

    public void setYRange(double min, double max) {
        yAxis.setRange(min, max);
        if(!deferInvalidate)
        {
            requestLayout();
            invalidate();
        }
        
    }

    public void setXRange(double min, double max) {
        xAxis.setRange(min, max);
        if(!deferInvalidate)
        {
            requestLayout();
            invalidate();
        }
        
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
