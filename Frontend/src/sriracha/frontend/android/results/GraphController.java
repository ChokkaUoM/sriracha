package sriracha.frontend.android.results;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import sriracha.frontend.R;

public class GraphController extends FrameLayout implements View.OnClickListener
{
    private AxisController yController;

    private AxisController xController;

    private Graph graph;

    Button autoScale;


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
        autoScale = (Button) findViewById(R.id.auto_scale_btn);
        autoScale.setOnClickListener(this);
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

    @Override
    public void onClick(View view)
    {
        graph.autoScale();
    }
}
