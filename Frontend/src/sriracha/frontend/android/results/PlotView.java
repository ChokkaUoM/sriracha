package sriracha.frontend.android.results;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

        Point previous = index == 0 ? null : plot.getPoint(index -1), p = null;

        while (index < plot.size() && plot.getPoint(index).getX() <= graph.getXmax())
        {

            p = plot.getPoint(index);

            double pX = p.getX(), pY = func == null ? p.getY() : func.evaluate(p.getY());

            
            
            if (previous == null)
            {
                //if at first node just skip
                if( index == 0){
                    previous = p;
                    index++;
                    continue;
                }
                //if current node outside of range
                if(pY < graph.getYmin() || pY > graph.getYmax()){
                    index ++;
                    continue;
                }
                
            }

            if ((pY < graph.getYmin() || pY > graph.getYmax()) && previous == null)
            {
                index++;
                previous = null;
                continue;

            }

            double prevX = previous.getX(), prevY = func == null ? previous.getY() : func.evaluate(previous.getY());

            float[] start = graph.pixelsFromCoords(prevX, prevY);
            float[] end = graph.pixelsFromCoords(pX, pY);


            //don't draw points that are really close together
            if (Math.pow(end[0] - start[0], 2) + Math.pow(end[1] - start[1], 2) < 2)
            {
                index++;
                continue;
            }

            canvas.drawLine(start[0], start[1], end[0], end[1], paint);


            previous = (pY < graph.getYmin() || pY > graph.getYmax()) ? null: p;
            index++;
        }

    }
}
