package sriracha.frontend.android.results;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import sriracha.frontend.android.results.functions.Function;
import sriracha.frontend.resultdata.Plot;
import sriracha.frontend.resultdata.Point;

public class PlotView extends FrameLayout
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
    
    private void init(){
        //always stretch PlotView to size of graph
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if(graph == null) graph = (Graph) getParent();
        color = Color.rgb(0,250,35);
    }

    public void setColor(int color)
    {
        this.color = color;
    }
    

    @Override
    protected void onDraw(Canvas canvas)
    {


        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(0.5f);
        
        int index = 0;
        while(plot.getPoint(index).getX() < graph.getXmin()) index ++;
        
        Point previous = null, p = null;
        
        while(plot.getPoint(index).getX() <= graph.getXmax()){
            
            p = plot.getPoint(index);
            
            double pX = p.getX(), pY = func == null ? p.getY() : func.evaluate(p.getY());
            
           
            
            if(pY < graph.getYmin() || pY > graph.getYmax() || previous == null) {
                previous = null;
                continue;
            }

            double prevX = previous.getX(), prevY = func == null ? previous.getY() : func.evaluate(previous.getY());
            
            float []start = graph.pixelsFromCoords(prevX, prevY);
            float []end = graph.pixelsFromCoords(pX, pY);
            
            

            if( Math.pow(end[0] - start[0], 2) + Math.pow(end[1] - start[1], 2) < 2) continue;
            
            
            canvas.drawLine(start[0], start[1], end[0], end[1], paint);




            previous = p;
            index++;
        }
        
    }
}
