package sriracha.frontend.android.results;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import sriracha.frontend.R;

public class GraphController extends FrameLayout
{
    private AxisController yController;

    private AxisController xController;

    private Graph graph;


    public GraphController(Context context)
    {
        super(context);
//        init();
    }

    public GraphController(Context context, AttributeSet attrs)
    {
        super(context, attrs);
//        init();
    }

    public GraphController(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
//        init();
    }

    @Override
    protected void onFinishInflate()
    {
        init();
        super.onFinishInflate();
    }

    private void init()
    {
        inflate(getContext(), R.layout.results_graph_controller, this);
        yController = (AxisController) findViewById(R.id.yAxisCtl);
        xController = (AxisController) findViewById(R.id.xAxisCtl);

        xController.setTitle("X Axis");
        yController.setTitle("Y Axis");

    }


    public Graph getGraph()
    {
        return graph;
    }

    public void setGraph(Graph graph)
    {
        this.graph = graph;
        yController.setAxis(graph.yAxis);
        xController.setAxis(graph.xAxis);
    }
}
