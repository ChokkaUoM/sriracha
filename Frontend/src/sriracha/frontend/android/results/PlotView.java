package sriracha.frontend.android.results;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import sriracha.frontend.android.results.functions.Function;
import sriracha.frontend.resultdata.Plot;
import sriracha.frontend.resultdata.Point;

public class PlotView extends View
{
    private Plot plot;

    private Graph graph;

    private int color;

    public void setFunc(Function func)
    {
        this.func = func;
    }


    /**
     * range of the data in this plot
     *
     * @return new double[]{xmin, xmax, ymin, ymax}
     */
    public double[] getRange()
    {
        return plot.getRange();
    }


    private Function func;

    public PlotView(Graph graph, Context context)
    {
        super(context);
        this.graph = graph;
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        //  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        //plot is always child of graph and will always fill entire graph space
        setMeasuredDimension(width, height);
    }

    public PlotView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PlotView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        //always stretch PlotView to size of graph
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (graph == null) graph = (Graph) getParent();
        color = Color.rgb(0, 250, 35);
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public void setPlot(Plot plot)
    {
        this.plot = plot;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);

        int index = 0;
        while (index < plot.size() && plot.getPoint(index).getX() < graph.getXmin()) index++;

        Point previous = index == 0 ? null : plot.getPoint(index - 1), p = null;

        while (index < plot.size() && (index == 0 || plot.getPoint(index - 1).getX() <= graph.getXmax()))
        {
            p = plot.getPoint(index);

            double pX = p.getX(), pY = func == null ? p.getY() : func.evaluate(p.getY());


            if (previous == null || ((pY < graph.getYmin() || pY > graph.getYmax()) && (previous.getY() < graph.getYmin() || previous.getY() > graph.getYmax())))
            {
                previous = p;
                index++;
                continue;
            }


            double prevX = previous.getX(), prevY = func == null ? previous.getY() : func.evaluate(previous.getY());

            float[] start = graph.internalPixelsFromCoordinates(prevX, prevY);
            float[] end = graph.internalPixelsFromCoordinates(pX, pY);


            //don't draw points that are really close together
            if (Math.pow(end[0] - start[0], 2) + Math.pow(end[1] - start[1], 2) < 2)
            {
                index++;
                continue;
            }

            canvas.drawLine(start[0], start[1], end[0], end[1], paint);


            previous = p;
            index++;
        }

    }

    /**
     * Finds nearest point in plot to target point does not check non visible points
     *
     * @param xCoord x coordinate of target point
     * @param yCoord y coordinate of target point
     * @return closest point or null if none are currently visible
     */
    public Point findNearestPoint(double xCoord, double yCoord)
    {
        int index = 0;
        Point touch = new Point(xCoord, yCoord);
        double minDistance = Double.POSITIVE_INFINITY;
        //initialize to null
        Point closest = null;
        //skip points before visible range
        while (index < plot.size() && plot.getPoint(index).getX() < graph.getXmin()) index++;

        //for points inside visible range
        while (index < plot.size() && plot.getPoint(index).getX() <= graph.getXmax())
        {
            Point p = plot.getPoint(index);
            if (minDistance > touch.distance(p))
            {
                //use copy not actual point
                closest = new Point(p.getX(), p.getY());
                minDistance = touch.distance(closest);
            }
            index++;
        }

        return closest;
    }
}
